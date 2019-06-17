package edu.ncsu.csc.itrust2.utils;

import java.io.File;
import java.io.FileInputStream;
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
 * @author Andrew Hayes
 *
 *
 */
public class DBUtil {

    static private String url      = null;
    static private String username = null;
    static private String password = null;

    static {
        InputStream input = null;
        final Properties properties = new Properties();

        try {
            final String filename = "src/main/java/db.properties";
            final File initialFile = new File( filename );
            input = new FileInputStream( initialFile );
            properties.load( input );
            url = properties.getProperty( "url" );
            username = properties.getProperty( "username" );
            password = properties.getProperty( "password" );

        }
        catch ( final Exception e ) {
            e.printStackTrace();
            // The file couldn't be loaded
            // Set some default values and maybe we'll get lucky
            url = "jdbc:mysql://localhost:3306/iTrust2?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=EST";
            username = "root";
            password = "";
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
    }

    /**
     * DataSource used to enable iTrust2 to connect to the MySQL database. Uses
     * root username and empty password. Please note that this would be VERY bad
     * in production and this should not be emulated, but it makes it easier to
     * share among teammates and Jenkins.
     *
     * @return data source
     */
    static public DataSource dataSource () {
        final DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName( "com.mysql.jdbc.Driver" );
        driverManagerDataSource.setUrl( url );
        driverManagerDataSource.setUsername( username );
        driverManagerDataSource.setPassword( password );
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

    /**
     * Get the url found in db.properties
     *
     * @return url
     */
    static public String getUrl () {
        return url;
    }

    /**
     * Get the username found in db.properties
     *
     * @return username
     */
    static public String getUsername () {
        return username;
    }

    /**
     * Get the password found in db.properties
     *
     * @return password
     */
    static public String getPassword () {
        return password;
    }

}
