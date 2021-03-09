package edu.ncsu.csc.itrust2.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import edu.ncsu.csc.itrust2.models.persistent.User;
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

	static private Thread dbThread = new Thread(new Runnable() {
		@Override
		public void run() {
			for (;;) {
				User.getByName("Wladyslaw Jagiello"); // doesn't matter if they exist or not

				try {
					Thread.sleep(3 * 60 * 1000); /* 3 minutes to milliseconds */
				} catch (final InterruptedException e) {
					// Exception ignored
				}
			}
		}
	});

	/**
	 * Gracefully tell Hibernate to close the connections to the database rather
	 * than dropping everything on the floor.
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent arg0) {
		HibernateUtil.shutdown();

		dbThread.stop();
	}

	@Override
	public void contextInitialized(final ServletContextEvent arg0) {

		dbThread.setName("DBKeepAlive_Thread");
		dbThread.setPriority(Thread.MIN_PRIORITY);
		dbThread.setDaemon(true);
		dbThread.start();
	}

}
