package com.tomkimani.mgwt.demo.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.googlecode.mgwt.ui.client.widget.ProgressIndicator;
import com.googlecode.mgwt.ui.client.widget.WidgetList;
import com.tomkimani.mgwt.demo.client.css.LogoBundle;
import com.tomkimani.mgwt.demo.client.login.LoginActivity.ILoginView;
import com.tomkimani.mgwt.demo.client.ui.IconButton;
import com.tomkimani.mgwt.demo.client.ui.PasswordField;
import com.tomkimani.mgwt.demo.client.ui.TextField;

public class LoginView implements ILoginView {

	private static LoginViewUiBinder uiBinder = GWT
			.create(LoginViewUiBinder.class);

	interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
	}

	LayoutPanel LayoutPanel;
	WidgetList widgetList;
	Button loginButton;
	private HTML IssuesArea;
	private TextField userNameField;
	private PasswordField passWord;
	private ProgressIndicator progressIndicator;
	private MTextBox serverAddress;
	private WidgetList ipConfigList;
	private MListBox mListBox;
	private Button saveButton;
	private IconButton ipButton;
	private PasswordField confirmPassWord;
	private HTML title;
	
	public static Boolean isFirstTime = false;

	// private final Widget widget;

	public LoginView() {
		// widget = uiBinder.createAndBindUi(this);
		widgetList = new WidgetList();

		ipConfigList = new WidgetList();
		LayoutPanel = new LayoutPanel();
		title = new HTML();
		IssuesArea = new HTML();
		loginButton = new Button("LOGIN");
		ipButton = new IconButton("icon-cogs", null);

		// Logo
		Image logo = new Image(LogoBundle.INSTANCE.logo());
		logo.getElement().getStyle().setMarginLeft(20.0, Unit.PCT);
		LayoutPanel.add(logo);

		// UserName And Password TexFields
		widgetList.setRound(true);
		userNameField = new TextField("UserName");
		passWord = new PasswordField("Password");
		confirmPassWord = new PasswordField("Confirm Password");
		widgetList.add(userNameField);
		widgetList.add(passWord);

		// Ipconfig List
		ipConfigList.setRound(true);
		ipConfigList.setVisible(false);
		// LayoutPanel.add(new HTML("Set the Ip-Address"));
		ipButton.setVisible(false);
		ipButton.getElement().getStyle().clearWidth();
		ipButton.setWidth("8%");
		ipButton.removeMinWidth();
		ipButton.getElement().getStyle().setMarginTop(30, Unit.PCT);

		ipButton.addTapHandler(new TapHandler() {

			private boolean isShown = true;

			@Override
			public void onTap(TapEvent event) {
				if (isShown) {
					showIpChange(true);
					ipButton.setIcon("icon-arrow-left");
					isShown = false;
				} else {
					showIpChange(false);
					ipButton.setIcon(" icon-cogs");
					isShown = true;
				}
			}
		});

		mListBox = new MListBox();
		mListBox.addItem("Cloud Server", "197.248.2.44:8030");
		mListBox.addItem("Local", "197.237.31.119");
		ipConfigList.add(mListBox);
		serverAddress = new MTextBox();
		serverAddress.getElement().setAttribute("type", "number");
		ipConfigList.add(serverAddress);

		saveButton = new Button("Save");
		saveButton.setConfirm(true);
		saveButton.setVisible(false);

		mListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String value = mListBox.getValue(mListBox.getSelectedIndex());
				serverAddress.setValue(value);
			}
		});
		
		//Title Area
		title.getElement().getStyle().setColor("Blue");
		title.setVisible(false);
		title.getElement().getStyle().setMarginLeft(20.0, Unit.PCT);
		title.getElement().getStyle().setMarginTop(5.0, Unit.PCT);
		title.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
		title.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		LayoutPanel.add(title);
		
		
		// Layout Panel Items
		LayoutPanel.add(widgetList);
		LayoutPanel.add(ipConfigList);

		// Progress Indicator
		progressIndicator = new ProgressIndicator();
		progressIndicator.getElement().setAttribute("style",
				"margin:auto; margin-top: 50px");
		progressIndicator.setVisible(false);
		LayoutPanel.add(progressIndicator);
		
		// IssuesArea
		IssuesArea.getElement().getStyle().setColor("Red");
		IssuesArea.setVisible(false);
		IssuesArea.getElement().getStyle().setMarginLeft(20.0, Unit.PCT);
		LayoutPanel.add(IssuesArea);

		// LoginButton
		loginButton.setConfirm(true);
		LayoutPanel.add(loginButton);
		LayoutPanel.add(saveButton);
		LayoutPanel.add(ipButton);

	}

	@Override
	public Widget asWidget() {
		return LayoutPanel;
	}

	public HasTapHandlers getLoginButton() {
		return loginButton;
	}

	public HTML getIssuesArea() {
		return IssuesArea;
	}

	@Override
	public String getuserName() {
		return userNameField.getValue();
	}

	@Override
	public String getpassword() {
		return passWord.getValue();
	}

	public void showBusy(boolean status) {
		if (status) {
			progressIndicator.setVisible(true);
		} else {
			progressIndicator.setVisible(false);
		}
	}

	public MTextBox getServerAddress() {
		return serverAddress;
	}

	public HasTapHandlers getSaveButton() {
		return saveButton;
	}

	@Override
	public void showIpChange(boolean status) {
		if (status) {
			widgetList.setVisible(false);
			ipConfigList.setVisible(true);
			loginButton.setVisible(false);
			saveButton.setVisible(true);
		} else {
			widgetList.setVisible(true);
			ipConfigList.setVisible(false);
			loginButton.setVisible(true);
			saveButton.setVisible(false);
		}
	}

	@Override
	public void showUpdatePassword(boolean status, String userName) {
		if (status) {
			isFirstTime=true;
			title.setVisible(true);
			title.setText("CREATE YOUR PASSWORD:");
			userNameField.setValue(userName);
			passWord.setValue("");
			passWord.setPlaceholder("New password");
			widgetList.add(confirmPassWord);
			loginButton.setText("Save");
		} else {
			isFirstTime=false;
			title.setVisible(false);
			userNameField.setPlaceholder("UserName");
			passWord.setPlaceholder("Password");
			widgetList.remove(confirmPassWord);
			loginButton.setText("LOGIN");
		}
	}

	@Override
	public String getconfirmPassword() {
		return confirmPassWord.getValue();
	}

}
