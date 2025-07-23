package com.learning.demo.service;

import com.learning.demo.model.Course;
import com.learning.demo.model.Enrollment;
import com.learning.demo.model.Student;
import com.learning.demo.repository.CourseRepository;
import com.learning.demo.repository.EnrollmentRepository;
import com.learning.demo.repository.StudentRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Enrollment enroll(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (course.getEnrolledCount() >= course.getCapacity()) {
            throw new IllegalStateException("Course is full");
        }

        course.setEnrolledCount(course.getEnrolledCount() + 1);
        try {
            courseRepository.saveAndFlush(course); // optimistic locking
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            throw new IllegalStateException("Concurrent enrollment detected. Please try again.");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();
        enrollment = enrollmentRepository.save(enrollment);
        log.info("Enrolled event: studentId={}, courseId={}", studentId, courseId);
        return enrollment;
    }

    public Enrollment getEnrollment(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
    }
}
