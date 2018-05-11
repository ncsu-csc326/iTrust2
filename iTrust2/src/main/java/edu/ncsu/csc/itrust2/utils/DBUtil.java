package edu.ncsu.csc.itrust2.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * A bit of helper logic for interfacing with the DB manually rather than just
 * using Hibernate. This is necessary for the Spring security login process.
 * 
 * @author Kai Presler-Marshall
 *
 */
public class DBUtil {

    /**
     * DataSource used to enable iTrust2 to connect to the MySQL database. Uses
     * root username and empty password. Please note that this would be VERY bad
     * in production and this should not be emulated, but it makes it easier to
     * share among teammates and Jenkins.
     * 
     * @return data source
     */
    static public DataSource dataSource () {

        final Properties properties = new Properties();

        InputStream input = null;
        String url = null;
        String username = null;
        String password = null;

        try {
            final String filename = "db.properties";
            input = DBUtil.class.getClassLoader().getResourceAsStream( filename );
            if ( null != input ) {
                properties.load( input );
                url = properties.getProperty( "url" );
                username = properties.getProperty( "username" );
                password = properties.getProperty( "password" );
            }

        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
        finally {
            if ( null != input ) {
                try {
                    input.close();
                }
                catch ( final Exception e2 ) {
                    // Exception ignored
                }
            }
        }

        final DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName( "com.mysql.jdbc.Driver" );
        driverManagerDataSource
                .setUrl( null == url ? "jdbc:mysql://localhost:3306/iTrust2?createDatabaseIfNotExist=true" : url );
        driverManagerDataSource.setUsername( null == username ? "root" : username );
        driverManagerDataSource.setPassword( null == password ? "" : password );
        return driverManagerDataSource;
    }

    /**
     * Provices a connection to the db using the DataSource above. MAKE SURE TO
     * CLOSE THE CONNECTION WHEN YOU ARE DONE WITH IT.
     * 
     * @return database connection
     * @throws SQLException
     *             Getting a connection can throw a SQLException, but shouldn't
     */
    static public Connection getConnection () throws SQLException {
        final DataSource ds = DBUtil.dataSource();
        final java.sql.Connection conn = ds.getConnection();
        return conn;
    }

}
