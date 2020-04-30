package com.google.gwt.fileSharingApp.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionStore {
	  private static Map<String, String> userSessionStore = new HashMap<String, String>();
	  private static void deleteSessionIdsForUser(final String userName) {
		  userSessionStore.remove(userName);
	  }
	  
	  public static String addNewSessionIdForUser(final String username) {
		  deleteSessionIdsForUser(username);
		  final String sessionId = getNewSessionId();
		  userSessionStore.put(username, sessionId);
		  return sessionId;
	  }
	    
	  public static String getUserSessionId(final String username) {
		  return userSessionStore.get(username);
	  }
	  
	  public static boolean isValidSession(final String sessionId) {
		  return userSessionStore.containsValue(sessionId);
	  }
	  
	  public static String getUsernameForSessionId(final String sessionId) {
		  String userName = null;
		  if ( userSessionStore.containsValue(sessionId) ) {
			  userName = userSessionStore.
					  entrySet().
					  stream().
					  filter( entry -> entry.getValue().equals(sessionId)).
					  findFirst().
					  get().
					  getKey();
		  }
		  
		  return userName;
	  }
	  
	  private static String getNewSessionId() {
		  return UUID.randomUUID().toString();
	  }
}
