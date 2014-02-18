package com.tomkimani.mgwt.demo.client.contacts;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.MSearchBox;
import com.googlecode.mgwt.ui.client.widget.ProgressIndicator;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;
import com.googlecode.mgwt.ui.client.widget.celllist.CellListWithHeader;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;
import com.tomkimani.mgwt.demo.client.base.BaseView;
import com.tomkimani.mgwt.demo.client.contacts.ContactActivity.IContactsView;
import com.tomkimani.mgwt.demo.client.customerSearch.CustomerSearchActivity.Customer;

public class ContactView extends BaseView implements IContactsView {

	private static ContactViewUiBinder uiBinder = GWT.create(ContactViewUiBinder.class);

	interface ContactViewUiBinder extends UiBinder<Widget, ContactView> {
	}

	@UiField MSearchBox searchBox;

	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField ProgressIndicator progressIndicator;

	@UiField(provided = true)
	CellListWithHeader<Customer> cellList;


	private Widget widget;

	@UiField
	LayoutPanel divIssues;
	@UiField
	InlineLabel spnError;

	public ContactView() {
		BasicCell<Customer> cell = new BasicCell<Customer>() {

			@Override
			public String getDisplayString(Customer model) {
				return model.getFullNames();
			}

			@Override
			public boolean canBeSelected(Customer model) {
				return true;
			}
		};
		
		cellList = new CellListWithHeader<Customer>(cell);
		
		widget = uiBinder.createAndBindUi(this);
		
		progressIndicator.getElement().setAttribute("style","margin:auto; margin-bottom: 140px");
		createContent(widget);
		
		searchBox.setPlaceHolder("Search by Customer Names, Phone Number or Reference code");
		
		//Issues
		spnError.getElement().setAttribute("style","margin:50px; font-size:22px; color:gray");
		

		backButton.setVisible(true);
	}
	
	@Override
	public Widget asWidget() {
		return super.asWidget();
	}


	@Override
	public void display(List<Customer> contacts) {
		cellList.getCellList().render(contacts);
		scrollPanel.refresh();
	}

	public MSearchBox getSearchBox() {
		return searchBox;
	}
	
	@Override
	public HasTapHandlers getBackButton() {
		return super.getBackButton();
	}
	
	public void showBusy(boolean status){
		if(status){
			progressIndicator.setVisible(true);
			cellList.setVisible(false);
		}else{
			progressIndicator.setVisible(false);
			cellList.setVisible(true);
		}
	}
	
	@Override
	public void showError(boolean status){
		if(status){
			divIssues.setVisible(true);
			cellList.setVisible(false);
		}else{
			divIssues.setVisible(false);
			cellList.setVisible(true);
		}
	}
	
	public HasCellSelectedHandler getCellList() {
		return cellList.getCellList();
	}
}
