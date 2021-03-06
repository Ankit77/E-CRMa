package com.symphony_ecrm;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
import com.symphony_ecrm.http.WSGetAppVersion;
import com.symphony_ecrm.register.RegisterFragment;
import com.symphony_ecrm.service.CustomerListService;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.symphony_home);
        Crittercism.initialize(getApplicationContext(), "013ad36d5316405e83389083e1f0929f00555300");
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        //this.getSupportActionBar().setCustomView(R.layout.home_actionbar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayUseLogoEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.getSupportActionBar().setIcon(android.R.color.transparent);
        prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        if (Util.isNetworkAvailable(SymphonyHome.this)) {
            AsyncCheckVersion asyncCheckVersion = new AsyncCheckVersion();
            asyncCheckVersion.execute();
        } else
        {
            AsyncRegisterGCM asyncRegisterGCM = new AsyncRegisterGCM();
            asyncRegisterGCM.execute();
        }

        if (E_CRM.getsInstance().getSharedPreferences().getBoolean("isregister", false)) {
            //Check time diffrence for Wipe Data
            long diff_wipedata = Calendar.getInstance().getTimeInMillis() - E_CRM.getsInstance().getSharedPreferences().getLong(Const.PREF_WIPEDATA, 0);
            if (diff_wipedata >= Const.WIPETIME) {
                Log.e(SymphonyHome.class.getSimpleName(), "WIPE IS CALL");
                Intent wipedataService = new Intent(SymphonyHome.this, WipeDataService.class);
                startService(wipedataService);
                SharedPreferences.Editor editor = E_CRM.getsInstance().getSharedPreferences().edit();
                editor.putLong(Const.PREF_WIPEDATA, Calendar.getInstance().getTimeInMillis());
                editor.commit();
            }

            long diff_syncdata = Calendar.getInstance().getTimeInMillis() - E_CRM.getsInstance().getSharedPreferences().getLong(Const.PREF_SYNC, 0);
            if (diff_syncdata >= Const.SYNCDATA_INTERVAL) {
                Log.e(SymphonyHome.class.getSimpleName(), "SYNC IS CALL");
                Intent syncdataService = new Intent(SymphonyHome.this, VisitsyncService.class);
                startService(syncdataService);
                SharedPreferences.Editor editor = E_CRM.getsInstance().getSharedPreferences().edit();
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
            Intent intent_custmorList = new Intent(this, CustomerListService.class);
            startService(intent_custmorList);
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


    public void showAlertDialog(Context context, final String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        try {
                            Intent viewIntent =
                                    new Intent("android.intent.action.VIEW",
                                            Uri.parse("https://play.google.com/store/apps/details?id=com.symphony_ecrm"));
                            startActivity(viewIntent);
                        } catch (Exception e) {
                            Toast.makeText(SymphonyHome.this, "Unable to Connect Try Again...",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }

    private class AsyncCheckVersion extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SymphonyUtils.displayProgressDialog(SymphonyHome.this);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = "http://61.12.85.74:800/eSampark_GetVersion.asp?NM=track_new&PASS=trak123";
            WSGetAppVersion wsGetAppVersion = new WSGetAppVersion();
            return wsGetAppVersion.executePorposeLst(url);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SymphonyUtils.dismissProgressDialog(progressDialog);
            if (!TextUtils.isEmpty(s)) {
                s = s.substring(1);
                float serverversion = Float.parseFloat(s);
                float appVersion = Float.parseFloat(SymphonyUtils.getAppVersion(SymphonyHome.this));
//                if (appVersion < serverversion) {
//                    showAlertDialog(SymphonyHome.this, "E-CRM needs an update. There's a new version of E-CRM available on PlayStore.");
//                } else {
                    AsyncRegisterGCM asyncRegisterGCM = new AsyncRegisterGCM();
                    asyncRegisterGCM.execute();
//                }
            } else {
                AsyncRegisterGCM asyncRegisterGCM = new AsyncRegisterGCM();
                asyncRegisterGCM.execute();
            }
        }
    }

}
