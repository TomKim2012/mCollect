package com.tomkimani.mgwt.demo.client.places;

import java.util.LinkedList;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.googlecode.gwtphonegap.client.contacts.Contact;
import com.tomkimani.mgwt.demo.client.customerSearch.CustomerSearchActivity.Customer;
import com.tomkimani.mgwt.demo.client.transactions.Transaction;

public class TransactionDetailPlace extends Place{

	private Transaction transaction;
	private Customer customer;
	private Contact contact;
	private Boolean isMiniStatement=false;
	private LinkedList<Customer> customerList;

	public TransactionDetailPlace(Transaction transaction) {
		this.transaction = transaction;
	}
	public TransactionDetailPlace() {
	}
	
	public TransactionDetailPlace(Customer customer, Contact contact) {
		this.customer=customer;
		this.contact = contact;
	}
	
	public TransactionDetailPlace(Customer customer, Boolean isMinistatement, Contact contact) {
		this.customer =customer;
		this.isMiniStatement = isMinistatement;
		this.contact = contact; 
	}
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public Boolean getIsMiniStatement() {
		return isMiniStatement;
	}
	
	public LinkedList<Customer> getCustomerList() {
		return customerList;
	}
	
	public Contact getContact() {
		return contact;
	}
	
	public static class TransactionDetailPlaceTokenizer implements PlaceTokenizer<TransactionDetailPlace> {

		@Override
		public TransactionDetailPlace getPlace(String token) {
			return new TransactionDetailPlace();
		}
		@Override
		public String getToken(TransactionDetailPlace place) {
			return "";
		}
	}
}
