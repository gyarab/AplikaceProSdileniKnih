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

public class AccessibleFilesController extends CustomHttpServlet {
	private static final long serialVersionUID = -8875087326280081648L;
	private static final Logger logger = Logger.getLogger(AccessibleFilesController.class.getSimpleName());
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "User Accessible files: Server side call invoked");
		
		String status = null;
		AuthStatus authStatus = getAuthDetails(request);
		if(authStatus.isAuthenticated()) {
			logger.log(Level.INFO, "User Accessible files: User authenticated");
			try {				
				JSONObject userAccessibleFiles = new JSONObject();
				
				JSONArray userFiles = new JSONArray();				
				FileStore.getUserFiles(authStatus.getUserName()).forEach( file -> {
					userFiles.put(file);
				});				
				userAccessibleFiles.put("userFiles", userFiles);

				JSONArray sharedFiles = new JSONArray();				
				FileStore.getFilesSharedWithUser(authStatus.getUserName()).forEach( file -> {
					sharedFiles.put(file);
				});				
				userAccessibleFiles.put("sharedFiles", sharedFiles);

				JSONArray publicFiles = new JSONArray();				
				FileStore.getPublicFiles().forEach( file -> {
					publicFiles.put(file);
				});
				userAccessibleFiles.put("publicFiles", publicFiles);
				
				String userAccessibleFilesAsString = userAccessibleFiles.toString();
				
				PrintWriter responseBody = response.getWriter();
				responseBody.print(userAccessibleFilesAsString);
				responseBody.flush(); 
			} catch (Exception e) {
				logger.log(Level.SEVERE, "User Accessible files: Failed to retrieve the file " + e.getMessage());
				buildSimpleJSONStatusResponse(response, "Failure - Failed to build the user file list");
			}
			

		} else {
			logger.log(Level.WARNING, "User Accessible files: User NOT authenticated");
			buildSimpleJSONStatusResponse(response, "Failure - User not authenticated");
		}
		logger.log(Level.INFO, "User Accessible files: Response built, server side call finalizing");
	}

}
