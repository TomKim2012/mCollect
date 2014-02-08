package com.tomkimani.mgwt.demo.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ContactPlace extends Place {
	Boolean miniStatement=false;
	
	public ContactPlace(Boolean miniStatement) {
		this.miniStatement = miniStatement;
	}
	public ContactPlace() {
		// TODO Auto-generated constructor stub
	}
	
	public Boolean getMiniStatement() {
		return miniStatement;
	}
	
	public static class ContactPlaceTokenizer implements PlaceTokenizer<ContactPlace> {
		@Override
		public ContactPlace getPlace(String token) {
			return new ContactPlace();
		}

		@Override
		public String getToken(ContactPlace place) {
			return "";
		}

	}
}