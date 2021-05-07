package jpa.dao;

import jpa.entitymodels.Course;
import jpa.service.CourseService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class CourseDAO implements CourseService {
    final private EntityManagerFactory emf;

    public CourseDAO() {
        emf = DAOHelper.getEntityManagerFactory();
    }

    // Check the Course class for the actual queries.
    @Override
    public List<Course> getAllCourses() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Course> q = em.createNamedQuery("Course.getAll", Course.class);
        return q.getResultList();
    }

}
