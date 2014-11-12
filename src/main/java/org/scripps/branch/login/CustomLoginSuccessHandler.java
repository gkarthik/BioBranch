package org.scripps.branch.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scripps.branch.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomLoginSuccessHandler.class);
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session != null) {
            String redirectUrl = (String) session.getAttribute("url_prior_login");
            if (redirectUrl != null && !redirectUrl.contains("/login")) {
                // we do not forget to clean this attribute from session
                session.removeAttribute("url_prior_login");
                LOGGER.debug(redirectUrl);
                // then we redirect
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            } else {
            	getRedirectStrategy().sendRedirect(request, response, "/datasets");
            }
        } else {
        	getRedirectStrategy().sendRedirect(request, response, "/datasets");
        }
    }
}
