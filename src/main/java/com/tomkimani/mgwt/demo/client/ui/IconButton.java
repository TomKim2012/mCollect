package com.tomkimani.mgwt.demo.client.ui;

import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.tomkimani.mgwt.demo.client.css.ButtonsCss;

public class IconButton extends Button {
	private String text;
	private String  icon;
	private String txt="";
	public IconButton() {
		super((ButtonsCss) MGWTStyle.getTheme().getMGWTClientBundle().getButtonCss());
	}
	
	public IconButton(String icon,String text){
		String icn ="<i class='mgwt-Button-icon "+icon+"'></i>";
		if(text!=null){
			txt ="<span class='text'>"+text+"</span>";
		}
		getElement().setInnerHTML(icn+txt);
	}
	
	private void createButton(){
		String icn ="<i class='mgwt-Button-icon "+this.icon +"'></i>";
		String txt ="<span class='txt'>"+this.text+"</span>";
		getElement().setInnerHTML(icn+txt);
	}
	
	@Override
	public void setText(String text) {
		this.text=text;
		createButton();
	}
	
	public void setIcon(String style){
		 addStyleName(((ButtonsCss)css).IconButton()); //".gwt-button"
		 this.icon=style;
	}
	
	public void removeMinWidth() {
		addStyleName(((ButtonsCss)css).NoWidth());
	}
 }
