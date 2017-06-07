package com.symphony_ecrm.register;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.symphony_ecrm.R;
import com.symphony_ecrm.database.CheckData;
import com.symphony_ecrm.database.DB;
import com.symphony_ecrm.database.OTPData;
import com.symphony_ecrm.distributer.DistributerActivity;
import com.symphony_ecrm.http.HttpManager;
import com.symphony_ecrm.http.HttpStatusListener;
import com.symphony_ecrm.http.OTPListener;
import com.symphony_ecrm.service.CustomerListService;

import java.text.SimpleDateFormat;

public class VerifyFragment extends Fragment {


    private Button verifyBtn;
    private Button resendBtn;
    private EditText otpField;
    private String userMobileNumber;
    private String userName;
    private String registrationId;
    private String timeStamp;
    private String otpNumber;


    private TextView verifyStatus;
    private ImageView verifyStatusIcon;
    private LinearLayout verifyStatusLayout;
    private LinearLayout otpLayout;

    private TextView verifyRegistrationText;
    private Button nextBtn;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private ProgressDialog mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.verify_fragment, container, false);
        verifyBtn = (Button) v.findViewById(R.id.verifyBtn);
        resendBtn = (Button) v.findViewById(R.id.resendBtn);
        otpField = (EditText) v.findViewById(R.id.otpField);

        verifyStatus = (TextView) v.findViewById(R.id.verifyStatus);
        verifyStatusIcon = (ImageView) v.findViewById(R.id.verifyStatusIcon);
        verifyStatusLayout = (LinearLayout) v.findViewById(R.id.verifyStatusLayout);
        otpLayout = (LinearLayout) v.findViewById(R.id.otpLayout);

        verifyRegistrationText = (TextView) v.findViewById(R.id.verifyRegistrationText);
        nextBtn = (Button) v.findViewById(R.id.nextBtn);


        prefs = getActivity().getSharedPreferences(getString(R.string.app_name), getActivity().MODE_PRIVATE);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mProgressBar = new ProgressDialog(getActivity());
        mProgressBar.setTitle("Verifying OTP");
        mProgressBar.setMessage("Please wait  ...");
        mProgressBar.setProgressStyle(mProgressBar.STYLE_SPINNER);
        mProgressBar.hide();
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setCancelable(false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userMobileNumber = bundle.getString("usernumber");
            userName = bundle.getString("username");
            registrationId = bundle.getString("registrationId");
            timeStamp = bundle.getString("timestamp");
            otpNumber = bundle.getString("otpdata");
            otpField.setText(otpNumber);
        }
        verifyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                closeKeyBoard();
                if (TextUtils.isEmpty(otpField.getText().toString()))
                    Toast.makeText(getActivity(), "Please enter OTP code", Toast.LENGTH_LONG).show();
                else {
                    // otp number
                    Log.d("VERFY  SCREEN", otpField.getText().toString() + " " + userMobileNumber);
                    mProgressBar.show();
                    HttpManager httpManager = new HttpManager(getActivity());
                    httpManager.verifyOTP(otpField.getText().toString(), userMobileNumber, registrationId, new HttpStatusListener() {

                        @Override
                        public void onAddCustomerStatus(Boolean status) {

                        }

                        @Override
                        public void onVerifyStatus(Boolean status) {
                            // TODO Auto-generated method stub
                            Log.e("INSIDE LISTENER", status + "");
                            editor = prefs.edit();
                            editor.putBoolean("isregister", status);
                            if (status == true) {
                                mProgressBar.dismiss();
                                editor.putString("usermobilenumber", userMobileNumber);
                                editor.putBoolean("isFirstTime", true);
                                ContentValues values = new ContentValues();
                                values.put(DB.USER_MOBILE, userMobileNumber);
                                values.put(DB.USER_NAME, userName);
                                values.put(DB.USER_TIMESTAMP, timeStamp);
                                getActivity().getContentResolver().insert(

                                        Uri.parse("content://com.symphony_ecrm.database.DBProvider/addNewUser"),

                                        values);
                            } else {
                                mProgressBar.dismiss();
                            }
                            setVerifyOTPMessage(status);
                            editor.commit();
                        }

                        private ContentValues ContentValues() {
                            // TODO Auto-generated method stub
                            return null;
                        }

                        @Override
                        public void onDistributerListLoad(Boolean status) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onVerifyMobileStatus(Boolean status) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onTimeOut() {
                            // TODO Auto-generated method stub
                            mProgressBar.dismiss();

                            Toast.makeText(getActivity(), "Request Timeout occurs , please try again", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onNetworkDisconnect() {
                            // TODO Auto-generated method stub
                            mProgressBar.dismiss();

                            Toast.makeText(getActivity(), "Network not available at this moment", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCheckStatus(CheckData checkData) {
                            // TODO Auto-generated method stub

                        }

                    });


                }


            }


        });

        resendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                closeKeyBoard();
                HttpManager httpManger = new HttpManager(getActivity());
                httpManger.registerDeviceId(

                        userName,
                        userMobileNumber,
                        userMobileNumber + "@gmail.com",
                        registrationId,
                        new OTPListener() {

                            @Override
                            public void onOtpReceived(OTPData otpData) {
                                // TODO Auto-generated method stub


                                Log.e("RegisterFragment ", "OTP RECEIVED " +


                                        otpData.getOtp() + " " +
                                        otpData.isStatus());
                                mProgressBar.dismiss();

                                if (otpData.isStatus()) {
                                    otpField.setText(otpData.getOtp());
                                } else {
                                    Toast.makeText(getActivity(), "Not able to get the OTP", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onTimeOut() {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onNetworkDisconnect() {
                                // TODO Auto-generated method stub

                            }
                        });

            }


        });


        nextBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                closeKeyBoard();
                boolean isRegister = prefs.getBoolean("isregister", false);
                if (isRegister) {
                    Intent intent_custmorList = new Intent(getActivity(), CustomerListService.class);
                    getActivity().startService(intent_custmorList);
                    Intent intent = new Intent(getActivity(), DistributerActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }

            }


        });

    }


    private void setVerifyOTPMessage(boolean status) {
        if (status) {
            verifyStatusIcon.setBackgroundResource(R.drawable.success);
            verifyStatus.setText(R.string.verify_success_text);
            otpField.setVisibility(TextView.GONE);
            resendBtn.setVisibility(Button.GONE);
            nextBtn.setVisibility(Button.VISIBLE);
            verifyBtn.setVisibility(Button.GONE);
            verifyRegistrationText.setVisibility(TextView.GONE);

        } else {
            verifyStatusIcon.setBackgroundResource(R.drawable.failed);
            verifyStatus.setText(R.string.verify_failed_text);
            verifyRegistrationText.setVisibility(TextView.VISIBLE);
            nextBtn.setVisibility(Button.GONE);
        }

        verifyStatusLayout.setVisibility(LinearLayout.VISIBLE);
    }

    private void closeKeyBoard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }


    }


}
