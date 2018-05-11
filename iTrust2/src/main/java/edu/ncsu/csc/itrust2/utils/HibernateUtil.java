package edu.ncsu.csc.itrust2.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * A utility class for setting up the Hibernate SessionFactory
 *
 * @author Elizabeth Gilbert
 */
public class HibernateUtil {

    /**
     * SeesionFactory used
     */
    private static SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Creates a SessionFactory
     *
     * @return SessioNFactory that was built
     */
    private static SessionFactory buildSessionFactory () {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        }
        catch ( final Throwable ex ) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println( "Initial SessionFactory creation failed." + ex );
            throw new ExceptionInInitializerError( ex );
        }
    }

    /**
     * Retrieves the SessionFactory generated
     *
     * @return sessionFactory that was generated
     */
    public static SessionFactory getSessionFactory () {
        return sessionFactory;
    }

    /**
     * Close the SessionFactory
     */
    public static void shutdown () {
        // Close caches and connection pools
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }
}
