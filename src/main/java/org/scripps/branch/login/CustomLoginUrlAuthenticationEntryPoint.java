package org.scripps.branch.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;


public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomLoginUrlAuthenticationEntryPoint.class);
	
	public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl){
		super(loginFormUrl);
	}
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
	    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	    String referrer = request.getRequestURL()+"?"+request.getQueryString();
		LOGGER.debug(referrer);
		if (referrer != null && !referrer.contains("login") && !referrer.contains("datasets")) {
			request.getSession().setAttribute("url_prior_login", referrer);
		}
	    redirectStrategy.sendRedirect(request, response, "/login?error=access_denied");
	}
}
