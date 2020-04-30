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

public class LogoutController extends CustomHttpServlet {
	private static final long serialVersionUID = -7908293877089927477L;
	private static final Logger logger = Logger.getLogger(LogoutController.class.getSimpleName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Logout: Server side call invoked");
		addEmptyAuthCookie(response);
		final String status = "Success";
		buildSimpleJSONStatusResponse(response, status);
		logger.log(Level.INFO, "Logout: Response built, server side call finalizing");
	}

	private void addEmptyAuthCookie(final HttpServletResponse response) {
		final Cookie sessionIdCookie = new Cookie(Constants.SESSION_ID_COOKIE, null);
		sessionIdCookie.setMaxAge(0);
		response.addCookie(sessionIdCookie);
	}
}
