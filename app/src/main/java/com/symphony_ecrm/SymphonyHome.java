package com.symphony_ecrm;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.symphony_ecrm.distributer.DistributerActivity;
import com.symphony_ecrm.register.RegisterFragment;
import com.symphony_ecrm.service.TimeTickService;
import com.symphony_ecrm.service.VisitsyncService;
import com.symphony_ecrm.service.WipeDataService;
import com.symphony_ecrm.sms.SMSService;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;
import com.symphony_ecrm.utils.Util;

import java.io.IOException;
import java.util.Calendar;


public class SymphonyHome extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private SharedPreferences prefs;
    private LocationManager mLocationManager;
    private GoogleApiClient googleApiClient;
    private E_CRM e_crm;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.symphony_home);
        Crittercism.initialize(getApplicationContext(), "013ad36d5316405e83389083e1f0929f00555300");
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        e_crm = (E_CRM) getApplicationContext();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        //this.getSupportActionBar().setCustomView(R.layout.home_actionbar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayUseLogoEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.getSupportActionBar().setIcon(android.R.color.transparent);
        prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        AsyncRegisterGCM asyncRegisterGCM = new AsyncRegisterGCM();
        asyncRegisterGCM.execute();

        if (e_crm.getSharedPreferences().getBoolean("isregister", false)) {
            //Check time diffrence for Wipe Data
            long diff_wipedata = Calendar.getInstance().getTimeInMillis() - e_crm.getSharedPreferences().getLong(Const.PREF_WIPEDATA, 0);
            if (diff_wipedata >= Const.WIPETIME) {
                Log.e(SymphonyHome.class.getSimpleName(), "WIPE IS CALL");
                Intent wipedataService = new Intent(SymphonyHome.this, WipeDataService.class);
                startService(wipedataService);
                SharedPreferences.Editor editor = e_crm.getSharedPreferences().edit();
                editor.putLong(Const.PREF_WIPEDATA, Calendar.getInstance().getTimeInMillis());
                editor.commit();
            }

            long diff_syncdata = Calendar.getInstance().getTimeInMillis() - e_crm.getSharedPreferences().getLong(Const.PREF_SYNC, 0);
            if (diff_syncdata >= Const.SYNCDATA_INTERVAL) {
                Log.e(SymphonyHome.class.getSimpleName(), "SYNC IS CALL");
                Intent syncdataService = new Intent(SymphonyHome.this, VisitsyncService.class);
                startService(syncdataService);
                SharedPreferences.Editor editor = e_crm.getSharedPreferences().edit();
                editor.putLong(Const.PREF_SYNC, Calendar.getInstance().getTimeInMillis());
                editor.commit();
            }
        }
        //Start service for checking wipe data && Sync Pending Data
        if (!Util.isMyServiceRunning(TimeTickService.class, SymphonyHome.this)) {
            Intent intent = new Intent(SymphonyHome.this, TimeTickService.class);
            startService(intent);
        }
    }

    private void loadData() {

        Intent intentLocationService = new Intent(this, SMSService.class);
        intentLocationService.setAction(SMSService.FETCH_LOCATION_INTENT);

        startService(intentLocationService);

        boolean isRegister = prefs.getBoolean("isregister", false);
        if (!isRegister) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.homeFragment, new RegisterFragment()).commitAllowingStateLoss();
        } else {
            Intent intent = new Intent(this, DistributerActivity.class);
            startActivity(intent);
            finish();
        }
        FragmentManager fm = getSupportFragmentManager();
        fm.removeOnBackStackChangedListener(new OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                // TODO Auto-generated method stub
                Log.e("REMOVED ", getFragmentManager().getBackStackEntryCount() + "");
            }


        });
        fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // if(getFragmentManager().getBackStackEntryCount() == 0) finish();
                Log.e("ADD ", getFragmentManager().getBackStackEntryCount() + "");
            }
        });
    }

    @Override
    public void onBackPressed() {
        boolean isRegister = prefs.getBoolean("isregister", false);
        if (isRegister) {
            getSupportFragmentManager().popBackStack();
            finish();
            System.exit(1);
        } else {
            super.onBackPressed();
        }
    }


    public void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(SymphonyHome.class.getSimpleName(), "All location settings are satisfied.");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(SymphonyHome.class.getSimpleName(), "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(SymphonyHome.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(SymphonyHome.class.getSimpleName(), "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(SymphonyHome.class.getSimpleName(), "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                loadData();
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        Log.i(SymphonyHome.class.getSimpleName(), "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(SymphonyHome.class.getSimpleName(), "User chose not to make required location settings changes.");
                        break;

                }
                break;
        }
    }


    private class AsyncRegisterGCM extends AsyncTask<Void, Void, String> {
        private String regId = "";
        private GoogleCloudMessaging gcm;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SymphonyUtils.displayProgressDialog(SymphonyHome.this);
        }

        @Override
        protected String doInBackground(Void... params) {
            regId = SymphonyGCMHome.getRegistrationId(SymphonyHome.this);
            if (TextUtils.isEmpty(regId)) {
                gcm = GoogleCloudMessaging.getInstance(SymphonyHome.this);
                try {
                    regId = gcm.register(SymphonyUtils.GCM_KEY);
                    SymphonyGCMHome.setRegistrationId(SymphonyHome.this, regId);
                } catch (IOException e) {
                    e.printStackTrace();
                    regId = "";
                }
            }
            Log.e(SymphonyGCMHome.class.getSimpleName(), "REGID : " + regId);
            return regId;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            SymphonyUtils.dismissProgressDialog(progressDialog);
            if (!TextUtils.isEmpty(s)) {
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    googleApiClient = new GoogleApiClient
                            .Builder(SymphonyHome.this)
                            .enableAutoManage(SymphonyHome.this, 34992, SymphonyHome.this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(SymphonyHome.this)
                            .addOnConnectionFailedListener(SymphonyHome.this)
                            .build();
                    locationChecker(googleApiClient, SymphonyHome.this);
                } else {
                    loadData();
                }
            } else {
                Util.showAlertDialog(SymphonyHome.this, getString(R.string.alert_somethingwrong));
            }
        }
    }

}
