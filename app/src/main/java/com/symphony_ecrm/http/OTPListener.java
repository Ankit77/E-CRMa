package com.symphony_ecrm.http;

import com.symphony_ecrm.database.OTPData;

public interface OTPListener extends HTTPConnectionListener {

	public void onOtpReceived(OTPData otpData);
}
