package com.symphony_ecrm;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.symphony_ecrm.database.SymphonyDB;
import com.symphony_ecrm.distributer.CheckStatus;
import com.symphony_ecrm.model.CRMModel;
import com.symphony_ecrm.receiver.MyService;
import com.symphony_ecrm.service.VisitsyncService;
import com.symphony_ecrm.utils.Const;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by indianic on 19/12/15.
 */
public class E_CRM extends Application {

    private SharedPreferences sharedPreferences;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private long giventime;
    private SymphonyDB symphonyDB;
    @Override
    public void onCreate() {
        super.onCreate();
        symphonyDB = new SymphonyDB(getApplicationContext());
        symphonyDB.openDataBase();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (sharedPreferences.getLong(Const.PREF_STRAT_TIME, 0) == 0) {
            sharedPreferences.edit().putLong(Const.PREF_STRAT_TIME, Calendar.getInstance().getTimeInMillis()).commit();
        }
        Intent intentLocationService = new Intent(getApplicationContext(), MyService.class);
        startService(intentLocationService);
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        if (!sharedPreferences.getBoolean(Const.PREF_ISSYNCDATA, false)) {
            setSyncCheckStatusAlarm();
            sharedPreferences.edit().putBoolean(Const.PREF_ISSYNCDATA, true).commit();
        }

    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SymphonyDB getSymphonyDB() {
        return symphonyDB;
    }


    BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                long time = Calendar.getInstance().getTimeInMillis() - sharedPreferences.getLong(Const.PREF_STRAT_TIME, 0);
                if (time > Const.WIPETIME) {
                    //delete checkin checkout image which are sync to server
                    ArrayList<CRMModel> getAllSyncedVist = symphonyDB.getAllSyncedVisit();
                    if (getAllSyncedVist != null && getAllSyncedVist.size() > 0) {
                        for (int i = 0; i < getAllSyncedVist.size(); i++) {
                            CRMModel crmModel = getAllSyncedVist.get(i);
                            File file_checkIn = new File(crmModel.getCheckInImagePath());
                            if (file_checkIn.exists()) {
                                file_checkIn.delete();
                            }
                            File file_checkout = new File(crmModel.getCheckOutImagePath());
                            if (file_checkout.exists()) {
                                file_checkout.delete();
                            }
                        }
                    }
                    symphonyDB.deleteAllVisit();
                    getContentResolver()
                            .delete(Uri.parse("content://com.symphony_ecrm.database.DBProvider/deleteNotificationReport"),
                                    null,
                                    null);
                    sharedPreferences.edit().putLong(Const.PREF_STRAT_TIME, Calendar.getInstance().getTimeInMillis()).commit();

                }
                Log.e(CheckStatus.class.getSimpleName(), "Time  Tick Call");
            }
        }
    };

    /**
     * Set alarm for sync pending visit to server
     */
    private void setSyncCheckStatusAlarm() {
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(this, VisitsyncService.class);
        PendingIntent alramPendingIntent = PendingIntent.getService(this, 0, alramReceiverIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                Const.SYNCDATA_INTERVAL, alramPendingIntent);
    }
}
