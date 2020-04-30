package com.google.gwt.fileSharingApp.server;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.thirdparty.json.JSONArray;
import com.google.gwt.thirdparty.json.JSONException;
import com.google.gwt.thirdparty.json.JSONObject;

public class UserFile extends JSONObject {
	public UserFile() {
		super();
		try {
			this.put("fileName", "");
			this.put("owner", "");
			this.put("public", false);
			this.put("sharedWith", new JSONArray());
		} catch (JSONException e) {
			
		}
	}
	
	public UserFile(final String fileName, final String owner) {
		// call non parameter constructor first to make sure we have all the fields created first
		this();
		this.setFileName(fileName);
		this.setOwner(owner);
	}
	
	public void setFileName(final String filename) {
		try {
			this.put("fileName", filename);
		} catch (JSONException e) {

		}
	}

	public String getFileName() {
		String filename = "";
		try {
			filename = this.getString("fileName");
		} catch (JSONException e) {

		}
		return filename;
	}
	
	public void setOwner(final String owner) {
		try {
			this.put("owner", owner);
		} catch (JSONException e) {

		}
	}

	public String getOwner() {
		String owner = "";
		try {
			owner = this.getString("owner");
		} catch (JSONException e) {

		}
		return owner;
	}

	public void setPublic(final boolean sharedWithEveryone) {
		try {
			this.put("public", sharedWithEveryone);
		} catch (JSONException e) {

		}
	}

	public boolean isPublic() {
		boolean sharedWithEveryone = false;
		try {
			sharedWithEveryone = this.getBoolean("public");
		} catch (JSONException e) {

		}
		return sharedWithEveryone;
	}

	public void shareWithUser(final String username) {
		try {
			if (username != null && !username.isEmpty()) {
				this.getJSONArray("sharedWith").put(username);
				//Revisit to make sure we are not entering the same user twice!
			}
		} catch (JSONException e) {

		}
	}

	public boolean isSharedWithUser(final String username) {
		boolean isShared = false;
		try {
			JSONArray sharedWithUsers = this.getJSONArray("sharedWith");
			for (int i = 0; i < sharedWithUsers.length(); i++) {
				isShared = sharedWithUsers.get(i).equals(username);
				if (isShared) {
					break;
				}
			}
		} catch (JSONException e) {

		}

		return isShared;
	}

	public Set<String> getSharedWithUsers() {
		Set<String> userList = new HashSet<String>();
		// Java Set implementations (like HashSet) does not allow duplicates
		// So in case we happen to store the same user twice, we would not at
		// least return duplicates
		// See above, best to make sure we do not enter the same username twice!
		try {
			final int userListLength = this.getJSONArray("sharedWith").length();
			for (int i = 0; i < userListLength; i++) {
				userList.add(this.getJSONArray("sharedWith").getString(i));
			}
		} catch (JSONException e) {
			
		}
		return userList;
	}
}