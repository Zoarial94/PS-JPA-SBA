package jpa.service;

import jpa.entitymodels.Course;
import jpa.entitymodels.Student;

import java.util.List;

public interface StudentService {

    Student getStudentByEmail(String email);

    List<Student> getAllStudents();

    boolean validateStudent(String email, String password);

    void registerStudentToCourse(String email, int courseId);

    List<Course> getStudentCourses(String email);
}
