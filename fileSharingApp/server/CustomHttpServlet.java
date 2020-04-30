package com.google.gwt.fileSharingApp.server;

import java.io.PrintWriter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.fileSharingApp.shared.Constants;
import com.google.gwt.thirdparty.json.JSONArray;
import com.google.gwt.thirdparty.json.JSONException;
import com.google.gwt.thirdparty.json.JSONObject;

abstract class CustomHttpServlet extends HttpServlet {	
	// eclipse auto-generated to avoid warning
	private static final long serialVersionUID = -827333475865157364L;

	protected boolean isAuthenticated(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie: cookies) {
			if (Constants.SESSION_ID_COOKIE.equals(cookie.getName())) {
				final String sessionId = cookie.getValue();
				if ( SessionStore.isValidSession(sessionId)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// Extended version of isAuhtnticated method to retrieve more details 
	protected AuthStatus getAuthDetails(HttpServletRequest request) {
		boolean authenticated = false;
		String userName = "";
		String sessionId = "";
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie: cookies) {
			if (Constants.SESSION_ID_COOKIE.equals(cookie.getName())) {
				sessionId = cookie.getValue();
				if ( SessionStore.isValidSession(sessionId)) {
					authenticated = true;
				} 
			}
		}
		userName = SessionStore.getUsernameForSessionId(sessionId);
		return new AuthStatus(authenticated, userName, sessionId);
	}
	
	protected void buildSimpleJSONStatusResponse(HttpServletResponse response, final String status)  {
		response.addHeader("Content-Type", "application/json");
		PrintWriter responseBody = null;
		try {
			responseBody = response.getWriter();		
			final String jsonAsString = 
					new JSONObject().put("Status", status != null ? status: "").toString();
			responseBody.print(jsonAsString);
		} catch (Exception e) {
			
		} finally {
			if (responseBody != null) {
				responseBody.flush();
			}
		}
	}
	
	
	
}
