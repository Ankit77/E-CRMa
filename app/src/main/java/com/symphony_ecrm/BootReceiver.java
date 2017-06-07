package com.symphony_ecrm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.symphony_ecrm.service.TimeTickService;
import com.symphony_ecrm.service.VisitsyncService;
import com.symphony_ecrm.sms.SMSService;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;
import com.symphony_ecrm.utils.Util;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {


    private Context context;

    private SharedPreferences prefs;


    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // TODO Auto-generated method stub
        if ((intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
                || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")
        )) {
            SymphonyUtils.startWipeDataAlram(context);
            startLocationService();
            E_CRM.getsInstance().getSharedPreferences().edit().putBoolean(Const.PREF_ISSYNCDATA, true).commit();
            if (E_CRM.getsInstance().getSharedPreferences().getBoolean("isregister", false)) {
                if (!Util.isMyServiceRunning(TimeTickService.class, context)) {
                    Intent service_intent = new Intent(context, TimeTickService.class);
                    context.startService(service_intent);
                }
            }
        }
    }


    private void startLocationService() {

        Intent intentLocationService = new Intent(context, SMSService.class);
        intentLocationService.setAction(SMSService.FETCH_LOCATION_INTENT);
        context.startService(intentLocationService);
    }

    /**
     * Set alarm for sync pending visit to server
     */
//    private void setSyncCheckStatusAlarm(Context context) {
//        Calendar calendar = Calendar.getInstance();
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent alramReceiverIntent = new Intent(context, VisitsyncService.class);
//        PendingIntent alramPendingIntent = PendingIntent.getBroadcast(context, 0, alramReceiverIntent, 0);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis(),
//                Const.SYNCDATA_INTERVAL, alramPendingIntent);
//    }

}
