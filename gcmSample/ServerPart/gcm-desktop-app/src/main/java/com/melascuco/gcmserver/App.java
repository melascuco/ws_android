package com.melascuco.gcmserver;

import com.melascuco.gcmserver.vo.Content;

public class App {
	
	final static private String apiKey = "AIzaSyCwtPGtk7nmxmmm0Iw-gKRQWgl1RFxTMog";
	//final static private String regId  = "APA91bFqnQzp0z5IpXWdth1lagGQZw1PTbdBAD13c-UQ0T76BBYVsFrY96MA4SFduBW9RzDguLaad-7l4QWluQcP6zSoX1HSUaAzQYSmI93....";
	final static private String regId  = "APA91bFzDQaZF013K_rXaEUbnD__sWuftaJLDADqwr9Vf6Yz7Djd_v_Be2EMhTVdIz8ljUq-XDqDndhm1DDMWl7eDHM3-tEIhYBkJShBm0F9GP4XhkYvKwRczf56zyv7_bFIKBjliyYkkcm9vXXU1asXNKLUcJTf0imM5I9JDace1jBGKpp9S8oa9-duXSd4yvatr8FLTTYt";
	
	public static void main(String[] args) {
		System.out.println("Sending POST to GCM");
		Content content = createContent();
		POST2GCM.post(apiKey, content);
	}

	public static Content createContent() {
		Content c = new Content();
		c.addRegId(regId);
		c.createData("Test Title 2", "Test Message 2");
		return c;
	}
}