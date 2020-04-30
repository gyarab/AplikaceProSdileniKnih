package com.google.gwt.fileSharingApp.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.fileSharingApp.shared.Constants;

public class FileUploadController extends CustomHttpServlet {
	private static final long serialVersionUID = -5861065654119964996L;
	private static final Logger logger = Logger.getLogger(FileUploadController.class.getSimpleName());
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "File upload: Server side call invoked");
		
		String status = null;
		AuthStatus authStatus = getAuthDetails(request);
		if(authStatus.isAuthenticated()) {
			logger.log(Level.INFO, "File upload: User authenticated");
			final String fileName = request.getParameter("fileName");
			FileItem uploadItem = getFileItem(request);
			if (uploadItem != null && fileName != null && fileName.length() > 0) {
				// this is the directory where we need to write our file
				Path uploadDirPath = new File(
					String.format("%s%s", Constants.FILE_UPLOAD_DIR, authStatus.getUserName())).toPath();
				if (Files.isRegularFile(uploadDirPath)) {
					Files.deleteIfExists(uploadDirPath);
				}
				if (!Files.exists(uploadDirPath) ) {
					Files.createDirectories(uploadDirPath);
				}
				
				// this is the new file full path
				final String filePath = String.format("%s%s/%s", 
						Constants.FILE_UPLOAD_DIR, authStatus.getUserName(), fileName);
				
				// Try with resources
				try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
					final byte[] fileContents = uploadItem.get();
					outputStream.write(fileContents);
					FileStore.addFileForUser(fileName, authStatus.getUserName());
					status = "Success";
				} catch (IOException e) {
					logger.log(Level.SEVERE, "File upload: Failed to write file " + filePath);
					logger.log(Level.SEVERE, "File upload: Failure details: " + e.getMessage());
					status = "Failure - Failed to write file";
				}		
			} else {
				status = "Failure - Invalid inputs";
			}
		} else {
			logger.log(Level.INFO, "File upload: User NOT authenticated");
			status = "Failure - User not authenticated";
		}
		buildSimpleJSONStatusResponse(response, status);
		logger.log(Level.INFO, "File upload: Response built, server side call finalizing");
	}

	private FileItem getFileItem(HttpServletRequest request) {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField() && "uploadFormElement".equals(item.getFieldName())) {
					return item;
				}
			}
		} catch (FileUploadException e) {
			return null;
		}
		return null;
	}

}
