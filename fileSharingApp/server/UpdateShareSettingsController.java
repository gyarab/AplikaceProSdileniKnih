package com.google.gwt.fileSharingApp.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.fileSharingApp.shared.Constants;
import com.google.gwt.thirdparty.json.JSONArray;
import com.google.gwt.thirdparty.json.JSONObject;

public class UpdateShareSettingsController extends CustomHttpServlet {
	private static final long serialVersionUID = -4713894511757397549L;
	private static final Logger logger = Logger.getLogger(UpdateShareSettingsController.class.getSimpleName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Update share settings: Server side call invoked");
	
		String status = null;
		AuthStatus authStatus = getAuthDetails(request);
		if(authStatus.isAuthenticated()) {
			logger.log(Level.INFO, "Update share settings: User authenticated");

			final String fileName = request.getParameter("fileName");
			final String userName = authStatus.getUserName();
			final boolean makePublic = Boolean.parseBoolean(request.getParameter("makePublic"));
			final String userToShareFileWith = request.getParameter("shareWith");
			logger.log(Level.INFO, String.format("Update share settings: FileStore.updateFileForUser parameters -> %s %s %s %s",
					fileName, userName, makePublic, userToShareFileWith));
			FileStore.updateFileForUser(fileName, userName, makePublic, userToShareFileWith);
			
			status = "Success";
		} else {
			logger.log(Level.WARNING, "Update share settings: User NOT authenticated");
			status = "Failure - User not authenticated";
		}
		buildSimpleJSONStatusResponse(response, status);
		logger.log(Level.INFO, "Update share settings: Response built, server side call finalizing");
	}

}
