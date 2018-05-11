package edu.ncsu.csc.itrust2.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import edu.ncsu.csc.itrust2.models.persistent.LoginBan;
import edu.ncsu.csc.itrust2.models.persistent.LoginLockout;

/**
 * Custom Http Filter to redirect all requests from banned or locked out IP
 * addresses.
 *
 * @author Thomas
 *
 */
public class IPFilter extends GenericFilterBean {
    /*
     * Source for filter setup:
     * http://www.baeldung.com/spring-security-custom-filter
     */
    // Handle IP locking/banning here
    @Override
    public void doFilter ( final ServletRequest request, final ServletResponse response, final FilterChain chain )
            throws IOException, ServletException {
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String relative = httpRequest.getRequestURI().substring( httpRequest.getContextPath().length() );

        final String addr = request.getRemoteAddr();

        // Redirect all banned IPs to /login?ipbanned
        if ( LoginBan.isIPBanned( addr )
                && ( !relative.contains( "/login" ) || !httpRequest.getParameterMap().containsKey( "ipbanned" ) ) ) {
            httpRequest.getSession().invalidate();
            httpResponse.sendRedirect( httpRequest.getContextPath() + "/login?ipbanned" );
        }
        // redirect all locked out IPs to /login?iplocked
        else if ( LoginLockout.isIPLocked( addr )
                && ( !relative.contains( "/login" ) || !httpRequest.getParameterMap().containsKey( "iplocked" ) ) ) {
            httpRequest.getSession().invalidate();
            httpResponse.sendRedirect( httpRequest.getContextPath() + "/login?iplocked" );

        }
        else {
            chain.doFilter( request, response );
        }

    }
}
