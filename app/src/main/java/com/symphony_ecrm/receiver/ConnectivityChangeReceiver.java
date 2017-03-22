package com.symphony_ecrm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.service.VisitsyncService;


public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private E_CRM e_crm;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";
    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo networkInfo = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null) {

            if (firstConnect) {
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
                firstConnect = false;
            } else {
                firstConnect = true;
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
