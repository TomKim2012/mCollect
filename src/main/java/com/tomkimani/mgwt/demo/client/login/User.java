package com.tomkimani.mgwt.demo.client.login;

public interface User {
	String getUserId();

	String getGroup();

	String getFirstName();

	String getLastName();

	String getUserName();

	Boolean getAuthorize();

	Boolean getFirstTime();

	String getError();
	
	Boolean getisAllocated();
}
