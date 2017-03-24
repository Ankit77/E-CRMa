package com.symphony_ecrm.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.database.DB;
import com.symphony_ecrm.sms.SyncManager.DISTRIBUTER_META_DATA;
import com.symphony_ecrm.utils.SymphonyUtils;

public class SyncAlaram extends BroadcastReceiver {


    public static final String DB_CHECK_FOR_DIST_PHOTO = "com.symphony_ecrm.sms.DB_CHECK_FOR_DIST_PHOTO";
    private SharedPreferences prefs;
    public static String WIPE_REPORT_DATA = "com.symphony_ecrm.sms.WIPE_REPORT_DATA";
    private E_CRM e_crm;


    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        e_crm = (E_CRM) context.getApplicationContext();
        if (intent != null) {
            if (intent.getAction().equals(DB_CHECK_FOR_DIST_PHOTO)) {
                Cursor cur = context.getContentResolver().
                        query(Uri.parse("content://com.symphony_ecrm.database.DBProvider/getDistributerMetaData"),
                                DISTRIBUTER_META_DATA.PROJECTION,
                                DB.DIST_FLAG + " = 1",
                                null,
                                null);
                Cursor curCheck = context.getContentResolver().
                        query(Uri.parse("content://com.symphony_ecrm.database.DBProvider/getCheckData"),
                                SyncManager.CHECK_DATA.PROJECTION,
                                DB.CHECK_FLAG + " = 1",
                                null,
                                null);
                Intent intentLocationService = new Intent(context, SMSService.class);
                intentLocationService.setAction(SMSService.FETCH_LOCATION_INTENT);
                context.startService(intentLocationService);
                if (cur != null) {
                    if (cur.getCount() != 0) {
                        //start service
                        if (isNetworkAvailable(context)) {
                            Intent syncManager = new Intent(context, SyncManager.class);
                            syncManager.setAction(SyncManager.SYNC_DISTRIBUTER_DATA);
                            context.startService(syncManager);
                        }
                    }
                    cur.close();
                }
                if (curCheck != null) {
                    if (curCheck.getCount() != 0) {
                        if (isNetworkAvailable(context)) {
                            Intent syncManager = new Intent(context, SyncManager.class);
                            syncManager.setAction(SyncManager.SYNC_CHECK_STATUS_DATA);
                            context.startService(syncManager);
                        }
                    }
                    curCheck.close();
                }
            } else if (intent.getAction().equals(WIPE_REPORT_DATA)) {
                //sendNotification("Wipe out all data");
                int delDistributerReport = context.getContentResolver()
                        .delete(Uri.parse("content://com.symphony_ecrm.database.DBProvider/deleteDistributerReport"),
                                null,
                                null);
                int delCheckReport = context.getContentResolver()
                        .delete(Uri.parse("content://com.symphony_ecrm.database.DBProvider/deleteCheckReport"),
                                null,
                                null);
                Log.e("Symphony ", "Wipe out all data " + delDistributerReport + " " + delCheckReport);
//                SymphonyUtils.cancelAlarm(context);
//                SymphonyUtils.startWipeDataAlram(context);
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
