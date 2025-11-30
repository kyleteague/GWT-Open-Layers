package com.bounce.gwtopenlayers.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

	void sendDate(Date now, AsyncCallback<Long> asyncCallback);
}
