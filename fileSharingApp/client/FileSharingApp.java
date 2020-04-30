package com.google.gwt.fileSharingApp.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import static com.google.gwt.query.client.GQuery.*;

import com.google.gwt.fileSharingApp.shared.Constants;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.apache.http.HttpStatus;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FileSharingApp implements EntryPoint {
	private static Logger logger = Logger.getLogger(FileSharingApp.class.getName());
	private static final String homeLinkId = "homeLink";
	private static final String loginLinkId = "loginLink";
	private static final String uploadFilesLinkId = "uploadFilesLink";
	private static final String shareFilesLinkId = "shareFilesLink";
	private static final String downloadFilesLinkId = "downloadFilesLink";

	private static final String homeContainerId = "homeContainer";
	private static final String loginContainerId = "loginContainer";
	private static final String uploadFilesContainerId = "uploadFilesContainer";
	private static final String uploadFilesFormContainerId = "uploadFilesFormContainer";
	private static final String shareFilesContainerId = "shareFilesContainer";
	private static final String downloadFilesContainerId = "downloadFilesContainer";

	private static final String registerLinkId = "registerLink";
	private static final String logoutLinkId = "logoutLink";
	private String authenticatedUser = null;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Set initial tab and configure click event handlers so that we can
		// navigate to other tabs
		configureMenu();

		// configure register/logout links on login tab
		configureLoginContainerLinks();
	}

	private void configureMenu() {
		// Activate home page content container & menu link
		this.navigateToTab(FileSharingApp.homeLinkId, FileSharingApp.homeContainerId);

		// Attache click event handlers for the top navigation menu
		Anchor.wrap(RootPanel.get(FileSharingApp.homeLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Home page link clicked");
				navigateToTab(FileSharingApp.homeLinkId, FileSharingApp.homeContainerId);
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.loginLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Login page link clicked");
				navigateToTab(FileSharingApp.loginLinkId, FileSharingApp.loginContainerId);
				configureLoginContainerContent();
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.uploadFilesLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Upload files page link clicked");
				navigateToTab(FileSharingApp.uploadFilesLinkId, FileSharingApp.uploadFilesContainerId);
				configureFileUploadContainerContent();
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.shareFilesLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Share files page link clicked");
				navigateToTab(FileSharingApp.shareFilesLinkId, FileSharingApp.shareFilesContainerId);
				configureShareFilesContainerContent();
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.downloadFilesLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Download files page link clicked");
				navigateToTab(FileSharingApp.downloadFilesLinkId, FileSharingApp.downloadFilesContainerId);
				configureDownloadFilesContainerContent();
			}
		});

	}

	private void navigateToTab(final String tabLink, final String tabContainer) {
		// hide all content tabs
		$(".tab-container").addClass("hidden-container");
		// deactivate activate navigation menu link
		$(".tab-link.active").removeClass("active");

		RootPanel.get(tabLink).addStyleName("active");
		RootPanel.get(tabContainer).removeStyleName("hidden-container");
	}

	private boolean isUserLoggedIn() {
		return this.authenticatedUser != null;
	}

	private void configureLoginContainerContent() {
		if (isUserLoggedIn()) {
			configurePostLoginScreen();
		} else {
			configureLoginScreen();
		}
	}

	private void configureLoginContainerLinks() {
		Anchor.wrap(RootPanel.get(FileSharingApp.registerLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Registration link clicked");
				configureRegistrationScreen();
				// configureDownloadFilesContainerContent();
			}
		});

		Anchor.wrap(RootPanel.get(FileSharingApp.logoutLinkId).getElement()).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileSharingApp.logger.log(Level.INFO, "Logout link clicked");
				logoutUser();
			}
		});
	}

	private void addLoginForm() {
		$("#loginContainer form").remove();
		final FormPanel form = new FormPanel();
		form.setAction(getDefaultFileUploadFormAction());
		form.setEncoding(FormPanel.ENCODING_URLENCODED);
		form.setMethod(FormPanel.METHOD_POST);
		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);
		final Label usernameLabel = new Label();
		usernameLabel.setText("Please enter your username");
		panel.add(usernameLabel);
		final TextBox usernameTextBox = new TextBox();
		usernameTextBox.setName("username");
		usernameTextBox.setFocus(true);
		usernameTextBox.setWidth("200px");
		panel.add(usernameTextBox);
		final Label passwordLabel = new Label();
		passwordLabel.setText("Please enter your password");
		panel.add(passwordLabel);
		final PasswordTextBox passwordTextBox = new PasswordTextBox();
		passwordTextBox.setName("password");
		passwordTextBox.setWidth("200px");
		panel.add(passwordTextBox);
		panel.setSpacing(3);
		panel.setTitle("User name and password table");
		Button submitBtn = new Button();
		submitBtn.setText("Login");
		submitBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		panel.add(submitBtn);
		panel.setCellHorizontalAlignment(submitBtn, HasHorizontalAlignment.ALIGN_CENTER);

		Label statusLabel = new Label();
		statusLabel.setText("");
		panel.add(statusLabel);
		panel.setCellHorizontalAlignment(statusLabel, HasHorizontalAlignment.ALIGN_CENTER);

		// Add an event handler to the form.
		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				// This event is fired just before the form is submitted.
				form.setAction(getDefaultLoginFormAction());
				if (usernameTextBox.getText().isEmpty() || passwordTextBox.getText().isEmpty()) {
					Window.alert("Username or password missing");
					event.cancel();
				}
			}
		});
		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String jsonString = event.getResults().replaceAll("\\<.*?\\>", "");
				if (event.getResults().toLowerCase().contains("success")) {
					updateLoginStatus(usernameTextBox.getText());
					configurePostLoginScreen();
				} else {
					statusLabel.setText("Login failure");
				}
				clearLoginForm(usernameTextBox, passwordTextBox);
				logger.log(Level.INFO, "Login request response received: " + jsonString);
			}
		});

		RootPanel.get(FileSharingApp.loginContainerId).add(form);
	}

	private void addRegistrationForm() {
		$("#loginContainer form").remove();
		final FormPanel form = new FormPanel();
		form.setAction(getDefaultFileUploadFormAction());
		form.setEncoding(FormPanel.ENCODING_URLENCODED);
		form.setMethod(FormPanel.METHOD_POST);
		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);
		final Label usernameLabel = new Label();
		usernameLabel.setText("Please enter your username");
		panel.add(usernameLabel);
		final TextBox usernameTextBox = new TextBox();
		usernameTextBox.setName("username");
		usernameTextBox.setFocus(true);
		usernameTextBox.setWidth("200px");
		panel.add(usernameTextBox);
		final Label passwordLabel = new Label();
		passwordLabel.setText("Please enter your password");
		panel.add(passwordLabel);
		final PasswordTextBox passwordTextBox = new PasswordTextBox();
		passwordTextBox.setName("password");
		passwordTextBox.setWidth("200px");
		panel.add(passwordTextBox);
		panel.setSpacing(3);
		panel.setTitle("User name and password table");
		Button submitBtn = new Button();
		submitBtn.setText("Register");
		submitBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		panel.add(submitBtn);
		panel.setCellHorizontalAlignment(submitBtn, HasHorizontalAlignment.ALIGN_CENTER);

		Label statusLabel = new Label();
		statusLabel.setText("");
		panel.add(statusLabel);
		panel.setCellHorizontalAlignment(statusLabel, HasHorizontalAlignment.ALIGN_CENTER);

		// Add an event handler to the form.
		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				// This event is fired just before the form is submitted.
				form.setAction(getDefaultRegistrationFormAction());
				if (usernameTextBox.getText().isEmpty() || passwordTextBox.getText().isEmpty()) {
					Window.alert("Username or password missing");
					event.cancel();
				}
			}
		});
		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String jsonString = event.getResults().replaceAll("\\<.*?\\>", "");

				if (event.getResults().toLowerCase().contains("success")) {
					configureLoginScreen();
				} else {
					statusLabel.setText("Registration failure");
				}
				clearLoginForm(usernameTextBox, passwordTextBox);
				logger.log(Level.INFO, "Registration response received: " + jsonString);
			}
		});

		RootPanel.get(FileSharingApp.loginContainerId).add(form);
	}

	private void configurePostLoginScreen() {
		// remove the login form
		$("#loginContainer form").remove();

		// hide the registration link
		$("#registerPleaseTitle").hide();
		// hide the registration screen title
		$("#registrationTitle").hide();
		// add/enable the "Login success" message with logout link
		$("#logoutPleaseTitle").show();
	}

	private void configureLoginScreen() {
		// hide the registration link
		$("#registerPleaseTitle").show();
		// hide the registration screen title
		$("#registrationTitle").hide();
		// add/enable the "Login success" message with logout link
		$("#logoutPleaseTitle").hide();
		addLoginForm();
	}

	private void configureRegistrationScreen() {
		// hide the registration link
		$("#registerPleaseTitle").hide();
		// hide the registration screen title
		$("#registrationTitle").show();
		// add/enable the "Login success" message with logout link
		$("#logoutPleaseTitle").hide();
		addRegistrationForm();
	}

	private void configureFileUploadContainerContent() {
		if (!isAuthenticated()) {
			$("#uploadFilesContainer .authenticated-user").hide();
			$("#uploadFilesContainer .anonymous-user").show();
		} else {
			$("#uploadFilesContainer .anonymous-user").hide();
			$("#uploadFilesContainer .authenticated-user").show();
			$("#uploadFilesContainer form").remove();
			final FormPanel form = new FormPanel();
			form.setAction(getDefaultFileUploadFormAction());
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			VerticalPanel panel = new VerticalPanel();
			form.setWidget(panel);
			FileUpload upload = new FileUpload();
			upload.setName("uploadFormElement");
			panel.add(upload);
			Button submitBtn = new Button();
			submitBtn.setText("Submit");
			submitBtn.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					form.submit();
				}
			});
			panel.add(submitBtn);
			panel.setCellHorizontalAlignment(submitBtn, HasHorizontalAlignment.ALIGN_CENTER);

			Label statusLabel = new Label();
			statusLabel.setText("");
			statusLabel.getElement().setId("uploadStatusId");
			panel.add(statusLabel);
			panel.setCellHorizontalAlignment(statusLabel, HasHorizontalAlignment.ALIGN_CENTER);

			// Add an event handler to the form.
			form.addSubmitHandler(new FormPanel.SubmitHandler() {
				public void onSubmit(SubmitEvent event) {
					// This event is fired just before the form is
					// submitted. We can take
					final String fileName = getFileName(upload);
					form.setAction(getFileUploadFormAction(fileName));
					if (upload.getFilename().isEmpty()) {
						Window.alert("No file selected yet");
						event.cancel();
					}
				}
			});
			form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
				public void onSubmitComplete(SubmitCompleteEvent event) {
					String jsonString = event.getResults().replaceAll("\\<.*?\\>", "");
					if (event.getResults().toLowerCase().contains("success")) {
						statusLabel.setText("File upload success");
					} else {
						statusLabel.setText("File upload failure");
					}
					upload.getElement().setPropertyString("value", "");
					logger.log(Level.INFO, "File upload request response received: " + jsonString);
				}
			});
			RootPanel.get(FileSharingApp.uploadFilesFormContainerId).add(form);

		}
	}

	private void configureShareFilesContainerContent() {
		if (!isAuthenticated()) {
			$("#shareFilesContainer .authenticated-user").hide();
			$("#shareFilesContainer .anonymous-user").show();
		} else {
			$("#shareFilesContainer .anonymous-user").hide();
			$("#shareFilesContainer .authenticated-user").show();
			// remove whatever content we may have rendered with the element
			// with id="userFilesToDownload"
			$("#userFilesToShare").children().remove();

			try {
				String requestData = null;
				String shareFilesTabDataRestEndpoint = "/filesharingapp/filesharingapp/shareFilesTabData";

				RequestBuilder shareFilesTabDataRbuilder = new RequestBuilder(RequestBuilder.GET,
						URL.encode(shareFilesTabDataRestEndpoint));
				shareFilesTabDataRbuilder.sendRequest(requestData, new RequestCallback() {
					@Override
					public void onResponseReceived(Request request, Response response) {
						logger.log(Level.INFO, "/shareFilesTabData: received response " + response.getText());
						if (HttpStatus.SC_OK == response.getStatusCode()) {
							try {
								JSONValue jsonValue = JSONParser.parseStrict(response.getText());
								JSONObject responseAsJSON = jsonValue.isObject();
								JSONArray allUsers = responseAsJSON.get("allUsers").isArray();
								List<String> allUsersAsList = new ArrayList<String>();
								for (int j = 0; j < allUsers.size(); j++) {
									allUsersAsList.add(allUsers.get(j).isString().stringValue());
								}

								JSONArray userFiles = responseAsJSON.get("userFiles").isArray();

								for (int i = 0; i < userFiles.size(); i++) {

									final String fileName = userFiles.get(i).isObject().get("fileName").isString()
											.toString().replace("\"", "");
									final String sharedBy = userFiles.get(i).isObject().get("owner").isString()
											.toString().replace("\"", "");
									final boolean isPublic = userFiles.get(i).isObject().get("public").isBoolean()
											.booleanValue();
									final JSONArray sharedWith = userFiles.get(i).isObject().get("sharedWith")
											.isArray();
									List<String> sharedWithAsList = new ArrayList<String>();
									for (int j = 0; j < sharedWith.size(); j++) {
										sharedWithAsList.add(sharedWith.get(j).isString().stringValue());
									}

									HorizontalPanel userFilePanel = new HorizontalPanel();
									Label fileNameLabel = new Label();
									fileNameLabel.setText(fileName);
									fileNameLabel.setWidth("350px");
									userFilePanel.add(fileNameLabel);
									userFilePanel.setCellHorizontalAlignment(fileNameLabel,
											HasHorizontalAlignment.ALIGN_LEFT);
									userFilePanel.setCellVerticalAlignment(fileNameLabel,
											HasVerticalAlignment.ALIGN_MIDDLE);

									ListBox shareWithListBox = new ListBox();
									allUsersAsList.stream().filter(usr -> {
										// filter our owner and users the file
										// is already shared with!!!
										return !sharedBy.equals(usr) && !sharedWithAsList.contains(usr);
									}).forEach(usr -> {
										shareWithListBox.addItem(usr);
									});

									userFilePanel.add(shareWithListBox);
									userFilePanel.setCellHorizontalAlignment(shareWithListBox,
											HasHorizontalAlignment.ALIGN_CENTER);
									userFilePanel.setCellVerticalAlignment(shareWithListBox,
											HasVerticalAlignment.ALIGN_MIDDLE);
									userFilePanel.setCellWidth(shareWithListBox, "100px");

									CheckBox makePublicCheckBox = new CheckBox("Public");
									makePublicCheckBox.setValue(isPublic);
									userFilePanel.add(makePublicCheckBox);
									userFilePanel.setCellHorizontalAlignment(makePublicCheckBox,
											HasHorizontalAlignment.ALIGN_CENTER);
									userFilePanel.setCellVerticalAlignment(makePublicCheckBox,
											HasVerticalAlignment.ALIGN_MIDDLE);
									userFilePanel.setCellWidth(makePublicCheckBox, "80px");

									Button updateBtn = new Button();
									updateBtn.setText("Update");
									updateBtn.addClickHandler(new ClickHandler() {
										public void onClick(ClickEvent event) {
											String updateShareSettingsRestEndpoint = "/filesharingapp/filesharingapp/updateShareSettings";
											String requestData = "fileName=" + URL.encode(fileNameLabel.getText())
													+ "&makePublic="
													+ URL.encode(makePublicCheckBox.getValue().toString())
													+ "&shareWith=" + URL.encode(shareWithListBox
															.getValue(shareWithListBox.getSelectedIndex()));
											RequestBuilder updateShareSettingsRBuilder = new RequestBuilder(
													RequestBuilder.POST, URL.encode(updateShareSettingsRestEndpoint));
											updateShareSettingsRBuilder.setHeader("Content-type",
													"application/x-www-form-urlencoded");
											try {
												updateShareSettingsRBuilder.sendRequest(requestData,
														new RequestCallback() {
															@Override
															public void onResponseReceived(Request request,
																	Response response) {
																logger.log(Level.INFO,
																		"/updateShareSettings: received response "
																				+ response.getText());
																if (HttpStatus.SC_OK == response.getStatusCode()) {
																	try {

																	} catch (Exception e) {
																		
																	}
																} else {
																}
															}

															@Override
															public void onError(Request request, Throwable exception) {
															}
														});
											} catch (RequestException e) {
												e.printStackTrace();
											}
										}
									});
									userFilePanel.add(updateBtn);
									userFilePanel.setCellHorizontalAlignment(shareWithListBox,
											HasHorizontalAlignment.ALIGN_CENTER);
									userFilePanel.setCellVerticalAlignment(shareWithListBox,
											HasVerticalAlignment.ALIGN_MIDDLE);

									RootPanel.get("userFilesToShare").add(userFilePanel);
								}

							} catch (Exception e) {
							}
						} else {
						}
					}

					@Override
					public void onError(Request request, Throwable exception) {
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void logoutUser() {
		try {
			String requestData = null;
			String logoutRestEndpoint = "/filesharingapp/filesharingapp/logout";

			RequestBuilder shareFilesTabDataRbuilder = new RequestBuilder(RequestBuilder.GET,
					URL.encode(logoutRestEndpoint));
			shareFilesTabDataRbuilder.sendRequest(requestData, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					logger.log(Level.INFO, "/logout: received response " + response.getText());
					if (HttpStatus.SC_OK == response.getStatusCode()) {
						try {
							removeLoggedInUserName();
							configureLoginScreen();
						} catch (Exception e) {
		
						}
					} else {
	
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeLoggedInUserName() {
		this.authenticatedUser = null;
		RootPanel.get("loginStatus").getElement().removeAllChildren();
	}

	private void configureDownloadFilesContainerContent() {
		if (!isAuthenticated()) {
			$("#downloadFilesContainer .authenticated-user").hide();
			$("#downloadFilesContainer .anonymous-user").show();
		} else {
			$("#downloadFilesContainer .anonymous-user").hide();
			$("#downloadFilesContainer .authenticated-user").show();
			// remove whatever content we may have rendered with the element
			// with id="userFilesToDownload"
			$("#userFilesToDownload").children().remove();
			// remove whatever content we may have rendered with the element
			// with id="sharedFilesToDownload"
			$("#sharedFilesToDownload").children().remove();
			// remove whatever content we may have rendered with the element
			// with id="publicFilesToDownload"
			$("#publicFilesToDownload").children().remove();
			// render a table with link to download user's own files

			try {
				String requestData = null;
				String accessibleFilesEndpoint = "/filesharingapp/filesharingapp/accessibleFiles";

				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(accessibleFilesEndpoint));
				builder.sendRequest(requestData, new RequestCallback() {
					@Override
					public void onResponseReceived(Request request, Response response) {
						logger.log(Level.INFO, "/accessibleFiles: received response " + response.getText());
						if (HttpStatus.SC_OK == response.getStatusCode()) {
							try {
								// Build tables with links to download
								// individual files
								// links would be such as one below:
								// http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName=notes.txt

								JSONValue jsonValue = JSONParser.parseStrict(response.getText());
								JSONObject responseAsJSON = jsonValue.isObject();

								JSONArray publicFiles = responseAsJSON.get("publicFiles").isArray();
								VerticalPanel publicFilesLinksPanel = new VerticalPanel();
								for (int i = 0; i < publicFiles.size(); i++) {
									final String fileName = publicFiles.get(i).isObject().get("fileName").isString()
											.toString().replace("\"", "");
									final String sharedBy = publicFiles.get(i).isObject().get("sharedBy").isString()
											.toString().replace("\"", "");
									String fileDownloaUrl = "http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName="
											+ fileName + "&sharedBy=" + sharedBy;
									Anchor link = new Anchor(fileName, fileDownloaUrl);
									link.setTarget("_blank");
									publicFilesLinksPanel.add(link);
								}
								RootPanel.get("publicFilesToDownload").add(publicFilesLinksPanel);

								JSONArray userFiles = responseAsJSON.get("userFiles").isArray();
								VerticalPanel userFilesLinksPanel = new VerticalPanel();
								for (int i = 0; i < userFiles.size(); i++) {
									final String fileName = userFiles.get(i).isObject().get("fileName").isString()
											.toString().replace("\"", "");
									final String sharedBy = userFiles.get(i).isObject().get("sharedBy").isString()
											.toString().replace("\"", "");
									String fileDownloaUrl = "http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName="
											+ fileName + "&sharedBy=" + sharedBy;
									Anchor link = new Anchor(fileName, fileDownloaUrl);
									link.setTarget("_blank");
									userFilesLinksPanel.add(link);
								}
								RootPanel.get("userFilesToDownload").add(userFilesLinksPanel);

								JSONArray sharedFiles = responseAsJSON.get("sharedFiles").isArray();
								VerticalPanel sharedFilesLinksPanel = new VerticalPanel();
								for (int i = 0; i < sharedFiles.size(); i++) {
									final String fileName = sharedFiles.get(i).isObject().get("fileName").isString()
											.toString().replace("\"", "");
									final String sharedBy = sharedFiles.get(i).isObject().get("sharedBy").isString()
											.toString().replace("\"", "");
									String fileDownloaUrl = "http://localhost:8080/filesharingapp/filesharingapp/downloadFile?fileName="
											+ fileName + "&sharedBy=" + sharedBy;
									Anchor link = new Anchor(fileName, fileDownloaUrl);
									link.setTarget("_blank");
									sharedFilesLinksPanel.add(link);
								}
								RootPanel.get("sharedFilesToDownload").add(sharedFilesLinksPanel);
							} catch (Exception e) {

							}
						} else {

						}
					}

					@Override
					public void onError(Request request, Throwable exception) {

					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void updateLoginStatus(final String username) {
		if (username == null || username.isEmpty()) {
			return;
		}
		this.authenticatedUser = username;
		final Label authenticatedUserLabel = new Label();
		authenticatedUserLabel.setText("Logged in as " + this.authenticatedUser);
		RootPanel.get("loginStatus").add(authenticatedUserLabel);
	}

	private String getFileUploadFormAction(String fileName) {
		final String action = "/" + Constants.APP_NAME + "/" + Constants.MODULE_NAME + "/"
				+ Constants.FILE_UPLOAD_SERVICE + "?fileName=" + fileName;
		return action;
	}

	private String getDefaultFileUploadFormAction() {
		final String action = "/" + Constants.APP_NAME + "/" + Constants.MODULE_NAME + "/"
				+ Constants.FILE_UPLOAD_SERVICE;
		return action;
	}

	private String getDefaultLoginFormAction() {
		final String action = "/" + Constants.APP_NAME + "/" + Constants.MODULE_NAME + "/" + Constants.LOGIN_SERVICE;
		return action;
	}

	private String getDefaultRegistrationFormAction() {
		final String action = "/" + Constants.APP_NAME + "/" + Constants.MODULE_NAME + "/" + Constants.REGISTER_SERVICE;
		return action;
	}

	private String getFileName(FileUpload fileUpload) {
		final String fileName = fileUpload.getFilename().substring(Constants.FILE_UPLOAD_NAME_FAKE_PATH.length());
		return fileName;
	}

	private void clearLoginForm(TextBox username, TextBox password) {
		if (username != null) {
			username.setValue("");
		}
		if (password != null) {
			password.setValue("");
		}
	}

	private void disableLoginForm(TextBox username, TextBox password, Button loginBtn) {
		if (username != null) {
			username.setEnabled(false);
		}
		if (password != null) {
			password.setEnabled(false);
		}
		if (loginBtn != null) {
			loginBtn.setEnabled(false);
		}
	}

	private boolean isAuthenticated() {
		return !(this.authenticatedUser == null || this.authenticatedUser.isEmpty());
	}
}
