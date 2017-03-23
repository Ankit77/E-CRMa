package com.symphony_ecrm.distributer;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.database.DB;
import com.symphony_ecrm.register.HomeFragment;
import com.symphony_ecrm.report.SymphonyReport;
import com.symphony_ecrm.sms.SMSService;
import com.symphony_ecrm.sms.SyncAlaram;
import com.symphony_ecrm.sms.SyncManager;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DistributerActivity extends AppCompatActivity implements DistributerActivityListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int GPS_RESULT = 100;
    public String messageText;
    private int GET_DISTRIBUTER_IMAGE_REQUEST = 120;
    public static final String LOCATION_RECEIVER = "com.symphony_ecrm.locationreceiver";
    private LocationManager mLocationManager;
    private CheckStatusListener mCheckStatusListener;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    private static String currentDistId;
    private static String currentDistKey;
    private static String currentDistName;
    private AlarmManager alramManager;
    private PendingIntent alramPendingIntent;
    private DistributerInfo distInfoFrag;
    private E_CRM e_sampark;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String cacsId;
    private String crmActId;
    private HomeFragment homeFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distributer_home);
        e_sampark = (E_CRM) getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCheckStatusListener = (CheckStatusListener) getSupportFragmentManager().findFragmentById(R.id.checkStatusFragment);
        startSyncAlram();
        SymphonyUtils.startWipeDataAlram(this);
        e_sampark.getSharedPreferences().edit().putString(Const.USERTYPE, Const.USER_CACS).commit();
        homeFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.distHome, homeFragment, HomeFragment.class.getSimpleName())
                .commit();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        loadNotificationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        loadNotificationIntent(intent);
    }

    private void loadNotificationIntent(final Intent intent) {
        if (intent.getExtras() != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String notificationtype = intent.getExtras().get(Const.KEY_NOTIFICATIONTYPE).toString();
                    if (notificationtype.equalsIgnoreCase(Const.TYPE_VISIT)) {
                        cacsId = intent.getExtras().get(Const.KEY_CACSVISITID).toString();
                        crmActId = intent.getExtras().get(Const.KEY_CRMACTID).toString();
                        if (homeFragment != null) {
                            homeFragment.showNextActionDateDiglog(cacsId, crmActId);
                        }
                    }
                }
            }, 1000);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.symphony_exit:
                System.exit(1);
                return true;
            case R.id.distributer_listview:
                onDistributerListSelect();
                return true;
            case R.id.symphony_settings:
                onSettingsSelect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDistributerListSelect() {
        Log.e("MENU ITEM  ", "distributer list selected");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DistributerList distList = new DistributerList();
        ft.replace(R.id.distHome, distList).addToBackStack("distlist").commit();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_RESULT) {
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (mCheckStatusListener != null)
                    mCheckStatusListener.onGPSOK();
                Log.e("gps", resultCode + " " + requestCode + " " +
                        mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                );
            }
        } else if (requestCode == GET_DISTRIBUTER_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (distInfoFrag != null)
                        distInfoFrag.setGeoLocaitonBtnEnable(true);
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap bitmap = extras.getParcelable("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte imageInByte[] = stream.toByteArray();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss a");
                        String currentDateandTime = sdf.format(new Date()).replace(" ", "");
                        currentDateandTime = currentDateandTime.replace(".", "");
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String timeStampSort = timeStampFormat.format(new Date());
                        if (prefs != null) {
                            currentDistId = prefs.getString("tdistid", null);
                            currentDistKey = prefs.getString("tdistkey", null);
                            currentDistName = prefs.getString("tdistname", null);
                        }
                        ContentValues valueOne = new ContentValues();
                        valueOne.put(DB.DIST_META_ID, currentDistId);
                        valueOne.put(DB.DIST_NAME, currentDistName);
                        valueOne.put(DB.DIST_IMG, imageInByte);
                        valueOne.put(DB.DIST_IMG_URL, currentDistId + "_" + timeStamp + ".jpg");
                        valueOne.put(DB.DIST_FLAG, 1);
                        valueOne.put(DB.DIST_TIME, currentDateandTime);
                        valueOne.put(DB.DIST_TIMESTAMP, timeStampSort);
                        if (SMSService.addressLatLng != null && !TextUtils.isEmpty(SMSService.addressLatLng)) {
                            String[] latlng = SMSService.addressLatLng.split(",");
                            if (latlng.length == 2) {
                                valueOne.put(DB.DIST_LAT, latlng[0]);
                                valueOne.put(DB.DIST_LNG, latlng[1]);
                            }
                        }

                        getBaseContext().getContentResolver().insert
                                (Uri.parse("content://com.symphony_ecrm.database.DBProvider/addDistributerMetaData"),
                                        valueOne);
                        Intent intent = new Intent(DistributerActivity.this, SyncManager.class);
                        intent.setAction(SyncManager.SYNC_DISTRIBUTER_DATA);
                        startService(intent);
                        getSupportFragmentManager().popBackStack();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (distInfoFrag != null)
                    distInfoFrag.setGeoLocaitonBtnEnable(true);
                // user cancelled Image capture
                Toast.makeText(this,
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                if (distInfoFrag != null)
                    distInfoFrag.setGeoLocaitonBtnEnable(true);
                Toast.makeText(this,
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }


    }


    @Override
    public void onOKPressed() {
        // TODO Auto-generated method stub
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(callGPSSettingIntent, GPS_RESULT);
    }

    @Override
    public void onCanclePressed() {
        // TODO Auto-generated method stub
        mCheckStatusListener.onGPSCancel(messageText);
    }

    @Override
    public void onDistributerListItemSelect(Bundle bundle) {
        // TODO Auto-generated method stub
        DistributerList distributerList = (DistributerList) getSupportFragmentManager().findFragmentByTag(DistributerList.class.getSimpleName());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        distInfoFrag = new DistributerInfo();
        distInfoFrag.setArguments(bundle);
        ft.add(R.id.distHome, distInfoFrag).hide(distributerList).addToBackStack(null).commit();

    }

    @Override
    public void onGPSDialogOpen(String msgText) {
        // TODO Auto-generated method stub
        messageText = msgText;
        showDialog(msgText);
    }

    private void showDialog(String msgText) {
        DialogFragment newFragment = DialogAlert.newInstance(R.string.gps_dialog_text);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onSettingsSelect() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(DistributerActivity.this, SymphonyReport.class);
        startActivity(intent);
    }

    @Override
    public void onCameraImage(String distId, String distKey, String distName) {
        // TODO Auto-generated method stub
        currentDistId = distId;
        currentDistName = distName;
        if (prefs != null) {
            edit = prefs.edit();
            edit.putString("tdistid", distId);
            edit.putString("tdistkey", distKey);
            edit.putString("tdistname", distName);
            edit.commit();
        }
        //Toast.makeText(this, currentDistId, Toast.LENGTH_LONG).show();
        Log.e("IDS", distId + " " + distKey);
        getCameraImage();

    }

    public void getCameraImage() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, GET_DISTRIBUTER_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "Camera is not supported on this device", Toast.LENGTH_LONG).show();
        }
    }


    public void startSyncAlram() {
        alramManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(this, SyncAlaram.class);
        alramReceiverIntent.setAction(SyncAlaram.DB_CHECK_FOR_DIST_PHOTO);
        alramPendingIntent = PendingIntent.getBroadcast(this, 0, alramReceiverIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Cancel alarms
        try {
            alramManager.cancel(alramPendingIntent);
        } catch (Exception e) {
            Log.e("Distributer Activity", "AlarmManager update was not canceled. " + e.toString());
        }
        alramManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 120, alramPendingIntent);
        edit = prefs.edit();
        edit.putBoolean("isAlramOn", true);
        edit.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CheckStatus.class.getSimpleName());
            if (fragment instanceof CheckStatus) {
                CheckStatus checkStatus = (CheckStatus) fragment;
                if (checkStatus != null && checkStatus.isVisible()) {
                    getSupportFragmentManager().beginTransaction().remove(checkStatus).commit();
                    getSupportFragmentManager().popBackStack();
                }
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            SMSService.location1 = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
