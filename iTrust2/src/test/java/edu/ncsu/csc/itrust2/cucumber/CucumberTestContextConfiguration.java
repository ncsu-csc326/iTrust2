package edu.ncsu.csc.iTrust2.cucumber;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import edu.ncsu.csc.iTrust2.common.DBUtils;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
public class CucumberTestContextConfiguration {

    @Autowired
    protected DataSource dataSource;

    @Before
    public void beforeTests () {
        DBUtils.resetDB( dataSource );
    }

}
