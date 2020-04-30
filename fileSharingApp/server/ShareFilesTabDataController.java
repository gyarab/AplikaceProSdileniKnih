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

public class ShareFilesTabDataController extends CustomHttpServlet {
	private static final long serialVersionUID = 3472536747845438117L;
	private static final Logger logger = Logger.getLogger(ShareFilesTabDataController.class.getSimpleName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "ShareFilesTabDataController: Server side call invoked");

		AuthStatus authStatus = getAuthDetails(request);
		if (authStatus.isAuthenticated()) {
			logger.log(Level.INFO, "ShareFilesTabDataController: User authenticated");
			try {
				JSONArray userFiles = new JSONArray();
				FileStore.getUserFilesWithDetails(authStatus.getUserName()).forEach(file -> {
					userFiles.put(file);
				});
				JSONArray allUsers = new JSONArray();
				UserStore.getUsers().forEach(user -> {
					allUsers.put(user);
				});
				String userFilesAsString = new JSONObject().
						put("userFiles", userFiles).
						put("allUsers", allUsers).
						toString();

				PrintWriter responseBody = response.getWriter();
				responseBody.print(userFilesAsString);
				responseBody.flush();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "ShareFilesTabDataController: Failed to retrieve data and build response " + e.getMessage());
				buildSimpleJSONStatusResponse(response, "Failure - Failed to retrieve data and build response");
			}

		} else {
			logger.log(Level.WARNING, "ShareFilesTabDataController: User NOT authenticated");
			buildSimpleJSONStatusResponse(response, "Failure - User not authenticated");
		}
		logger.log(Level.INFO, "ShareFilesTabDataController: Response built, server side call finalizing");
	}

}
