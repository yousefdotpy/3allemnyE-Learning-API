package com.learning.demo;

import com.learning.demo.model.Course;
import com.learning.demo.model.Enrollment;
import com.learning.demo.model.Student;
import com.learning.demo.repository.CourseRepository;
import com.learning.demo.repository.EnrollmentRepository;
import com.learning.demo.repository.StudentRepository;
import com.learning.demo.service.EnrollmentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class EnrollmentConcurrencyTest {
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Course course;
    private List<Student> students;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        course = courseRepository.save(Course.builder().title("Physics").capacity(5).enrolledCount(0).build());
        students = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            students.add(studentRepository.save(Student.builder().name("Student" + i).build()));
        }
    }

    @Test
    void testConcurrentEnrollment() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Long> enrollmentIds = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    Enrollment enrollment = enrollmentService.enroll(students.get(idx).getId(), course.getId());
                    synchronized (enrollmentIds) {
                        enrollmentIds.add(enrollment.getId());
                    }
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();
        Assertions.assertEquals(5, enrollmentIds.size());
        Course updated = courseRepository.findById(course.getId()).orElseThrow();
        Assertions.assertEquals(5, updated.getEnrolledCount());
    }
}
