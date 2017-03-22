package com.symphony_ecrm.distributer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.database.SymphonyDB;
import com.symphony_ecrm.model.CRMModel;
import com.symphony_ecrm.model.CustomerListModel;
import com.symphony_ecrm.sms.SMSService;
import com.symphony_ecrm.utils.CameraUtils;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.GetFilePath;
import com.symphony_ecrm.utils.ImageLoadingUtils;
import com.symphony_ecrm.utils.SymphonyUtils;
import com.symphony_ecrm.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class CheckStatus extends Fragment implements CheckStatusListener, LocationListener {

    private Button checkStatus;
    private TextView txtMessage;
    private TextView txtCheckINOUTLabel;
    private DistributerActivityListener mDistributerListener;
    private LocationManager mLocationManager;
    private static final long TIME_DIFFERENCE = 1000 * 60 * 1;
    private E_CRM e_sampark;
    private SymphonyDB symphonyDB;
    private String customerId;
    private String cameraFilePath;

    //Changes By Mavya
    private static final int TAKE_PHOTO = 1001;
    private String selectedPath;
    private File compressedImage;
    private String refVisitId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getString("CUSID", "");
            refVisitId = getArguments().getString("VISITREFERENCEID", "");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDistributerListener = (DistributerActivityListener) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN/OUT");
        e_sampark = (E_CRM) getActivity().getApplicationContext();
        symphonyDB = new SymphonyDB(getActivity());
        setHasOptionsMenu(true);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        View v = inflater.inflate(R.layout.checkstatus_fragment, null);
        checkStatus = (Button) v.findViewById(R.id.checkStatus);
        TextView tvCustomerName = (TextView) v.findViewById(R.id.checkStatusCustomerName);
        tvCustomerName.setText("Customer : " + e_sampark.getSymphonyDB().getCustomerName(customerId));
        txtMessage = (TextView) v.findViewById(R.id.txtStatusLabel);
        txtCheckINOUTLabel = (TextView) v.findViewById(R.id.checkStatusText);
        checkStatus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mDistributerListener.onGPSDialogOpen("Can not CHECK IN/OUT , because GPS is disabled");
                } else {
                    if (SymphonyUtils.isAutomaticDateTime(getActivity())) {
                        if (SMSService.addressLatLng == null || TextUtils.isEmpty(SMSService.addressLatLng)) {
                            Toast.makeText(getActivity(), "Not able to get the geocode , please try after a while", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            //Code for CACS user
                            if (!Util.isFackLocation(getActivity(), SMSService.location1)) {
                                captureImage();
                            } else {
                                Toast.makeText(getActivity(), "Please Disable Fake Location", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Toast.makeText(getActivity(), "Please Make date & Time setting to automatic mode", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_checkin, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.distributer_listview) item.setVisible(false);
        switch (item.getItemId()) {

            case android.R.id.home:
                // work around
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().popBackStack();
                return true;

            case R.id.list:
                ((DistributerActivity) getActivity()).onSettingsSelect();
                return true;
            case R.id.symphony_exit:
                System.exit(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        //if user type is Dom then register tick receiver for checkin/checkout enable
        if (e_sampark.getSharedPreferences().getString(Const.USERTYPE, "").equalsIgnoreCase(Const.USER_DOM)) {
            getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        }
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN/OUT");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        IntentFilter intentFilter = new IntentFilter(SMSService.GEO_LOCATION_FAILED);
        intentFilter.addAction(SMSService.GEO_LOCATION_FAILED);
        intentFilter.addAction(DistributerActivity.LOCATION_RECEIVER);


        if (e_sampark.getSharedPreferences().getString(Const.USERTYPE, "").equalsIgnoreCase(Const.USER_DOM)) {
            long diff = Calendar.getInstance().getTimeInMillis() - e_sampark.getSharedPreferences().getLong("TIME", 0);
            if (diff > 0 && diff > TIME_DIFFERENCE) {
                checkStatus.setEnabled(true);
                checkStatus.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                txtCheckINOUTLabel.setVisibility(View.VISIBLE);
            } else {
                checkStatus.setEnabled(false);
                checkStatus.setVisibility(View.GONE);
                txtMessage.setVisibility(View.VISIBLE);
                txtCheckINOUTLabel.setVisibility(View.GONE);
            }

        }
        if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
            setCheckIn();
        } else {
            setCheckOut();
        }

    }

    public void changeButtonState() {
        if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
            setCheckIn();
        } else {
            setCheckOut();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN/OUT");
        mLocationManager.removeUpdates(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (e_sampark.getSharedPreferences().getString(Const.USERTYPE, "").equalsIgnoreCase(Const.USER_DOM)) {
            getActivity().unregisterReceiver(tickReceiver);
        }
    }

    @Override
    public void onGPSCancel(String messageText) {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(), messageText, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onGPSOK() {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(), " GPS is enabled", Toast.LENGTH_SHORT).show();
    }


    public void setCheckOut() {
        checkStatus.setBackgroundResource(R.drawable.button_red);
        checkStatus.setText("CHECK OUT");
        checkStatus.setTag(Const.CHECKOUT);
//        setDistStatus(false);
        //setFirstTime();
    }

    public void setCheckIn() {

        checkStatus.setBackgroundResource(R.drawable.button_green);
        checkStatus.setText("CHECK IN");
        checkStatus.setTag(Const.CHECKIN);
        // setDistStatus(true);
        //setFirstTime();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            SMSService.addressLatLng = location.getLatitude() + "," + location.getLongitude();
            Toast.makeText(getActivity(), "CHECK STATUS - " + SMSService.addressLatLng, Toast.LENGTH_LONG).show();
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                Log.e(CheckStatus.class.getSimpleName(), "Time  Tick Call");
                long diff = Calendar.getInstance().getTimeInMillis() - e_sampark.getSharedPreferences().getLong("TIME", 0);
                if (diff > 0 && diff > TIME_DIFFERENCE) {
                    checkStatus.setEnabled(true);
                    checkStatus.setVisibility(View.VISIBLE);
                    txtMessage.setVisibility(View.GONE);
                    txtCheckINOUTLabel.setVisibility(View.VISIBLE);
                } else {
                    checkStatus.setEnabled(false);
                    checkStatus.setVisibility(View.GONE);
                    txtMessage.setVisibility(View.VISIBLE);
                    txtCheckINOUTLabel.setVisibility(View.GONE);
                }
            }


            if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
                setCheckIn();
            } else {
                setCheckOut();
            }
        }


    };


    private void captureImage() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {

            Uri outputFileUri = getPostImageUri(true, "" + System.currentTimeMillis());
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent1.putExtra("return-data", true);
            startActivityForResult(intent1, TAKE_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri getPostImageUri(boolean canCleanup, String ticket) {
        final File file = new File(getActivity().getExternalCacheDir() + File.separator + ticket + ".jpg");
        if (canCleanup) {
            if (file.exists()) {
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        cameraFilePath = file.getAbsolutePath();
        return Uri.fromFile(file);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == TAKE_PHOTO) {
                    final Uri uri = Uri.fromFile(new File(cameraFilePath));
                    selectedPath = GetFilePath.getPath(getActivity(), uri);
                    if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).toString().equalsIgnoreCase(Const.CHECKIN)) {
                        //selectedPath = compressImage(selectedPath);

                        //compress image
                        if (Util.getFileSizeInKB(selectedPath) > 500) {

                            compressedImage = new Compressor.Builder(getActivity())
                                    .setMaxWidth(640)
                                    .setMaxHeight(480)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(Util.getImagePath(getActivity()))
                                    .build()
                                    .compressToFile(new File(selectedPath));
                        } else {
                            compressedImage = new File(selectedPath);
                        }
                        showAlertDialog(compressedImage.getPath());
                        Log.d("TAG", "selectedPath" + compressedImage.getPath());
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                CameraUtils.setGeoTag(selectedPath, SMSService.location.getLatitude(), SMSService.location.getLongitude());
//                            }
//                        }, 150);

                    } else {
                        // for checkout
//                        selectedPath = CameraUtils.saveSelectedMediaToSDCard(getActivity(), Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/" + takePhotoFile, true);
                        //selectedPath = compressImage(selectedPath);
                        //compress image
                        if (Util.getFileSizeInKB(selectedPath) > 500) {
                            compressedImage = new Compressor.Builder(getActivity())
                                    .setMaxWidth(640)
                                    .setMaxHeight(480)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(Util.getImagePath(getActivity()))
                                    .build()
                                    .compressToFile(new File(selectedPath));
                        } else {
                            compressedImage = new File(selectedPath);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                CameraUtils.setGeoTag(selectedPath, SMSService.location.getLatitude(), SMSService.location.getLongitude());
                                final DialogCheckIn dialogCheckIn = new DialogCheckIn();
                                Bundle bundle = new Bundle();
                                bundle.putString("TYPE", Const.CHECKOUT);
                                bundle.putString("CHECKOUTIMAGE", compressedImage.getPath());
                                bundle.putString("CHECKOUTLAT", "" + SMSService.location.getLatitude());
                                bundle.putString("CHECKOUTLONG", "" + SMSService.location.getLongitude());
                                SimpleDateFormat timeStampFormat = new SimpleDateFormat(Const.DATETIMEFORMAT);
                                String timeStamp = timeStampFormat.format(new Date());
                                bundle.putString("CHECKOUTTIMESTAMP", "" + timeStamp);
                                dialogCheckIn.setArguments(bundle);
                                dialogCheckIn.setTargetFragment(CheckStatus.this, 0);
                                dialogCheckIn.setCancelable(false);
                                dialogCheckIn.show(getFragmentManager(), dialogCheckIn.getClass().getSimpleName());
                            }
                        }, 150);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public boolean isCheckStatus() {
        return e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).toString().equalsIgnoreCase(Const.CHECKIN);
    }


    private void showAlertDialog(final String checkInImage) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.app_name))
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(getString(R.string.alert_fill_detail))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        final DialogCheckIn dialogCheckIn = new DialogCheckIn();
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", Const.CHECKIN);
                        bundle.putString("CHECKINIMAGE", checkInImage);
                        bundle.putString("CUSID", customerId);
                        bundle.putString("VISITREFERENCEID", refVisitId);
                        bundle.putString("CHECKINLAT", "" + SMSService.location.getLatitude());
                        bundle.putString("CHECKINLONG", "" + SMSService.location.getLongitude());
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat(Const.DATETIMEFORMAT);
                        String timeStamp = timeStampFormat.format(new Date());
                        bundle.putString("CHECKINTIMESTAMP", "" + timeStamp);
                        dialogCheckIn.setArguments(bundle);
                        dialogCheckIn.setTargetFragment(CheckStatus.this, 0);
                        dialogCheckIn.show(getFragmentManager(), dialogCheckIn.getClass().getSimpleName());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothingdia

                        CRMModel crmModel = new CRMModel();
                        e_sampark.getSharedPreferences().edit().putString("TAG", Const.CHECKOUT).commit();
                        changeButtonState();
                        crmModel.setCusId(customerId);
                        crmModel.setReferenceVisitId(refVisitId);
                        crmModel.setCheckInLat("" + SMSService.location.getLatitude());
                        crmModel.setCheckInLong("" + SMSService.location.getLongitude());
                        crmModel.setCheckInImagePath("" + selectedPath);
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat(Const.DATETIMEFORMAT);
                        String timeStamp = timeStampFormat.format(new Date());
                        crmModel.setCheckInTimeStemp(timeStamp);
                        crmModel.setCheckStatus(0);
                        crmModel.setCheckFlag(0);
                        crmModel.setIsSendtoServer(0);
                        crmModel.setIsCompleteVisit(0);
                        CustomerListModel customerListModel = e_sampark.getSymphonyDB().getCustomerInfo(customerId);
                        if (customerListModel != null) {
                            crmModel.setCompanyname(customerListModel.getCustomername());
                            crmModel.setLocation(customerListModel.getTown());
                            crmModel.setConttactPerson(customerListModel.getContact());
                        }
                        long id = symphonyDB.insertCRM(crmModel);
                        e_sampark.getSharedPreferences().edit().putLong("LASTROWID", id).commit();
                        e_sampark.getSharedPreferences().edit().putBoolean(Const.PREF_COMPLETE_VISIT, false).commit();
                        dialog.dismiss();
                        setCheckOut();
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }
}
