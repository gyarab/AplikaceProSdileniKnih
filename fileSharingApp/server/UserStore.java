package com.google.gwt.fileSharingApp.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class UserStore {
	  private static final Logger logger = Logger.getLogger(UserStore.class.getSimpleName());
		
	  // configure dummy user store with sample user population
	  private static Map<String, String> userStore;
	  static {
		  userStore = new HashMap<String, String>();
		  userStore.put("user1", "Password01");
		  userStore.put("user2", "Password02");
		  userStore.put("user3", "Password03");
		  userStore.put("user4", "Password04");
	  } 
	  
	  public static Set<String> getUsers() {
		  return userStore.keySet();
	  }
	  
	  public static boolean isValidCredentials(final String username, final String password) {
		  return userStore.containsKey(username) && userStore.get(username).equals(password);
	  }
	  
	  public static boolean addUser(final String username, final String password) {
		  if (username == null || username.length() == 0 || password == null || password.length() == 0) {
			  // invalid credentials entered !!!
			  return false;
		  }
		  if (userStore.containsKey(username)) {
			  // user with given username already registered
			  return false;
		  }
		  userStore.put(username, password);
		  
		  return true;
	  }
}
