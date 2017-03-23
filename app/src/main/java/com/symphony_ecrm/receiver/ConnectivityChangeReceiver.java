package com.symphony_ecrm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.service.TimeTickService;
import com.symphony_ecrm.service.VisitsyncService;
import com.symphony_ecrm.utils.Util;


public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private static final String HTTP_PROTOCOL = "http://";
    private E_CRM e_crm;

    @Override
    public void onReceive(Context context, Intent intent) {
        e_crm = (E_CRM) context.getApplicationContext();
        NetworkInfo networkInfo = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null) {
            //Start service for checking wipe data && Sync Pending Data
            if (e_crm.getSharedPreferences().getBoolean("isregister", false)) {
                if (!Util.isMyServiceRunning(TimeTickService.class, context)) {
                    Intent service_intent = new Intent(context, TimeTickService.class);
                    context.startService(service_intent);
                }
            }

                // do subroutines here
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

                    //get the different network states
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        Log.e(ConnectivityChangeReceiver.class.getSimpleName(), "Connect");
                        if (isNetworkAvailable(context)) {
                            Intent syncService = new Intent(context, VisitsyncService.class);
                            context.startService(syncService);
                        }
                    } else {
                        Log.e(ConnectivityChangeReceiver.class.getSimpleName(), "disconnect");
                    }
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
