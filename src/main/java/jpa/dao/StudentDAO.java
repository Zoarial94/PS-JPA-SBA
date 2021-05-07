package jpa.dao;

import jpa.entitymodels.Course;
import jpa.entitymodels.Student;
import jpa.service.StudentService;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements StudentService {
    private final EntityManagerFactory emf;

    public StudentDAO() {
        emf = DAOHelper.getEntityManagerFactory();
    }

    @Override
    public Student getStudentByEmail(String email) {
        return getStudentByEmail(email, false);
    }

    /**
     * @param fetchCourses determines whether to pre-fetch the student's courses.
     *              The courses are lazily loaded by hibernate by default
     *              for performance reasons.
     *
     *              If fetch is true, then getCourses is (supposed to)
     *              return a list that won't throw when accessed.
     *
     *              If fetch is false, then getCourses will return a list
     *              that throws.
     *
     * @return A Student instance, or null if there is no student.
     *
     */
    public Student getStudentByEmail(String email, boolean fetchCourses) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Student> q;
        // If fetch is true, then use the corresponding query.
        // See the Student class to see the queries.
        if (fetchCourses) {
            q = em.createNamedQuery("Student.getByEmailFetchCourses", Student.class);
        } else {
            q = em.createNamedQuery("Student.getByEmail", Student.class);
        }

        q.setParameter("email", email);

        List<Student> results = q.getResultList();
        em.close();

        // With fetch, it may return nothing if there is nothing to fetch
        // Even if there is a student with that email.
        // This will return the student and then we'll add the
        // courses afterwards. (Which is just an empty list
        // since it wasn't already returned).
        if(results.size() == 0 && fetchCourses) {
            Student tmp = getStudentByEmail(email, false);
            if(tmp != null) {
                tmp.setCourses(new ArrayList<>());
            }
            return tmp;
        }

        // If we didn't get one result, then we need to log it.
        // And to be safe, return no student object.
        // We don't want the wrong person getting access to an account.
        if(results.size() != 1) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Student> q = em.createNamedQuery("Student.getAll", Student.class);

        List<Student> results = q.getResultList();
        em.close();
        return results;
    }

    @Override
    public boolean validateStudent(String email, String password) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Student> q = em.createNamedQuery("Student.validate", Student.class);

        q.setParameter("email", email);
        q.setParameter("password", password);

        List<Student> results = q.getResultList();
        em.close();
        return results.size() == 1;
    }

    @Override
    public List<Course> getStudentCourses(String email) {
        // Use fetch since we already know we want the courses.
        Student student = getStudentByEmail(email, true);

        if(student == null) {
            System.out.println("There's no student with that email.");
            return null;
        }

        return student.getCourses();
    }

    @Override
    public void registerStudentToCourse(String email, int courseId) {
        // Use fetch since we already know we want the courses.
        Student student = getStudentByEmail(email, true);

        if(student == null) {
            System.out.println("There is no student with that email.");
            return;
        }

        EntityManager em = emf.createEntityManager();

        // Check all the course and make sure the request course
        // is a real course.
        TypedQuery<Course> q = em.createNamedQuery("Course.getAll", Course.class);
        var courses = q.getResultList();
        Course course = null;
        for(Course c : courses) {
            if(c.getId() == courseId) {
                course = c;
                break;
            }
        }
        // Course id doesn't exist
        if (course == null) {
            System.out.println("No course with that id was found.");
            return;
        }

        // Student is already signed up to that course.
        if (student.getCourses().contains(course)) {
            System.out.println("Already assigned to this course.");
            return;
        }


        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // The student object at this point is detached
            // merge returns a re-attached object.
            // This ensures when we add the course, that it
            // gets propagated to the database.
            student = em.merge(student);
            student.getCourses().add(course);
            transaction.commit();
        } catch (EntityExistsException ex) {
            ex.printStackTrace();
            System.out.println("Student already exists in the database.");
        } catch (RollbackException ex) {
            ex.printStackTrace();
            System.out.println("Could not commit action to database.");
        } finally {
            em.close();
        }
    }
}
