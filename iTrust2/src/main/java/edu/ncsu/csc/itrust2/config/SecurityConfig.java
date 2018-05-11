package edu.ncsu.csc.itrust2.config;

import javax.servlet.Filter;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Class that manages which users are allowed to access the system and which
 * role they have. Different users are allowed to have different roles which can
 * be enforced by the different controllers to restrict access to only users of
 * the correct type.
 *
 *
 * @author Kai Presler-Marshall
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * DataSource used for connecting to and interacting with the database.
     */
    @Autowired
    DataSource dataSource;

    /**
     * Login configuration for iTrust2.
     *
     * @param auth
     *            AuthenticationManagerBuilder to use to configure the
     *            Authentication.
     * @throws Exception
     */
    @Autowired
    public void configureGlobal ( final AuthenticationManagerBuilder auth ) throws Exception {
        final JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> dbManager = auth.jdbcAuthentication();

        // User query enabled flag also checks for locked or banned users. The
        // FailureHandler then
        // determines if the DisabledUser Exception was due to ban, lockout, or
        // true disable.
        // POSSIBLE FUTURE CHANGE: Refactor the UserSource here along the lines
        // of this:
        // http://websystique.com/springmvc/spring-mvc-4-and-spring-security-4-integration-example/
        dbManager.dataSource( dataSource ).passwordEncoder( passwordEncoder() ).usersByUsernameQuery(
                "select username,password,enabled AND b.time IS NULL AND c.time IS NULL AS enabled  from Users AS u"
                        + " LEFT JOIN LoginBans AS b ON u.username = b.user_id "
                        + "LEFT JOIN LoginLockouts AS c ON u.username = c.user_id AND TIMESTAMPDIFF(MINUTE, c.time, NOW()) < 60 WHERE username = ?;" )
                .authoritiesByUsernameQuery( "select username,role from Users where username=?" );
        auth.authenticationEventPublisher( defaultAuthenticationEventPublisher() );

    }

    /**
     * Method responsible for the Login page. Can be extended to explicitly
     * override other automatic functionality as desired.
     */
    @Override
    protected void configure ( final HttpSecurity http ) throws Exception {

        final String[] patterns = new String[] { "/login*" };
        // Add filter for banned/locked IP
        /*
         * According to
         * https://docs.spring.io/spring-security/site/docs/current/apidocs/org/
         * springframework/security/config/annotation/web/builders/HttpSecurity.
         * html#addFilter-javax.servlet.Filter- ChannelProcessingFIlter is the
         * first filter processed, so this means the IP block will be the
         * absolute first Filter.
         */
        http.addFilterBefore( ipBlockFilter(), ChannelProcessingFilter.class );

        http.authorizeRequests().antMatchers( patterns ).anonymous().anyRequest().authenticated().and().formLogin()
                .loginPage( "/login" ).failureHandler( failureHandler() ).defaultSuccessUrl( "/" ).and().csrf()

                .csrfTokenRepository( CookieCsrfTokenRepository
                        .withHttpOnlyFalse() ); /*
                                                 * Credit to
                                                 * https://medium.com/spektrakel
                                                 * -blog/angular2-and-spring-a-
                                                 * friend-in-
                                                 * security-need-is-a-friend-
                                                 * against-csrf-indeed-
                                                 * 9f83eaa9ca2e and
                                                 * http://docs.spring.io/spring-
                                                 * security/site/docs/current/
                                                 * reference/
                                                 * html/csrf.html#csrf-cookie
                                                 * for information on how to
                                                 * make Angular work properly
                                                 * with CSRF protection
                                                 */

    }

    @Override
    public void configure ( final WebSecurity web ) throws Exception {
        // Allow anonymous access to the 3 mappings related to resetting a
        // forgotten password
        web.ignoring().antMatchers( "/api/v1/requestPasswordReset", "/api/v1/resetPassword/*", "/requestPasswordReset",
                "/resetPassword" );
    }

    /**
     * Bean used to generate a PasswordEncoder to hash the user-provided
     * password.
     *
     * @return The password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationEventPublisher used to assist with authentication
     *
     * @return The AuthenticationEventPublisher.
     */
    @Bean
    public DefaultAuthenticationEventPublisher defaultAuthenticationEventPublisher () {
        return new DefaultAuthenticationEventPublisher();
    }

    /**
     * Failure Handler used to track failed attempts to determine if a user or
     * IP needs to be banned.
     *
     * @return The AuthenticationFailureHandler
     */
    @Bean
    public SimpleUrlAuthenticationFailureHandler failureHandler () {
        return new FailureHandler();
    }

    /**
     * Servlet Filter used to redirect all requests from banned/locked IPs to
     * the appropriate pages.
     *
     * @return The IP FIlter
     */
    @Bean
    public Filter ipBlockFilter () {
        return new IPFilter();
    }
}
