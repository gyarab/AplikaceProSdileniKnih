package com.google.gwt.fileSharingApp.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.thirdparty.json.JSONArray;
import com.google.gwt.thirdparty.json.JSONException;
import com.google.gwt.thirdparty.json.JSONObject;

public class FileSharedBy extends JSONObject {
	public FileSharedBy() {
		super();
		try {
			this.put("fileName", "");
			this.put("sharedBy", "");
		} catch (JSONException e) {
			
		}
	}

	public FileSharedBy(final String fileName, final String ownerName) {
		super();
		this.setFileName(fileName);
		this.setSharedBy(ownerName);
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

	public void setSharedBy(final String sharedBy) {
		try {
			this.put("sharedBy", sharedBy);
		} catch (JSONException e) {
			
		}
	}

	public String getSharedBy() {
		String sharedBy = "";
		try {
			sharedBy = this.getString("sharedBy");
		} catch (JSONException e) {
			
		}
		return sharedBy;
	}
}
