package com.google.gwt.fileSharingApp.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gwt.thirdparty.json.JSONArray;
import com.google.gwt.thirdparty.json.JSONObject;

public class UserAccountsController extends CustomHttpServlet {
	private static final long serialVersionUID = 6361394565229622849L;
	private static final Logger logger = Logger.getLogger(UserAccountsController.class.getSimpleName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "User accounts: Server side call invoked");

		AuthStatus authStatus = getAuthDetails(request);
		if (authStatus.isAuthenticated()) {
			logger.log(Level.INFO, "User accounts: User authenticated");
			try {
				JSONArray users = new JSONArray();
				UserStore.getUsers().stream().forEach( user -> {
					users.put(user);
				});

				String usersAsString = new JSONObject().
						put("users", users).
						toString();

				PrintWriter responseBody = response.getWriter();
				responseBody.print(usersAsString);
				responseBody.flush();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "User accounts: Failed to retrieve user list " + e.getMessage());
				buildSimpleJSONStatusResponse(response, "Failure - Failed to build the user list");
			}

		} else {
			logger.log(Level.WARNING, "User accounts: User NOT authenticated");
			buildSimpleJSONStatusResponse(response, "Failure - User not authenticated");
		}
		logger.log(Level.INFO, "User accounts: Response built, server side call finalizing");
	}

}
