package com.google.gwt.fileSharingApp.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.fileSharingApp.shared.Constants;

public class LoginController extends CustomHttpServlet {
	private static final long serialVersionUID = -2306304458652996753L;
	private static final Logger logger = Logger.getLogger(LoginController.class.getSimpleName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Login: Server side call invoked");

		// Simplification, we assume for each parameter to be present max. once
		final String username = request.getParameter("username");
		final String password = request.getParameter("password");
		
		String status = null;
		if (UserStore.isValidCredentials(username, password)) {
			// user is authenticated
			// no session expiration/timeouts considered in this prototype implementation
			// store new authentication token in session store and configure response so that 
			// the session id is stored in the cookie in the user browser and passed back 
			// with following requests
			final String sessionId = SessionStore.addNewSessionIdForUser(username);
			addAuthSuccessCookie(response, sessionId);
			status = "Success";
			LoginController.logger.log(Level.INFO, String.format("User %s authentication success", username));
		} else {
			// user authentication failed
			LoginController.logger.log(Level.WARNING, String.format("User %s authentication failure", username));
			addAuthFailureCookie(response);
			status = "Failure";
		}
		buildSimpleJSONStatusResponse(response, status);
		logger.log(Level.INFO, "Login: Response built, server side call finalizing");
	}

	private void addAuthSuccessCookie(final HttpServletResponse response, final String sessionId) {
		final Cookie sessionIdCookie = new Cookie(Constants.SESSION_ID_COOKIE, sessionId);
		response.addCookie(sessionIdCookie);
	}

	private void addAuthFailureCookie(final HttpServletResponse response) {
		final Cookie sessionIdCookie = new Cookie(Constants.SESSION_ID_COOKIE, null);
		sessionIdCookie.setMaxAge(0);
		response.addCookie(sessionIdCookie);
	}
}
