package com.symphony_ecrm.sms;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.symphony_ecrm.R;
import com.symphony_ecrm.database.CheckData;
import com.symphony_ecrm.database.DB;
import com.symphony_ecrm.distributer.DistributerActivity;
import com.symphony_ecrm.http.HttpManager;
import com.symphony_ecrm.http.HttpStatusListener;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SMSService extends Service implements LocationListener {

    public static String addressLatLng;
    public static Location location;
    public static Location location1;
    private SharedPreferences prefs;
    private HttpManager httpManger;
    public static final String SEND_GEO_SMS_INTENT = "SEND_GEO_SMS_INTENT";
    public static final String FETCH_LOCATION_INTENT = "FETCH_LOCATION_INTENT";
    public static final String SEND_REGISTER_USER_INTENT = "SEND_REGISTER_USER_INTENT";
    public static final String SEND_CHECK_SMS_INTENT = "SEND_CHECK_SMS_INTENT";
    public static String CENTRAL_MOBILE_NUMBER = "9510070111";
    private static String USER_MOBILE_NUMBER;
    public static final String GEO_LOCATION_FAILED = "com.symphony_ecrm.GEO_LOCATION_FAILED";
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 40;
    // Update frequency in milliseconds

    private LocationManager mLocationManager;
    private Intent mintent;


    @Override
    public void onCreate() {

        servicesConnected();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
            // Do something with the recent location fix
            //  otherwise wait for the update below
            addressLatLng = location.getLatitude() + "," + location.getLongitude();
            this.location = location;
            //Toast.makeText(SMSService.this, "Last Location - " + addressLatLng, Toast.LENGTH_LONG).show();
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent != null) {
            if (FETCH_LOCATION_INTENT == intent.getAction()) {
                Log.e(SMSService.class.getSimpleName(), "Location is Change");
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            } else {
                mintent = intent;
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    addressLatLng = location.getLatitude() + "," + location.getLongitude();
                    this.location = location;
                    callIntentMethod(intent);
                    //Toast.makeText(SMSService.this, "Last Location - " + addressLatLng, Toast.LENGTH_LONG).show();
                } else {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }

            }
        }
        return this.START_STICKY;
    }

    private void callIntentMethod(Intent intent) {
        if (intent != null) {
            String versionNumber = SymphonyUtils.getAppVersion(this);
            if (SEND_CHECK_SMS_INTENT == intent.getAction()) {
                prefs = SMSService.this.getSharedPreferences(getString(R.string.app_name), SMSService.this.MODE_PRIVATE);
                USER_MOBILE_NUMBER = prefs.getString("usermobilenumber", null);
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss a");
                    String currentDateandTime = sdf.format(new Date()).replace(" ", "");
                    currentDateandTime = currentDateandTime.replace(".", "");
                    SimpleDateFormat timeStampFormat = new SimpleDateFormat(Const.DATETIMEFORMAT);
                    String timeStamp = timeStampFormat.format(new Date());
                    timeStamp = timeStamp.replace(".", "");
                    StringBuilder smsCheck = new StringBuilder();
                    if (bundle.getBoolean("checkstatus"))
                        smsCheck.append("CHECKIN,");
                    else
                        smsCheck.append("CHECKOUT,");

                    smsCheck.append(USER_MOBILE_NUMBER + ",");
                    smsCheck.append(addressLatLng + ",");
                    smsCheck.append(currentDateandTime);
                    smsCheck.append(",v" + (versionNumber != null ? versionNumber : "0.0"));
                    if (addressLatLng == null || TextUtils.isEmpty(addressLatLng)) {
                        addressLatLng = "";
                        Intent locationFailedIntent = new Intent();
                        locationFailedIntent.setAction(GEO_LOCATION_FAILED);
                        locationFailedIntent.putExtra("locationstatus", true);
                        sendBroadcast(locationFailedIntent);
                    } else {
                        new SendSMSAsyc().execute(SEND_CHECK_SMS_INTENT,
                                smsCheck.toString(),
                                String.valueOf(bundle.getBoolean("checkstatus")),
                                timeStamp
                        );
                    }
                }

            } else if (SEND_GEO_SMS_INTENT == intent.getAction()) {
                prefs = SMSService.this.getSharedPreferences(getString(R.string.app_name), SMSService.this.MODE_PRIVATE);
                USER_MOBILE_NUMBER = prefs.getString("usermobilenumber", null);
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    StringBuilder smsCheck = new StringBuilder();
                    smsCheck.append("GEOCODE,");
                    smsCheck.append(USER_MOBILE_NUMBER + ",");
                    smsCheck.append(bundle.getString("distid") + ",");
                    smsCheck.append(addressLatLng + ",");
                    smsCheck.append(",v" + (versionNumber != null ? versionNumber : "0.0"));
                    if (addressLatLng == null || TextUtils.isEmpty(addressLatLng)) {
                        addressLatLng = null;
                        Intent locationFailedIntent = new Intent();
                        locationFailedIntent.setAction(GEO_LOCATION_FAILED);
                        locationFailedIntent.putExtra("locationstatus", true);
                        sendBroadcast(locationFailedIntent);
                    } else {
                        Toast.makeText(this, "Sending " + smsCheck.toString(), Toast.LENGTH_SHORT).show();
                        new SendSMSAsyc().execute(SEND_GEO_SMS_INTENT,
                                smsCheck.toString(),
                                bundle.getString("distkey"));
                    }
                }
            } else if (SEND_REGISTER_USER_INTENT == intent.getAction()) {
                Bundle bundle = intent.getExtras();
                StringBuilder smsRegister = new StringBuilder();
                if (bundle != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss a");
                    String currentDateandTime = sdf.format(new Date()).replace(" ", "");

                    currentDateandTime = currentDateandTime.replace(".", "");
                    new SendSMSAsyc().execute(SEND_REGISTER_USER_INTENT,
                            bundle.getString("usernumber"),
                            bundle.getString("username"),
                            currentDateandTime);
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.e("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {

            Log.e("Location Updates",
                    "Google Play services is NOT available.");
            return false;
        }
    }

    public class SendSMSAsyc extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            prefs = SMSService.this.getSharedPreferences(getString(R.string.app_name), SMSService.this.MODE_PRIVATE);
            String centralNumber = prefs.getString("centralmobilenumer", null);
            if (centralNumber == null)
                CENTRAL_MOBILE_NUMBER = "9510070111";
            else
                CENTRAL_MOBILE_NUMBER = centralNumber;
            if (CENTRAL_MOBILE_NUMBER == null || TextUtils.isEmpty(CENTRAL_MOBILE_NUMBER)) {
                this.cancel(true);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            String methodCall = params[0];
            String smsBody = params[1];
            String checkStatus = null;
            String distKey = null;
            String userName = null;
            String userMobile = null;
            String timeStamp = null;
            if (methodCall.equals(SEND_GEO_SMS_INTENT)) {
                distKey = params[2];
            } else if (methodCall.equals(SEND_CHECK_SMS_INTENT)) {
                checkStatus = params[2];
            }

            if (smsBody != null) {
                String checkTime[] = smsBody.split(",");
                timeStamp = params[3];
                ContentValues checkStatusValue = new ContentValues();
                checkStatusValue.put(DB.CHECK_SMS, smsBody);
                checkStatusValue.put(DB.CHECK_TIMESTAMP, timeStamp);
                if (distKey != null)
                    checkStatusValue.put(DB.DIST_CHECK_KEY, distKey);

                if (checkStatus != null)
                    checkStatusValue.put(DB.CHECK_STATUS, checkStatus);

                checkStatusValue.put(DB.CHECK_FLAG, 1);
                final Uri insert = getBaseContext().getContentResolver().insert(Uri.parse("content://com.symphony_ecrm.database.DBProvider/addCheckStatus"),
                        checkStatusValue);
                Log.e("SMSService", "sending on webservice update this id ->>>> " + insert.getLastPathSegment());
                httpManger = new HttpManager(SMSService.this);
                httpManger.sendCheckStatus(smsBody, insert.getLastPathSegment(), new HttpStatusListener() {
                    CheckData checkData = new CheckData();

                    @Override
                    public void onAddCustomerStatus(Boolean status) {

                    }

                    @Override
                    public void onVerifyStatus(Boolean status) {
                        // TODO Auto-generated method stub
                        Log.e(SMSService.class.getSimpleName(), "onVerifyStatus");

                    }

                    @Override
                    public void onDistributerListLoad(Boolean status) {
                        // TODO Auto-generated method stub
                        Log.e(SMSService.class.getSimpleName(), "onDistributerListLoad");
                    }

                    @Override
                    public void onVerifyMobileStatus(Boolean status) {
                        // TODO Auto-generated method stub
                        Log.e(SMSService.class.getSimpleName(), "onVerifyMobileStatus");
                    }

                    @Override
                    public void onCheckStatus(CheckData checkData) {
                        // TODO Auto-generated method stub
                        this.checkData = checkData;
                        checkData.setCheckId(insert.getLastPathSegment());
                        checkData.setCheckFlag(false);
                        updateCheckFlag(checkData);
                    }

                    @Override
                    public void onTimeOut() {
                        // TODO Auto-generated method stub
                        checkData.setCheckId(insert.getLastPathSegment());
                        checkData.setCheckStatus(false);
                        checkData.setCheckFlag(true);
                        updateCheckFlag(checkData);
                    }

                    @Override
                    public void onNetworkDisconnect() {
                        // TODO Auto-generated method stub
                        checkData.setCheckId(insert.getLastPathSegment());
                        checkData.setCheckStatus(false);
                        checkData.setCheckFlag(true);
                        updateCheckFlag(checkData);
                    }
                });
            }
            return null;
        }
    }


    private int updateCheckFlag(CheckData checkData) {


        ContentValues values = new ContentValues();
        values.put(DB.CHECK_STATUS, checkData.isCheckStatus() == true ? 1 : 0); // 0 -> 0 success ,  		1 -> failed
        values.put(DB.CHECK_FLAG, checkData.isCheckFlag() == true ? 1 : 0); // 0 -> successfully synced , 1 -> not synced
        values.put(DB.CHECK_LAT, checkData.getCheckLat());
        values.put(DB.CHECK_LNG, checkData.getCheckLng());
        values.put(DB.DIST_CHECK_KEY, checkData.getCheckDistKey());
        values.put(DB.DIST_CHECK_NAME, checkData.getCheckDistName());
        values.put(DB.CHECK_ID, checkData.getCheckId());


        int updateRes = getBaseContext().getContentResolver().
                update(
                        Uri.parse("content://com.symphony_ecrm.database.DBProvider/updateCheckFlagStatus"),
                        values,
                        DB.CHECK_ID + " = " + checkData.getCheckId(),
                        null

                );

        //Log.e("SyncManager :: Check Key " , checkData.getCheckId()+"");

        return updateRes;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            addressLatLng = location.getLatitude() + "," + location.getLongitude();
            this.location = location;
            //Toast.makeText(SMSService.this, addressLatLng, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DistributerActivity.LOCATION_RECEIVER);
            intent.putExtra("current_location", addressLatLng);
            sendBroadcast(intent);
            mLocationManager.removeUpdates(this);
            if (mintent != null) {
                //  Toast.makeText(SMSService.this, addressLatLng, Toast.LENGTH_LONG).show();
                //callIntentMethod(mintent);
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
