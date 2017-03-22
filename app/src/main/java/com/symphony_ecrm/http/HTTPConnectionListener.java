package com.symphony_ecrm.http;

public interface HTTPConnectionListener {

	public void onTimeOut();
	
	public void onNetworkDisconnect();
}
