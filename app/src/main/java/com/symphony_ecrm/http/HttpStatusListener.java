package com.symphony_ecrm.http;

import com.symphony_ecrm.database.CheckData;

public interface HttpStatusListener extends HTTPConnectionListener {

	public void onVerifyStatus(Boolean status);
	public void onDistributerListLoad(Boolean status);
	public void onVerifyMobileStatus(Boolean status);
	public void onCheckStatus(CheckData checkData);
	public void onAddCustomerStatus(Boolean status);
	

	
}
