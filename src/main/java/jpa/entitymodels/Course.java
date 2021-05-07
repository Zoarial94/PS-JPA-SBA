package jpa.entitymodels;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;
import java.util.Objects;

@Entity
// Commonly used queries. @NamedQuery also helps with performance.
@NamedQueries({
        @NamedQuery(name = "Course.getAll", query = "SELECT c FROM Course c"),
})
public class Course implements Serializable {
    @Id
    int id;
    String name;
    String instructor;

    public Course() {
    }

    public Course(int id, String name, String instructor) {
        this.id = id;
        this.name = name;
        this.instructor = instructor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return id == course.id && Objects.equals(name, course.name) && Objects.equals(instructor, course.instructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, instructor);
    }

    @Override
    public String toString() {
        return String.format("%-5s%-35s%-15s",id, name, instructor);
    }
}
