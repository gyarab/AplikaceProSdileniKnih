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

public class RegisterController extends CustomHttpServlet {
	private static final long serialVersionUID = -3323062820027493924L;
	private static final Logger logger = Logger.getLogger(RegisterController.class.getSimpleName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "User Registration: Server side call invoked");

		// Simplification, we assume for each parameter to be present max. once
		final String username = request.getParameter("username");
		final String password = request.getParameter("password");
		
		String status = null;
		if (UserStore.addUser(username, password)) {
			status = "Success";
			logger.log(Level.INFO, String.format("User %s registered into the application", username));
		} else {
			status = "Failure";
			logger.log(Level.INFO, String.format("User %s registration failed", username));
		}
		buildSimpleJSONStatusResponse(response, status);
		logger.log(Level.INFO, "User Registration: Response built, server side call finalizing");
	}
}
