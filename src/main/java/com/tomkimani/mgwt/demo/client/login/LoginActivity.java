package com.tomkimani.mgwt.demo.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.event.MenuButtonPressedEvent;
import com.googlecode.gwtphonegap.client.event.MenuButtonPressedHandler;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.tomkimani.mgwt.demo.client.ClientFactory;
import com.tomkimani.mgwt.demo.client.MyBeanFactory;
import com.tomkimani.mgwt.demo.client.MyDialogs;
import com.tomkimani.mgwt.demo.client.MyRequestBuilder;
import com.tomkimani.mgwt.demo.client.MyRequestCallback;
import com.tomkimani.mgwt.demo.client.PioneerAppEntryPoint;
import com.tomkimani.mgwt.demo.client.places.DashboardPlace;

public class LoginActivity extends MGWTAbstractActivity {
	ClientFactory factory;
	PhoneGap phoneGap;
	private ILoginView view;
	private MyBeanFactory beanFactory;
	public static String loggedFullNames;
	public static String loggedUserGroup;
	public static String loggedUserId;
	public static String loggedUserName;

	public interface ILoginView extends IsWidget {
		HasTapHandlers getLoginButton();

		String getuserName();

		String getpassword();

		HTML getIssuesArea();

		void showBusy(boolean status);

		MTextBox getServerAddress();

		HasTapHandlers getSaveButton();

		void showIpChange(boolean status);

		void showUpdatePassword(boolean status, String userName);

		String getconfirmPassword();
	}

	public LoginActivity(ClientFactory factory) {
		this.factory = factory;
		this.phoneGap = factory.getPhonegap();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view = factory.getLoginView();
		panel.setWidget(view);

		// AutoBean Factory
		beanFactory = GWT.create(MyBeanFactory.class);

		view.showUpdatePassword(false, null);

		view.getServerAddress().setValue(MyRequestBuilder.serverAddress);

		// Add Tap Handler for Login
		addHandlerRegistration(view.getLoginButton().addTapHandler(
				new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						String userName = view.getuserName();
						String password = view.getpassword();
						String imeiCode = PioneerAppEntryPoint.deviceImei;

						if (LoginView.isFirstTime) {
							String confirmPassword = view.getconfirmPassword();
							
							if ((password.isEmpty()) && (confirmPassword.isEmpty())) {
								view.getIssuesArea().setText(
										"Please Fill In the Fields");
								view.getIssuesArea().setVisible(true);
								return;
							}
							
							if (password.equals(confirmPassword)) {
								performPasswordUpdate(confirmPassword,imeiCode);
							} else {
								view.getIssuesArea().setText(
										"Passwords don't Match");
								view.getIssuesArea().setVisible(true);
							}
						}

						if ((!userName.isEmpty()) && (!password.isEmpty())) {
							performLogin(userName, password, imeiCode);
						} else {
							view.getIssuesArea().setText(
									"Please Fill In the Fields");
							view.getIssuesArea().setVisible(true);
						}
					}
				}));

		addHandlerRegistration(view.getSaveButton().addTapHandler(
				new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						String serverAddress = view.getServerAddress()
								.getValue();
						MyRequestBuilder.setServerAddress(serverAddress);
						view.showIpChange(false);
					}
				}));

		factory.getPhonegap().getEvent().getMenuButton()
				.addMenuButtonPressedHandler(new MenuButtonPressedHandler() {
					private boolean isShown = true;

					@Override
					public void onMenuButtonPressed(MenuButtonPressedEvent event) {
						if (isShown) {
							view.showIpChange(true);
							isShown = false;
						} else {
							view.showIpChange(false);
							isShown = true;
						}
					}
				});

	}

	protected void performPasswordUpdate(String password, String imeiCode) {
		String customUrl = "updatePassword";

		JSONObject jrequest = new JSONObject();
		jrequest.put("userId", new JSONString(loggedUserId));
		jrequest.put("userName", new JSONString(loggedUserName));
		jrequest.put("imeiCode", new JSONString(imeiCode));
		jrequest.put("password", new JSONString(password));
		String postData = jrequest.toString();

		MyRequestBuilder rqs = new MyRequestBuilder(RequestBuilder.POST,
				customUrl);
		view.showBusy(true);
		try {
			Request request = rqs.getBuilder().sendRequest(postData,
					new MyRequestCallback() {

						public void onResponseReceived(Request request,
								Response response) {
							view.showBusy(false);
							if (200 == response.getStatusCode()) {
								User loggedUser = deserializeFromJson(response
										.getText());
								if (loggedUser.getAuthorize()) {

									loggedUserId = loggedUser.getUserId();
									if (loggedUser.getFirstTime()) {
										view.showUpdatePassword(true,
												loggedUser.getUserName());
										return;
									}

									loggedUserGroup = loggedUser.getGroup();
									loggedUserName = loggedUser.getUserName();
									loggedFullNames = loggedUser.getFirstName();

									factory.getPlaceController().goTo(
											new DashboardPlace());

								} else {
									view.showUpdatePassword(false, null);
									view.getIssuesArea().setText(
											loggedUser.getError());
									view.getIssuesArea().setVisible(true);
								}

							} else {
								MyDialogs.confirm(
										MyDialogs.NETWORK_ERROR_TITLE,
										MyDialogs.NETWORK_ERROR_MESSAGE, null);
							}
						}
					});
		} catch (RequestException e) {
			view.showBusy(false);
			MyDialogs.confirm(MyDialogs.NETWORK_ERROR_TITLE,
					MyDialogs.NETWORK_ERROR_MESSAGE, null);
		}

	}

	private void performLogin(String userName, String password, String imeiCode) {
		String customUrl = "login";

		JSONObject jrequest = new JSONObject();
		jrequest.put("userName", new JSONString(userName));
		jrequest.put("password", new JSONString(password));
		jrequest.put("imeiCode", new JSONString(imeiCode));
		String postData = jrequest.toString();

		MyRequestBuilder rqs = new MyRequestBuilder(RequestBuilder.POST,
				customUrl);
		view.showBusy(true);
		try {
			Request request = rqs.getBuilder().sendRequest(postData,
					new MyRequestCallback() {

						public void onResponseReceived(Request request,
								Response response) {
							view.showBusy(false);
							if (200 == response.getStatusCode()) {
								PioneerAppEntryPoint.consoleLog(response.getText());
								User loggedUser = deserializeFromJson(response
										.getText());
								if (loggedUser.getAuthorize()) {

									loggedUserId = loggedUser.getUserId();
									loggedUserName = loggedUser.getUserName();
									
									if (loggedUser.getFirstTime()) {
										view.showUpdatePassword(true,
												loggedUser.getUserName());
										return;
									}

									loggedUserGroup = loggedUser.getGroup();
									loggedFullNames = loggedUser.getFirstName();

									factory.getPlaceController().goTo(
											new DashboardPlace());

								} else {
									view.getIssuesArea().setText(
											loggedUser.getError());
									view.getIssuesArea().setVisible(true);
								}

							} else {
								MyDialogs.confirm(
										MyDialogs.NETWORK_ERROR_TITLE,
										MyDialogs.NETWORK_ERROR_MESSAGE, null);
							}
						}
					});
		} catch (RequestException e) {
			view.showBusy(false);
			MyDialogs.confirm(MyDialogs.NETWORK_ERROR_TITLE,
					MyDialogs.NETWORK_ERROR_MESSAGE, null);
		}

	}

	public interface User {
		String getUserId();

		String getGroup();

		String getFirstName();

		String getLastName();

		String getUserName();

		Boolean getAuthorize();

		Boolean getFirstTime();

		String getError();
	}

	User makeUser() {
		AutoBean<User> user = beanFactory.User();
		return user.as();
	}

	User deserializeFromJson(String json) {
		AutoBean<User> bean = AutoBeanCodex.decode(beanFactory, User.class,
				json);
		return bean.as();
	}
}
