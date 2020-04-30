package com.google.gwt.fileSharingApp.server;

public class AuthStatus {
	private boolean authenticated;
	private String sessionId;
	private String userName;
	
	AuthStatus(boolean authenticated, String userName, String sessionId) {
		this.authenticated = authenticated;
		this.userName = userName;
		this.sessionId = sessionId;
	}
	
	public boolean isAuthenticated() {
		return this.authenticated;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
}
