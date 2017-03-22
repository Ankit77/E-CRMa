package com.symphony_ecrm.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.symphony_ecrm.R;
import com.symphony_ecrm.sms.SyncAlaram;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SymphonyUtils {

    public static final String TIME_SERVER = "time-a.nist.gov";
    public static final String GCM_KEY = "915617718405";
    public static final String MESSAGE_KEY = "message";
    public static final String NOTIFICACTIONTYPE_KEY = "type";
    public static final String CACSVISITID_KEY = "cacsvisitid";
    public static final String CRMACTID_KEY = "crmactid";
    public static final String NORMAL = "normal";
    public static final String VISIT = "visit";

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor edit;
    public static boolean isTimePassed = false;
    public static AlarmManager dayAlarmMgr = null;
    public static PendingIntent dayAlarmIntent = null;
    public static Intent alramIntent = null;
    public static boolean isFutureTimePassed = false;


    public static String getAppVersion(Context context) {
        PackageInfo pInfo;
        try {

            if (context != null) {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return pInfo.versionName;
            } else
                return "0.0";

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }

    public static void startWipeDataAlram(Context context) {

        prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        edit = prefs.edit();
        checkTime(context);
        if (prefs.getString("reportTime", null) != null) {
            Log.e("startWipeDataAlram ", "already set " + prefs.getString("reportTime", null));
            return;
        } else {
            Log.e("startWipeDataAlram ", " need to set  " + prefs.getString("reportTime", null));
        }
        if (checkWipeDataAlram(context)) return;


        if (dayAlarmMgr == null) {
            dayAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Log.e(" startWipeDataAlram ", "creating new alram");


        }

        if (alramIntent == null) {
            alramIntent = new Intent(context, SyncAlaram.class);
            Log.e(" startWipeDataAlram ", "creating new intent");

        }
        alramIntent.setAction(SyncAlaram.WIPE_REPORT_DATA);

        if (dayAlarmIntent == null)


            dayAlarmIntent = PendingIntent.getBroadcast(context, 1001,
                    alramIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Calendar reportTime = Calendar.getInstance();
        reportTime.setTimeInMillis(System.currentTimeMillis());
        reportTime.set(Calendar.HOUR_OF_DAY, 0);
        reportTime.set(Calendar.MINUTE, 00);
        reportTime.set(Calendar.SECOND, 00);
        reportTime.add(Calendar.HOUR_OF_DAY, 48);


        edit = prefs.edit();
        edit.putString("reportTime", reportTime.getTime().toString()).commit();


        dayAlarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                reportTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 2,
                dayAlarmIntent);


        Log.e("Alram for wipe data", " @ " + reportTime.getTime().toString());


    }


    private static boolean checkWipeDataAlram(Context context) {


        String prefReportTime = prefs.getString("reportTime", null);

        if (prefReportTime == null) return false;

        if (alramIntent == null) {
            alramIntent = new Intent(context, SyncAlaram.class);
            Log.e("checkWipeDataAlram", "Creating new intent");
        }

        alramIntent.setAction(SyncAlaram.WIPE_REPORT_DATA);

        boolean isWorking = (PendingIntent.getBroadcast(context, 1001,
                alramIntent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        return isWorking;
    }

    public static void checkTime(Context context) {

        prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        edit = prefs.edit();
        String prefReportTime = prefs.getString("reportTime", null);
        //prefReportTime = "Thu Feb 12 00:00:00 IST 2015";


        Calendar reportTime = Calendar.getInstance();
        reportTime.setTimeInMillis(System.currentTimeMillis());
        reportTime.add(Calendar.MINUTE, 02);
        reportTime.set(Calendar.SECOND, 00);
        reportTime.add(Calendar.HOUR_OF_DAY, 0);


        if (prefReportTime != null) {

            Log.e("prefReportTime", prefReportTime);

            Calendar prefCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");


            try {
                prefCal.setTime(sdf.parse(prefReportTime));


                Log.e("STATUS ", " difference " + prefCal.compareTo(reportTime));


                if (prefCal.compareTo(reportTime) == -1) {

                    Log.e("STATUS ", " not passed (1) " + prefCal.getTime().toString() + " " + reportTime.getTime().toString());
                    isTimePassed = false;

                    if (!checkWipeDataAlram(context)) {


                        prefCal.set(Calendar.HOUR_OF_DAY, 0);
                        prefCal.set(Calendar.MINUTE, 00);
                        prefCal.set(Calendar.SECOND, 00);


                        if (dayAlarmMgr == null) {
                            dayAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        }

                        if (alramIntent == null) {
                            alramIntent = new Intent(context, SyncAlaram.class);
                        }

                        if (dayAlarmIntent == null)
                            dayAlarmIntent = PendingIntent.getBroadcast(context, 1001,
                                    alramIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                        dayAlarmMgr.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                prefCal.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY * 2,
                                dayAlarmIntent);


                        edit = prefs.edit();
                        edit.putString("reportTime", prefCal.getTime().toString()).commit();


                    }

                } else {

                    Log.e("STATUS ", " passed " + prefCal.getTime().toString() + " " + reportTime.getTime().toString());

                    Intent wipeReportIntent = new Intent(context, SyncAlaram.class);
                    wipeReportIntent.setAction(SyncAlaram.WIPE_REPORT_DATA);
                    context.sendBroadcast(wipeReportIntent);

                    isTimePassed = true;

                    edit.putString("reportTime", null);
                    edit.commit();


                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block


                e.printStackTrace();
            }
        } else {


            Log.e("Check Time ", "No prefered timestored");


        }


    }


    public static void cancelAlarm(Context context) {

        try {
            if (SymphonyUtils.dayAlarmMgr == null) {
                SymphonyUtils.dayAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            }


            if (SymphonyUtils.alramIntent == null) {
                SymphonyUtils.alramIntent = new Intent(context, SyncAlaram.class);
                Log.e(" restart alaram ", "creating new intent");

            }
            SymphonyUtils.alramIntent.setAction(SyncAlaram.WIPE_REPORT_DATA);

            if (SymphonyUtils.dayAlarmIntent == null)

                SymphonyUtils.dayAlarmIntent = PendingIntent.getBroadcast(context, 1001,
                        SymphonyUtils.alramIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            SymphonyUtils.dayAlarmMgr.cancel(SymphonyUtils.dayAlarmIntent);
            SymphonyUtils.dayAlarmIntent.cancel();
            SymphonyUtils.dayAlarmMgr = null;
            SymphonyUtils.dayAlarmIntent = null;
            edit = prefs.edit();

            isFutureTimePassed = true;
            edit.putString("reportTime", null);
            edit.commit();


        } catch (Exception e) {

            Log.e("Boot Receiver", "AlarmManager update was not canceled. " + e.toString());


        }


    }


    public static void setMasterIp(Context context, String masterIP, String masterPort) {

        if (context != null) {
            prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);

            edit = prefs.edit();
            edit.putString("masterIP", masterIP);
            edit.putString("masterPort", masterPort);


            edit.commit();
        }


    }

    public static long getCurrentNetworkTime() {
        long returnTime = 0;
        try {
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            //long returnTime = timeInfo.getReturnTime();   //local device time
            returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time
            Date time = new Date(returnTime);
            Log.d(SymphonyUtils.class.getSimpleName(), "Time from " + TIME_SERVER + ": " + time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnTime;
    }

    public static boolean isAutomaticDateTime(Context context) {
        int i = 0;
        if (android.os.Build.VERSION.SDK_INT < 17) {
            i = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0);
        } else {
            i = android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);
        }
        if (i == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static ProgressDialog displayProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading....");
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    /**
     * Dismiss current progress dialog
     *
     * @param dialog dialog
     */
    public static void dismissProgressDialog(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

}
