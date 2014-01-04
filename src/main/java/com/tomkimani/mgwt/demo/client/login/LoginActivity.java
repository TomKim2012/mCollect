package com.tomkimani.mgwt.demo.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
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
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.dialog.Dialogs;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.tomkimani.mgwt.demo.client.ClientFactory;
import com.tomkimani.mgwt.demo.client.MyBeanFactory;
import com.tomkimani.mgwt.demo.client.MyRequestBuilder;
import com.tomkimani.mgwt.demo.client.PioneerAppEntryPoint;
import com.tomkimani.mgwt.demo.client.places.DashboardPlace;

public class LoginActivity extends MGWTAbstractActivity {
	ClientFactory factory;
	private ILoginView view;
	private MyBeanFactory beanFactory;
	public static String loggedUserGroup;
	public static String loggedUserId;

	public interface ILoginView extends IsWidget {
		HasTapHandlers getLoginButton();

		String getuserName();

		String getpassword();

		HTML getIssuesArea();

		void showBusy(boolean status);

		MTextBox getServerAddress();
	}

	public LoginActivity(ClientFactory factory) {
		this.factory = factory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view = factory.getLoginView();
		panel.setWidget(view);

		// AutoBean Factory
		beanFactory = GWT.create(MyBeanFactory.class);
		
		
		view.getServerAddress().setValue(MyRequestBuilder.serverAddress);
		
		// Add Tap Handler for Login
		addHandlerRegistration(view.getLoginButton().addTapHandler(
				new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						String userName = view.getuserName();
						String password = view.getpassword();
						String imeiCode = PioneerAppEntryPoint.deviceImei;
						
						//String serverAddress = view.getServerAddress().getValue();
						String serverAddress = "197.248.2.44:8030";
						MyRequestBuilder.setServerAddress(serverAddress);
						
						if((!userName.isEmpty()) && (!password.isEmpty()) ){
						performLogin(userName, password, imeiCode);
						}else{
							view.getIssuesArea().setText("Please Fill In the Fields");
							view.getIssuesArea().setVisible(true);
						}
					}
				}));
	}

	private void performLogin(String userName, String password, String imeiCode) {
		  String customUrl = "login";
	
		  JSONObject jrequest = new JSONObject(); 
		  jrequest.put("userName", new JSONString(userName));
		  jrequest.put("password", new JSONString(password));
		  jrequest.put("imeiCode", new JSONString(imeiCode));
		  String postData = jrequest.toString();
		 

		MyRequestBuilder rqs = new MyRequestBuilder(RequestBuilder.POST, customUrl);
		view.showBusy(true);
		try {
			Request request = rqs.getBuilder().sendRequest(postData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Dialogs.alert("Error", "There was a problem communicating with the server. Contact the Administrator for Help", null);
				}
				
				public void onResponseReceived(Request request,
						Response response) {
					view.showBusy(false);
					if (200 == response.getStatusCode()) {
						User loggedUser = deserializeFromJson(response.getText());
						if(loggedUser.getAuthorize()){
							loggedUserId = loggedUser.getUserId();
							loggedUserGroup= loggedUser.getGroup();
							factory.getPlaceController().goTo(new DashboardPlace());
						}else{
							view.getIssuesArea().setText(loggedUser.getError());
							view.getIssuesArea().setVisible(true);
						}
						
					} else {
						Dialogs.alert("Error"+response.getStatusCode(), "There was a problem communicating with the server. Contact the Administrator for Help", null);
					}
				}
			});
		} catch (RequestException e) {
			Dialogs.alert("Error", "There was a problem communicating with the server. Contact the Administrator for Help", null);
		}

	}
	
	public interface User{
		String getUserId();
		String getGroup();
		String getFirstName();
		String getLastName();
		String getUserName();
		Boolean getAuthorize();
		String getError();
	}
	
	User makeUser(){
		AutoBean<User> user = beanFactory.User();
		return user.as();
	}
	
	User deserializeFromJson (String json){
		AutoBean<User> bean = AutoBeanCodex.decode(beanFactory, User.class, json);
		return bean.as();
	}	
}
