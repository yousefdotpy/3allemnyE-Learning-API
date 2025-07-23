# Course Management & Enrollment API

This is a Spring Boot application for managing courses and enrolling students, with concurrency-safe enrollment and simple caching.

## Features
- **Course Management**: Create and list courses (title, capacity, current enrolled count).
- **Student Enrollment**: Students enroll in courses via API. Enrollment is atomic and prevents overbooking even under concurrent requests.
- **View Enrollment**: Fetch details of a single enrollment.
- **Error Handling**: Graceful error responses for full courses and invalid data.
- **Layered Architecture**: Clear separation of controller, service, repository, and model layers.
- **Caching**: Simple caching for the course list.
- **Event Logging**: Logs an "enrolled" event.
- **Tests**: (To be added) Tests to prove concurrency handling.

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Running the Application

```
mvn spring-boot:run
```

The app will start on `http://localhost:8080`.

### API Endpoints

#### Courses
- `GET /api/courses` — List all courses
- `POST /api/courses` — Create a course (JSON body: `{ "title": "Math", "capacity": 30 }`)

#### Students
- `POST /api/students` — Create a student (JSON body: `{ "name": "John Doe" }`) (to be added)

#### Enrollment
- `POST /api/enrollments?studentId=1&courseId=1` — Enroll a student in a course
- `GET /api/enrollments/{id}` — Get enrollment details

### Error Responses
- 400 Bad Request: Invalid data or missing student/course
- 409 Conflict: Course is full or concurrent overbooking attempt

### H2 Console
Access the in-memory database at: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`)

### Caching
Course list endpoint (`GET /api/courses`) is cached.

### Logging
Enrollment events are logged to the application log.

### Tests
Tests for concurrency and API correctness are to be added in `src/test/java`.

---

Feel free to extend the application as needed!
