package com.google.gwt.fileSharingApp.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gwt.fileSharingApp.shared.Constants;


public class DownloadFileController extends CustomHttpServlet {
	private static final long serialVersionUID = 4352609264735177653L;
	private static final Logger logger = Logger.getLogger(DownloadFileController.class.getSimpleName());
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Download file: Server side call invoked");	
		AuthStatus authStatus = getAuthDetails(request);
		if(authStatus.isAuthenticated()) {
			logger.log(Level.INFO, "Download file: User authenticated");
			// the request processed by this servlet is like
			// http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName=notes.txt
			// the line below is to get the fileName query string parameter value
			// (query string is fileName=notes.txt in this sample case)
			final String fileName = request.getParameter("fileName"); 	// e.g. obrazek.jpg
			final String sharedBy = request.getParameter("sharedBy"); 	// e.g. user1
			try {
				final String filePath = String.format("%s%s/%s", 
						Constants.FILE_UPLOAD_DIR, sharedBy, fileName);
				File file = new File(filePath);
				final byte[] fileContents = Files.readAllBytes(file.toPath());
				response.setContentLength((int) file.length());
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				// writing response body
				ServletOutputStream responseBody = response.getOutputStream();
				responseBody.write(fileContents);
				responseBody.flush(); 
				
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Download file: Failed to retrieve the file " + e.getMessage());
				buildSimpleJSONStatusResponse(response, "Failure - Failed to retrieve the file");
			} 
			
		} else {
			logger.log(Level.WARNING, "Download file: User NOT authenticated");
			buildSimpleJSONStatusResponse(response, "Failure - User not authenticated");
		}
		logger.log(Level.INFO, "Download file: Response built, server side call finalizing");
	}
}
