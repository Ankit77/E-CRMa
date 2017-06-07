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

import com.crittercism.app.Crittercism;
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
    private SymphonyDB symphonyDB;
    private static E_CRM sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        symphonyDB = new SymphonyDB(getApplicationContext());
        symphonyDB.openDataBase();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        //Calling Service for fetch location
        Intent intentLocationService = new Intent(getApplicationContext(), MyService.class);
        startService(intentLocationService);
    }

    public static E_CRM getsInstance() {
        return sInstance;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SymphonyDB getSymphonyDB() {
        return symphonyDB;
    }

}
