package jpa.dao;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

// Just a helper class
public class DAOHelper {
    private static final EntityManagerFactory emf;
    static {
        // emf will be created before the program starts.
        emf = Persistence.createEntityManagerFactory("SMS");
    }
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
