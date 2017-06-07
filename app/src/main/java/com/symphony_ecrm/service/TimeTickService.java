package com.symphony_ecrm.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.SymphonyHome;
import com.symphony_ecrm.distributer.CheckStatus;
import com.symphony_ecrm.model.CRMModel;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.WriteLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;


public class TimeTickService extends Service {
    private String TAG = TimeTickService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WriteLog.E(TimeTickService.class.getSimpleName(), "Service Start");
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(tickReceiver);
    }

    BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                if (E_CRM.getsInstance().getSharedPreferences().getBoolean("isregister", false)) {
                    Log.e(CheckStatus.class.getSimpleName(), "Time  Tick Call");

                    long diff_wipedata = Calendar.getInstance().getTimeInMillis() - E_CRM.getsInstance().getSharedPreferences().getLong(Const.PREF_WIPEDATA, 0);
                    if (diff_wipedata >= Const.WIPETIME) {
                        Log.e(TAG, "WIPE IS CALL");
                        Intent wipedataService = new Intent(context, WipeDataService.class);
                        startService(wipedataService);
                        SharedPreferences.Editor editor = E_CRM.getsInstance().getSharedPreferences().edit();
                        editor.putLong(Const.PREF_WIPEDATA, Calendar.getInstance().getTimeInMillis());
                        editor.commit();
                    }

                    long diff_syncdata = Calendar.getInstance().getTimeInMillis() - E_CRM.getsInstance().getSharedPreferences().getLong(Const.PREF_SYNC, 0);
                    if (diff_syncdata >= Const.SYNCDATA_INTERVAL) {
                        Log.e(TAG, "SYNC IS CALL");
                        Intent syncdataService = new Intent(context, VisitsyncService.class);
                        startService(syncdataService);
                        SharedPreferences.Editor editor = E_CRM.getsInstance().getSharedPreferences().edit();
                        editor.putLong(Const.PREF_SYNC, Calendar.getInstance().getTimeInMillis());
                        editor.commit();
                    }

                }
            }
        }
    };


}
