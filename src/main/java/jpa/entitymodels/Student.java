package jpa.entitymodels;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
// Commonly used queries. Using @NamedQuery also helps with performance.
@NamedQueries({
        @NamedQuery(name = "Student.getAll", query = "SELECT s FROM Student s"),
        @NamedQuery(name = "Student.getByEmail", query = "SELECT s FROM Student s WHERE s.email = :email"),
        @NamedQuery(name = "Student.getByEmailFetchCourses", query = "Select DISTINCT s FROM Student s JOIN FETCH s.courses courses WHERE s.email = :email"),
        @NamedQuery(name = "Student.validate", query = "SELECT s FROM Student s WHERE s.email = :email AND s.password = :password"),
})
public class Student implements Serializable {
    @Id
    private String email;
    private String name;
    private String password;
    @ManyToMany
    @JoinTable(
            // Add this constraint so that a student can only sign up for a class once.
            uniqueConstraints = {@UniqueConstraint(columnNames = {"Student_email", "courses_id"})}
    )
    List<Course> courses;

    public Student() {
    }

    public Student(String email, String name, String password, List<Course> courses) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.courses = courses;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getStudentName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<Course> getCourses() {
        return courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return email.equals(student.email) && Objects.equals(name, student.name) && Objects.equals(password, student.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, password);
    }

    @Override
    public String toString() {
        return "Student{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", courses=" + courses +
                '}';
    }
}
