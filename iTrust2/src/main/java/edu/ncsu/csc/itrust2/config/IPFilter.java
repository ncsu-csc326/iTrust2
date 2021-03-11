package edu.ncsu.csc.iTrust2.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import edu.ncsu.csc.iTrust2.services.security.LoginBanService;
import edu.ncsu.csc.iTrust2.services.security.LoginLockoutService;

public class IPFilter extends GenericFilterBean {

    @Autowired
    private LoginBanService     loginBanService;

    @Autowired
    private LoginLockoutService loginLockoutService;

    /*
     * Source for filter setup:
     * http://www.baeldung.com/spring-security-custom-filter
     */
    @Override
    public void doFilter ( final ServletRequest request, final ServletResponse response, final FilterChain chain )
            throws IOException, ServletException {
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String relative = httpRequest.getRequestURI().substring( httpRequest.getContextPath().length() );

        final String addr = request.getRemoteAddr();

        // Redirect all banned IPs to /login?ipbanned
        if ( loginBanService.isIPBanned( addr )
                && ( !relative.contains( "/login" ) || !httpRequest.getParameterMap().containsKey( "ipbanned" ) ) ) {
            httpRequest.getSession().invalidate();
            httpResponse.sendRedirect( httpRequest.getContextPath() + "/login?ipbanned" );
        }
        // redirect all locked out IPs to /login?iplocked
        else if ( loginLockoutService.isIPLocked( addr )
                && ( !relative.contains( "/login" ) || !httpRequest.getParameterMap().containsKey( "iplocked" ) ) ) {
            httpRequest.getSession().invalidate();
            httpResponse.sendRedirect( httpRequest.getContextPath() + "/login?iplocked" );

        }
        else {
            chain.doFilter( request, response );
        }

    }

}
