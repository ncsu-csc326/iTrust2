package edu.ncsu.csc.iTrust2.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Assert;

public class DBUtils {

    static public void resetDB ( final DataSource dataSource ) {

        try ( Connection conn = dataSource.getConnection(); ) {

            final DatabaseMetaData metaData = conn.getMetaData();
            String dbName = metaData.getURL();

            /*
             * DB URL looks something like
             * `jdbc:mysql://localhost:3306/iTrust2_test?
             * createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true`, so
             * it has to be pulled apart to get the actual name out
             */
            dbName = dbName.split( "/" )[3].split( "\\?" )[0];

            final ResultSet tables = metaData.getTables( dbName, null, null, new String[] { "TABLE" } );

            try ( Statement st = conn.createStatement(); ) {

                st.executeUpdate( "SET FOREIGN_KEY_CHECKS = 0" );

                while ( tables.next() ) {
                    final String tableName = tables.getString( "TABLE_NAME" );

                    /*
                     * If you delete all entries from Hibernate's table it gets
                     * very unhappy
                     */
                    if ( "hibernate_sequence".equals( tableName ) ) {
                        continue;
                    }

                    st.executeUpdate( "TRUNCATE TABLE " + tableName );
                }

                st.executeUpdate( "SET FOREIGN_KEY_CHECKS = 1" );

            }
        }
        catch ( final Exception e ) {
            System.err.println( "Something bad appears to have happened while preparing environment " + e.getClass() );
            // e.printStackTrace( System.err );
            Assert.fail();
        }
    }

}
