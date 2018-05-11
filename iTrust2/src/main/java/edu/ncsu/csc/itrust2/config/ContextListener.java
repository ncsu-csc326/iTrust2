package edu.ncsu.csc.itrust2.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import edu.ncsu.csc.itrust2.utils.HibernateUtil;

/**
 * Simple listener that can bind actions to startup or shutdown of the web
 * application server. Used to close the database connection pool when
 * everything is finished.
 *
 * @author Kai Presler-Marshall
 *
 */
@WebListener
public class ContextListener implements ServletContextListener {

    /**
     * Gracefully tell Hibernate to close the connections to the database rather
     * than dropping everything on the floor.
     */
    @Override
    public void contextDestroyed ( final ServletContextEvent arg0 ) {
        HibernateUtil.shutdown();
    }

    @Override
    public void contextInitialized ( final ServletContextEvent arg0 ) {
        // There's nothing that we need to do here
    }

}
