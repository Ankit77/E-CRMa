package com.symphony_ecrm;

import android.content.Context;
import android.content.SharedPreferences;

public class SymphonyGCMHome {
    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        return prefs.getString("registrationId", null);
    }

    public static void setRegistrationId(Context context, String registrationId) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = prefs.edit();
        editor.putString("registrationId", registrationId);
        editor.commit();


    }
}
