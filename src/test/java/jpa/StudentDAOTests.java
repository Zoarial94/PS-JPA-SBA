package jpa;

import jpa.dao.CourseDAO;
import jpa.dao.StudentDAO;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StudentDAOTests {
    static StudentDAO studentDAO;
    static CourseDAO courseDAO;
    static String email = "aiannitti7@is.gd";
    static String password = "TWP4hf5j";

    @BeforeAll
    public static void begin() {
    studentDAO = new StudentDAO();
    courseDAO = new CourseDAO();
    }

    @Test
    @DisplayName("Successful Login")
    public void successfulLoginTest() {
        assertTrue(studentDAO.validateStudent("aiannitti7@is.gd", "TWP4hf5j"));
        assertTrue(studentDAO.validateStudent("cjaulme9@bing.com", "FnVklVgC6r6"));
    }

    @Test
    @DisplayName("Unsuccessful Login")
    public void unsuccessfulLoginTest() {
        // random username and password
        assertFalse(studentDAO.validateStudent("Someone@mail.com", "mypassword"));
        // actual username, but wrong password
        assertFalse(studentDAO.validateStudent("aiannitti7@is.gd", "notTheRightPassword"));
        // random username, but real password
        assertFalse(studentDAO.validateStudent("notTheRightUsername", "TWP4hf5j"));
        // real username and password, but not to each other
        assertFalse(studentDAO.validateStudent("cjaulme9@bing.com", "TWP4hf5j"));
    }

    @Test
    @DisplayName("Get Student By Email Without Fetching Courses")
    public void getStudentByEmailNoFetch() {
        assertTrue(studentDAO.validateStudent(email, password), "Student needs to be in database:\nEmail: " + email + "\nPassword: " + password);
        var students = studentDAO.getAllStudents();
        var student = studentDAO.getStudentByEmail(email, false);
        assertTrue(students.contains(student), "A student retrieved by email should be in the list of all students.");
        assertEquals(student.getEmail(), email);
        assertEquals(student.getPassword(), password);
        assertThrows(LazyInitializationException.class, student.getCourses()::size,
                "Hibernate should have lazy loading for collections for performance reasons.");
    }

    @Test
    @DisplayName("Get Student By Email With Fetching Courses")
    public void getStudentByEmailWithFetch() {
        assertTrue(studentDAO.validateStudent(email, password), "Student needs to be in database:\nEmail: " + email + "\nPassword: " + password);
        var students = studentDAO.getAllStudents();
        var courses = courseDAO.getAllCourses();
        var student = studentDAO.getStudentByEmail(email, true);
        assertTrue(students.contains(student), "A student retrieved by email should be in the list of all students.");
        assertEquals(student.getEmail(), email);
        assertEquals(student.getPassword(), password);
        assertDoesNotThrow(student.getCourses()::size,
                "Hibernate should have lazy loading for collections for performance reasons.");
        assertTrue(courses.containsAll(student.getCourses()),
                "All the courses a student signs up for, should be in the list of all courses.");
        assertArrayEquals(student.getCourses().toArray(), studentDAO.getStudentCourses(email).toArray(),
                "The list from the student, should be the same as the list directly from a query.");
    }
}
