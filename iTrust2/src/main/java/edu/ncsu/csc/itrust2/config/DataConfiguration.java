package edu.ncsu.csc.itrust2.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.ncsu.csc.itrust2.utils.DBUtil;

/**
 * Class that manages various aspects of the database connectivity used by
 * Spring and iTrust2
 *
 * @author Kai Presler-Marshall
 *
 */
@Configuration
public class DataConfiguration {

    /**
     * Spring Bean for the DataSource used to interact with the database.
     *
     * @return DataSource retrieved
     */
    @Bean
    public DataSource dataSource () {
        return DBUtil.dataSource();
    }

}
