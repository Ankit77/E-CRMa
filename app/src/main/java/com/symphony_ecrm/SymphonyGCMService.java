package com.symphony_ecrm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.symphony_ecrm.database.DB;
import com.symphony_ecrm.distributer.DistributerActivity;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SymphonyGCMService extends Service {

//    AIzaSyDSbfFtf-tm5VThCN_Os163RPPl4LAtY7k


    private String masterIP;
    private String masterPort;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    public static boolean isIpAddress(String ipAddress) {
        String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    public static boolean valisDomain(String domainName) {

        final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
        final Pattern pDomainNameOnly;

        pDomainNameOnly = Pattern.compile(DOMAIN_NAME_PATTERN);

        return pDomainNameOnly.matcher(domainName).find();

    }

    public static final String TAG = "SymphonyGCMService";

    @Override
    public void onCreate() {
        Log.e("GCM", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("GCM", "onStartCommand");
        prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (intent != null) {
            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);
            if (extras != null) {
                if (!extras.isEmpty()) {
                    if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                            .equals(messageType)) {
                        sendNotification("Send error: " + extras.toString());
                    } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                            .equals(messageType)) {
                        sendNotification("Deleted messages on server: "
                                + extras.toString());
                    } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                            .equals(messageType)) {
                        if (E_CRM.getsInstance().getSharedPreferences().getBoolean("isregister", false)) {
                            String message = extras.getString(SymphonyUtils.MESSAGE_KEY, "");
                            String notificationType = extras.getString(Const.KEY_NOTIFICATIONTYPE);

                            if (notificationType == null) {
                                sendNotification(message);
                            } else {
                                if (notificationType.equalsIgnoreCase(SymphonyUtils.NORMAL)) {
                                    sendNotification(message);
                                } else {
                                    String cacsVisitID = extras.getString(Const.KEY_CACSVISITID);
                                    String crmActId = extras.getString(Const.KEY_CRMACTID);
                                    String usermobilenumber=extras.getString(Const.KEY_USERMOBILENUMBER);
                                    String endCustomernumber=extras.getString(Const.KEY_END_CUSTOMER_MOBILENUMBER);
                                    if (!TextUtils.isEmpty(cacsVisitID) && !TextUtils.isEmpty(crmActId) && usermobilenumber.equalsIgnoreCase(prefs.getString("usermobilenumber", null))) {
                                        sendVisitNotification(
                                                message, cacsVisitID, crmActId,endCustomernumber);
                                    }
                                }
                            }
                            Log.i(TAG, "Received: " + extras.toString());
                        }
                    }
                }
            }
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
        return Service.START_STICKY;
    }


    private void sendNotification(String msg) {
        if (msg.contains("sip")) {
            try {
                String ipAddress[] = msg.split("#");
                if (ipAddress != null) {
                    if (ipAddress.length == 2) {
                        if (ipAddress[1].contains(":")) {
                            String[] ip = ipAddress[1].split(":");
                            if (ip != null) {
                                if (ip.length == 2) {
                                    masterIP = ip[0];
                                    masterPort = ip[1];
                                } else {
                                    masterIP = null;
                                    masterPort = null;
                                }
                            }
                        } else {
                            masterIP = ipAddress[1];
                            masterPort = null;
                        }
                        if (masterIP != null) {
                            if (isIpAddress(masterIP)) {
                                //set port here
                                setMasterIP();
                                msg = "Endpoint changed";
                            } else if (valisDomain(masterIP)) {
                                setMasterIP();
                                msg = "Endpoint changed";
                            } else {
                                msg = "Endpoint is not valid";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                masterIP = null;
                masterPort = null;
                Toast.makeText(this,
                        "Not able to change ip", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        Random r = new Random();
        int i1 = r.nextInt(1000 - 1) + 1;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = createNotification(SymphonyGCMService.this, msg, "", "","");
        mNotificationManager.notify(i1, notification);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss a");
        String currentDateandTime = sdf.format(new Date()).replace(" ", "");
        currentDateandTime = currentDateandTime.replace(".", "");


        // insert into database
        ContentValues value = new ContentValues();
        value.put(DB.NOTIFICATION_MESSAGE, msg);
        value.put(DB.NOTIFICATION_TIMESTAMP, currentDateandTime);
        value.put(DB.NOTIFICATION_TYPE, 0);

        this.getContentResolver().insert(Uri.parse("content://com.symphony_ecrm.database.DBProvider/addNewNotification"), value);
    }


    public void sendVisitNotification(String msg, String cacsVisitID, String crmActId,String endCustomerNumber) {

        Random r = new Random();
        int i1 = r.nextInt(1000 - 1) + 1;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = createNotification(SymphonyGCMService.this, msg, cacsVisitID, crmActId,endCustomerNumber);
        mNotificationManager.notify(i1, notification);
    }

    Notification createNotification(Context context, String message, String cacsVisitID, String crmActId,String endCustomerNumber) {

        boolean isVisitNofitification=false;
        Intent notificationIntent = new Intent(context, DistributerActivity.class);
        if (!TextUtils.isEmpty(cacsVisitID) && !TextUtils.isEmpty(crmActId)) {
            notificationIntent.putExtra(Const.KEY_CACSVISITID, cacsVisitID);
            notificationIntent.putExtra(Const.KEY_CRMACTID, crmActId);
            notificationIntent.putExtra(Const.KEY_NOTIFICATIONTYPE, Const.TYPE_VISIT);
            notificationIntent.putExtra(Const.KEY_END_CUSTOMER_MOBILENUMBER, endCustomerNumber);
            isVisitNofitification=true;
        }else
        {
            isVisitNofitification=false;
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("e-CRM Notification")
                .setContentIntent(intent)
                .setPriority(5) //private static final PRIORITY_HIGH = 5;
                .setContentText(message)
                .setAutoCancel(true).setOngoing(isVisitNofitification)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        return mBuilder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.e("GCM", "destroy");


    }

    private void setMasterIP() {

        edit = prefs.edit();
        edit.putString("masterIP", masterIP);
        edit.putString("masterPort", masterPort);


        edit.commit();


    }
}
