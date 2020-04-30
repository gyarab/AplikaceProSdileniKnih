package com.google.gwt.fileSharingApp.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileStore {
	  private static final Logger logger = Logger.getLogger(FileStore.class.getSimpleName());
	  private static Map<String, ArrayList<UserFile>> fileStoreDB;
	  static {
		  fileStoreDB = new HashMap<String, ArrayList<UserFile>>();
	  } 
	  
	  
	  public static List<FileSharedBy> getUserFiles(final String userName) {
		  ArrayList<UserFile> userFiles = fileStoreDB.get(userName);
		  if (userFiles == null) {
			  return new ArrayList<FileSharedBy>();
		  }
		  // return only filename and owner data
		  ArrayList<FileSharedBy> sharedFiles = new ArrayList<FileSharedBy>();
		  userFiles.stream().forEach( userFile -> {
			  sharedFiles.add(new FileSharedBy(userFile.getFileName(), userFile.getOwner()));
		  });	  
		  return sharedFiles;
	  }
	  
	  public static List<UserFile> getUserFilesWithDetails(final String userName) {
		  ArrayList<UserFile> userFiles = fileStoreDB.get(userName);
		  return userFiles != null ? userFiles : new ArrayList<UserFile>();
	  }
	  
	  public static List<FileSharedBy> getFilesSharedWithUser(final String userName) {
		  ArrayList<FileSharedBy> sharedFiles = new ArrayList<FileSharedBy>();
		  // Get all users that have their files uploaded in the system
		  Set<String> users = getUsersStoringFiles();
		  if (!users.isEmpty()) {
			  users.forEach( user -> {
				  ArrayList<UserFile> userFiles = fileStoreDB.get(user);
				  if (userFiles != null) {
					  // user does have some files store/uploaded already
					  userFiles.stream().forEach( userFile -> {
						  if (userFile.isSharedWithUser(userName)) {
							  sharedFiles.add(new FileSharedBy(userFile.getFileName(), userFile.getOwner()));
						  }
					  });
				  } 
			  });
		  }
		  return sharedFiles;
	  }
	  
	  public static List<FileSharedBy> getPublicFiles() {
		  ArrayList<FileSharedBy> sharedFiles = new ArrayList<FileSharedBy>();
		  // Get all users that have their files uploaded in the system
		  Set<String> users = getUsersStoringFiles();
		  if (!users.isEmpty()) {
			  users.forEach( user -> {
				  ArrayList<UserFile> userFiles = fileStoreDB.get(user);
				  if (userFiles != null) {
					  // user does have some files store/uploaded already
					  userFiles.stream().forEach( userFile -> {
						  if (userFile.isPublic()) {
							  sharedFiles.add(new FileSharedBy(userFile.getFileName(), userFile.getOwner()));
						  }
					  });
				  } 
			  });
			  

		  }
		  return sharedFiles;
	  }  
	  
	  public static void addFileForUser(final String fileName, final String userName) {
		  ArrayList<UserFile> userFiles = fileStoreDB.get(userName);
		  if (userFiles != null) {
			  // user does have some files store/uploaded already
			  addNextFileForUser(fileName, userName);
		  } else  {
			  // user does NOT have any files stored/uploaded
			  addFirstFileForUser(fileName, userName);
		  }
	  }
	  
	  public static void updateFileForUser(final String fileName, final String userName, 
			  final boolean makePublic, final String userToShareFileWith) {
		  if (fileStoreDB.get(userName) == null) {
			  return;
		  }		 
		  
		  fileStoreDB.get(userName).forEach( file -> {
			  if (file.getFileName().equals(fileName)) {
				  logger.log(Level.INFO, "Updating file store configuration for file " + fileName);
				  file.setPublic(makePublic);
				  file.shareWithUser(userToShareFileWith);
			  }
		  });
	  }
	  
	  private static void addFirstFileForUser(final String fileName, final String userName) {
		  ArrayList<UserFile> userFiles = new ArrayList<UserFile>();
		  userFiles.add(new UserFile(fileName, userName));
		  fileStoreDB.put(userName, userFiles);
	  }
	  
	  private static void addNextFileForUser(final String fileName,  final String userName) {
		  ArrayList<UserFile> userFiles = fileStoreDB.get(userName);
		  
		  Optional<UserFile> userFileOfGivenName = userFiles.stream().filter(userFile ->  
		  		userFile.getFileName().equals(fileName) 
		  	).findFirst();
		  
		  if (!userFileOfGivenName.isPresent()) {
			  userFiles.add(new UserFile(fileName, userName));
		  }
	  }
	  
	  private static Set<String> getUsersStoringFiles() {
		  return fileStoreDB.keySet();
	  }
	  
}
