package com.tomkimani.mgwt.demo.client.contacts;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.contacts.Contact;
import com.googlecode.gwtphonegap.client.contacts.ContactError;
import com.googlecode.gwtphonegap.client.contacts.ContactFindCallback;
import com.googlecode.gwtphonegap.client.contacts.ContactFindOptions;
import com.googlecode.gwtphonegap.collection.shared.CollectionFactory;
import com.googlecode.gwtphonegap.collection.shared.LightArray;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.MSearchBox;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;
import com.tomkimani.mgwt.demo.client.ClientFactory;
import com.tomkimani.mgwt.demo.client.MyBeanFactory;
import com.tomkimani.mgwt.demo.client.MyDialogs;
import com.tomkimani.mgwt.demo.client.PioneerAppEntryPoint;
import com.tomkimani.mgwt.demo.client.base.BaseActivity;
import com.tomkimani.mgwt.demo.client.customerSearch.CustomerSearchActivity.Customer;
import com.tomkimani.mgwt.demo.client.places.ContactPlace;
import com.tomkimani.mgwt.demo.client.places.DashboardPlace;
import com.tomkimani.mgwt.demo.client.places.TransactionDetailPlace;

public class ContactActivity extends BaseActivity {

	private MyBeanFactory beanFactory;
	private final PhoneGap phoneGap;
	private final IContactsView display;
	private static LinkedList<Customer> customerList;
	private Boolean isMiniStatement = false;
	protected String searchTerm = "";
	private static LightArray<Contact> allContacts;

	private String title = "";
	private String givenName = "";
	private String familyName = "";
	private String middleName = "";
	private String formattedName = "";
	private String nickName = "";
	private String phone = "";

	protected Timer timer = new Timer() {
		@Override
		public void run() {
			onSearchTermEntered(searchTerm);
		}
	};

	public interface IContactsView extends IView {
		public void display(List<Customer> contacts);

		MSearchBox getSearchBox();

		HasTapHandlers getBackButton();

		HasCellSelectedHandler getCellList();

		void showError(boolean status);

	}

	public ContactActivity(ClientFactory clientFactory) {
		super(clientFactory);

		this.display = clientFactory.getContactDisplay();
		this.phoneGap = clientFactory.getPhonegap();
		this.beanFactory = GWT.create(MyBeanFactory.class);
	}

	public void onSearchTermEntered(String term) {

		if (term == null)
			return;

		/*if ("".equals(term)) {
			return;
		}*/

		display.showBusy(true);
		display.showError(false);
		PioneerAppEntryPoint.consoleLog(">> Search started for " + term);

		LightArray<String> fields = CollectionFactory.<String> constructArray();

		fields.push("name");
		fields.push("phoneNumbers");
		fields.push("displayName");
		fields.push("nickname");

		ContactFindOptions findOptions = new ContactFindOptions(term, true);
		// findOptions.setFilter("PF");
		// findOptions.setUpdatedSince(updatedSince)
		// findOptions.setMutiple(false);

		phoneGap.getContacts().find(fields, new ContactFindCallback() {
			@Override
			public void onSuccess(LightArray<Contact> contacts) {

				allContacts = contacts;
				PioneerAppEntryPoint
						.consoleLog("<<Search Finished Number of Results:"
								+ contacts.length());
				display.showBusy(false);
				customerList = new LinkedList<Customer>();
				
				if (contacts.length() == 0) {
					display.showError(true);
					return;
				}


				int length = contacts.length() > 50 ? 50 : contacts.length();

				for (int i = 0; i < length; i++) {
					
					PioneerAppEntryPoint
					.consoleLog("Started Looping "
							+ contacts.length());
					if (contacts.get(i).getNickName().contains("/")) {
						PioneerAppEntryPoint
						.consoleLog("Contact contains / "
								+ contacts.length());
						
						Customer cust = makeCustomer();
						if (contacts.get(i).getName() != null) {
							title = contacts.get(i).getName()
									.getHonoricPrefix() != null ? contacts
									.get(i).getName().getHonoricPrefix() : "";
							givenName = contacts.get(i).getName()
									.getGivenName() != null ? contacts.get(i)
									.getName().getGivenName() : "";
							familyName = contacts.get(i).getName()
									.getFamilyName() != null ? contacts.get(i)
									.getName().getFamilyName() : "";
							middleName = contacts.get(i).getName()
									.getMiddleName() != null ? contacts.get(i)
									.getName().getMiddleName() : "";
							formattedName = contacts.get(i).getName()
									.getFormatted();
							
							cust.setFullNames(formattedName);
							cust.setFirstName(givenName);
							cust.setLastName(middleName + " " + familyName);
							cust.setRefNo(title);
						}

						if (contacts.get(i).getNickName() != null) {
							nickName = contacts.get(i).getNickName() != null ? contacts
									.get(i).getNickName() : null;
							cust.setCustomerId(nickName);
						}
						cust.setCustomerId(nickName);
						if (contacts.get(i).getPhoneNumbers().length() > 0) {
							phone = contacts.get(i).getPhoneNumbers().get(0)
									.getValue();
							
							cust.setMobileNo(phone);
						}
						
						PioneerAppEntryPoint
						.consoleLog("Value of CustomerId >>>>"+cust.getCustomerId());
						
						if(cust.getCustomerId()!=""){
							PioneerAppEntryPoint
							.consoleLog("Customer Id is not null., inserting"+cust.getCustomerId());
							customerList.add(cust);
						}
					}

				}

				display.display(customerList);
			}

			@Override
			public void onFailure(ContactError error) {
				MyDialogs.alert("Error", "Error while searching for contacts");
			}
		}, findOptions);

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {

		panel.setWidget(display);

		addHandlerRegistration(display.getSearchBox().addKeyUpHandler(
				new KeyUpHandler() {
					@Override
					public void onKeyUp(KeyUpEvent event) {
						String txt = display.getSearchBox().getValue().trim();
						if ((!txt.equals(searchTerm) && txt.length() > 5)
								|| event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							searchTerm = txt;
							timer.cancel();
							timer.schedule(400);
							PioneerAppEntryPoint
									.consoleLog("Finished Typing >>>"
											+ searchTerm);
						}
					}
				}));

		addHandlerRegistration(display.getCellList().addCellSelectedHandler(
				new CellSelectedHandler() {
					@Override
					public void onCellSelected(CellSelectedEvent event) {
						Customer cust1 = customerList.get(event.getIndex());

						Contact contact = allContacts.get(event.getIndex());

						// MyDialogs.alert("Contact Object",
						// "We removed Contact: "+ contact.getNickName());

						if (isMiniStatement) {
							factory.getPlaceController().goTo(
									new TransactionDetailPlace(cust1, true,
											contact));
						} else {
							factory.getPlaceController().goTo(
									new TransactionDetailPlace(cust1, contact));
						}
					}
				}));

		addHandlerRegistration(display.getBackButton().addTapHandler(
				new TapHandler() {
					@Override
					public void onTap(TapEvent event) {
						factory.getPlaceController().goTo(new DashboardPlace());
					}
				}));

		Place place = factory.getPlaceController().getWhere();
		if (place instanceof ContactPlace) {
			ContactPlace contactPlace = (ContactPlace) place;
			isMiniStatement = contactPlace.getMiniStatement();
		}

		 /*
		 if(customerList != null){
		 MyDialogs.alert("Noted","Customer List is not Null"); }
		 */

		 /*
		 Contact contact1 = phoneGap.getContacts().create();
		 contact1.getPhoneNumbers().push( phoneGap.getContacts().getFactory()
		 .createContactField("home", "0729472421", true));
		 contact1.getName().setHonoricfPrefix("PF-001-02555");
		 contact1.setNickName("PB/02555");
		 contact1.getName().setFamilyName("Muriranja");
		 contact1.getName().setGivenName("Tom Kimani"); contact1.save();
		
		
		 * Contact contact1 = phoneGap.getContacts().create();
		 * contact1.getPhoneNumbers().push( phoneGap.getContacts().getFactory()
		 * .createContactField("home", "0729472421", true));
		 * contact1.getName().setHonoricfPrefix("PF-001-02555");
		 * //contact1.setNickName("PB/05020");
		 * contact1.getName().setFamilyName("Gumisirize");
		 * contact1.getName().setGivenName("Daniel woiye"); contact1.save();
		 */

		// clear the screen
		//onSearchTermEntered("");

	}

	public Customer makeCustomer() {
		AutoBean<Customer> customer = beanFactory.Customer();
		return customer.as();
	}

}
