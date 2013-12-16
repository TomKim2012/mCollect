package com.tomkimani.mgwt.demo.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;

public class MyRequestBuilder{
	public static String serverAddress="localhost";
	public static String serverUrl= "http://"+serverAddress+"/PioneerServer/index.php/api/flexipay_server/";
//	private static String serverUrl= "http://192.168.0.135/PioneerServer/index.php/api/flexipay_server/";
	private String customUrl;
	private String format = "/format/json/";
	private RequestBuilder builder;
	
	public MyRequestBuilder(Method httpMethod, String customUrl) {
		this.customUrl = customUrl;
		builder = new RequestBuilder(httpMethod, serverUrl + this.customUrl+ this.format);
		builder.setHeader("Content-Type", "application/json");
		builder.setIncludeCredentials(true);
	}
	
	public static void setServerUrl(String serverUrl) {
		MyRequestBuilder.serverUrl = serverUrl;
	}
	
	public RequestBuilder getBuilder() {
		return builder;
	}
	
	
	
}