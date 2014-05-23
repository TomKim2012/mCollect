package com.tomkimani.mgwt.demo.client.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.contacts.Contact;
import com.googlecode.gwtphonegap.client.contacts.ContactError;
import com.googlecode.gwtphonegap.client.contacts.ContactFindCallback;
import com.googlecode.gwtphonegap.client.contacts.ContactFindOptions;
import com.googlecode.gwtphonegap.client.notification.ConfirmCallback;
import com.googlecode.gwtphonegap.collection.shared.CollectionFactory;
import com.googlecode.gwtphonegap.collection.shared.LightArray;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.tomkimani.mgwt.demo.client.ClientFactory;
import com.tomkimani.mgwt.demo.client.MyBeanFactory;
import com.tomkimani.mgwt.demo.client.MyDialogs;
import com.tomkimani.mgwt.demo.client.MyRequestBuilder;
import com.tomkimani.mgwt.demo.client.MyRequestCallback;
import com.tomkimani.mgwt.demo.client.PioneerAppEntryPoint;
import com.tomkimani.mgwt.demo.client.base.BaseActivity;
import com.tomkimani.mgwt.demo.client.customerSearch.CustomerSearchActivity.Customer;
import com.tomkimani.mgwt.demo.client.customerSearch.CustomerSearchActivity.CustomerList;
import com.tomkimani.mgwt.demo.client.login.LoginActivity;
import com.tomkimani.mgwt.demo.client.places.ContactPlace;
import com.tomkimani.mgwt.demo.client.places.TransactionsPlace;

public class DashboardActivity extends BaseActivity {
	private static boolean hasSynchronised = false;
	// TransactionsActivity transaction= new TransactionsActivity(factory);
	private final PhoneGap phoneGap;
	private MyBeanFactory beanFactory;
	private IDashboardView view;

	public interface IDashboardView extends IView {
		HasTapHandlers getBtnDeposit();

		HasTapHandlers getBtnStatement();

		void setSyncStatus(String text);
	}

	public DashboardActivity(ClientFactory factory) {
		super(factory);
		this.beanFactory = GWT.create(MyBeanFactory.class);
		this.phoneGap = factory.getPhonegap();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view = factory.getDashboardView();
		setView(view);

		super.start(panel, eventBus);

		addHandlerRegistration(view.getTransactionButton().addTapHandler(
				new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						factory.getPlaceController().goTo(
								new TransactionsPlace());
					}
				}));

		addHandlerRegistration(view.getBtnDeposit().addTapHandler(
				new TapHandler() {
					@Override
					public void onTap(TapEvent event) {
						factory.getPlaceController().goTo(new ContactPlace());
					}
				}));

		addHandlerRegistration(view.getBtnStatement().addTapHandler(
				new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						factory.getPlaceController().goTo(
								new ContactPlace(true));
					}
				}));
		panel.setWidget(view);
		
		if(LoginActivity.loggedUserGroup.equals("Admin")){
			if (!hasSynchronised) {
				SynchronizeContacts();
			}else{
				view.setSyncStatus("Contacts Synced");
			}
			
		}else{
			//No status
			view.setSyncStatus("");
		}
	}

	public void SynchronizeContacts() {
		String term = "";
		PioneerAppEntryPoint.consoleLog(">>Started Synchronisation Check");

		LightArray<String> fields = CollectionFactory.<String> constructArray();
		fields.push("name");
		fields.push("nickname");

		ContactFindOptions findOptions = new ContactFindOptions(term, true);
		// findOptions.setFilter("PF/*");

		phoneGap.getContacts().find(fields, new ContactFindCallback() {
			@Override
			public void onSuccess(LightArray<Contact> contacts) {
				int length = contacts.length();
				Integer clientCount = 0;

				if (length == 0) {
					// display.showError(true);
					PioneerAppEntryPoint.consoleLog("No Contacts set returned");
				}

				for (int i = 0; i < length; i++) {
					if (contacts.get(i).getNickName() != null) {

						// PioneerAppEntryPoint.consoleLog("NickName:"+
						// contacts.get(i).getNickName());
						if (contacts.get(i).getNickName().contains("/")) {
							clientCount += 1;
						}
					}
				}
				hasSynchronised=true;
				PioneerAppEntryPoint.consoleLog("Local Client Count:"
						+ clientCount);

				performSyncCheck(clientCount);

			}

			@Override
			public void onFailure(ContactError error) {
				MyDialogs.alert("Error", "Problem syncing contacts");

			}
		}, findOptions);

	}

	/*
	 * check with the server difference in contacts
	 */
	private String contactCount="";
	private void performSyncCheck(final Integer clientCount) {
		String postData="";
		view.setSyncStatus("Checking..");
		String customUrl = "customerSyncCheck";
		
		PioneerAppEntryPoint.consoleLog("Pushed Customer count to server as: "
				+ clientCount);

		JSONObject jrequest = new JSONObject();
		
		if(clientCount==0){
			contactCount="Zero";
		}else{
			contactCount = Integer.toString(clientCount);
		}
		
		jrequest.put("contactCount", new JSONString(contactCount));
		postData = jrequest.toString();
		


		MyRequestBuilder rqs = new MyRequestBuilder(RequestBuilder.POST,
				customUrl);

		try {
			Request request = rqs.getBuilder().sendRequest(postData,
					new MyRequestCallback() {
						private SyncResult result;

						public void onResponseReceived(Request request,
								Response response) {
							if (200 == response.getStatusCode()) {
								result = deserializeFromJson(response.getText());
								final Integer countDifference = result
										.getCountDifference();
								PioneerAppEntryPoint
										.consoleLog("Count Difference:"
												+ countDifference);
								
								if(countDifference<0){
									//Show contacts In Sync
									view.setSyncStatus("Sync unusual");
									return;
								}
								
								if(countDifference==0){
									//Show contacts In Sync
									view.setSyncStatus("Contacts Synced");
									return;
								}
								
								MyDialogs
										.confirm(
												"Sync Message",
												countDifference
														+ " Customer(s) need to be Synced. Sync now?",
												new ConfirmCallback() {

													@Override
													public void onConfirm(
															int button) {
														if (button == 1) {
															performSync(clientCount,countDifference);
														}else{
															view.setSyncStatus("Sync Cancelled");
														}
													}
												});
							} else {
								view.setSyncStatus("Sync Failed");
								PioneerAppEntryPoint
										.consoleLog("Sync Failed - 404");
							}
						}

					});
		} catch (RequestException e) {
			view.setSyncStatus("Sync Failed");
			PioneerAppEntryPoint.consoleLog("Sync Failed - 404");
		}

		// showTransactionComplete();
	}

	private void performSync(Integer clientCount, Integer countDifference) {
		view.setSyncStatus("Getting data...");
		String customUrl = "customerSync";
		PioneerAppEntryPoint.consoleLog("Started syncronising.."
				+ countDifference + "contacts");

		JSONObject jrequest = new JSONObject();
		String contactCount = Integer.toString(clientCount);
		String countString = Integer.toString(countDifference);

		jrequest.put("contactCount", new JSONString(contactCount));
		jrequest.put("countDifference", new JSONString(countString));

		MyRequestBuilder rqs = new MyRequestBuilder(RequestBuilder.POST,
				customUrl);
		
		rqs.getBuilder().setTimeoutMillis(60000);
		String postData = jrequest.toString();
		try {
			//view.showBusy(true); //Show Syncing
			Request request = rqs.getBuilder().sendRequest(postData,
					new MyRequestCallback() {

						private List<Customer> custList;

						@Override
						public void onResponseReceived(Request request,
								Response response) {
							super.onResponseReceived(request, response);
							view.setSyncStatus("Saving ...");
							custList = new ArrayList<Customer>();

							if (response.getText().isEmpty()) {
								PioneerAppEntryPoint.consoleLog("The server returned no new customers.");

//								view.getIssuesArea().setText(
//										"Customer Records not Found");
//								view.getIssuesArea().setVisible(true);
								return;
							}

							CustomerList lst = customersFromJson("{\"customerList\": "
									+ response.getText() + "}");
							custList = lst.getCustomerList();

							createContacts(custList);
						}
					});
		} catch (RequestException e) {
			MyDialogs.alert(MyDialogs.NETWORK_ERROR_MESSAGE,
					MyDialogs.NETWORK_ERROR_TITLE);
		}
	}

	private void createContacts(List<Customer> custList) {
		PioneerAppEntryPoint.consoleLog("Started creating contacts on phone");
		
		int counter = 0;
		for(Customer cust: custList){
		    Contact contact1 = phoneGap.getContacts().create();
		    contact1.getName().setFormatted(cust.getFullNames());
		    contact1.getPhoneNumbers().push(phoneGap.getContacts().getFactory().createContactField("home", cust.getMobileNo(), true));
			contact1.getName().setHonoricfPrefix(cust.getRefNo());
			contact1.setNickName(cust.getCustomerId());
			contact1.getName().setFamilyName(cust.getLastName());
			contact1.getName().setMiddleName(cust.getMiddleName());
			contact1.getName().setGivenName(cust.getFirstName()+" "+cust.getMiddleName());
			contact1.save();
			view.setSyncStatus(counter +"/"+custList.size() + " Saved");
		}
		
		view.setSyncStatus("Contacts Synced");
		
		MyDialogs.alert("Success", "Success!"+custList.size()+" contacts have been saved");
	}

	public interface SyncResult {
		Integer getCountDifference();
	}

	SyncResult deserializeFromJson(String json) {
		AutoBean<SyncResult> bean = AutoBeanCodex.decode(beanFactory,
				SyncResult.class, json);
		return bean.as();
	}
	
	CustomerList customersFromJson(String json) {
		// System.out.println(json);
		AutoBean<CustomerList> bean = AutoBeanCodex.decode(beanFactory,
				CustomerList.class, json);
		return bean.as();
	}
}
