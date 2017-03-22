package com.symphony_ecrm.utils;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import com.symphony_ecrm.R;
import com.symphony_ecrm.mocklocation.MockLocationDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 26-Jun-16.
 */
public class Util {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static long getFileSizeInKB(String path) {
        // Get file from file name
        File file = new File(path);

// Get length of file in bytes
        long fileSizeInBytes = file.length();
// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        return fileSizeInKB;
    }

    public static void showAlertDialog(Context context, final String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }

    public static long getDateStringtoMilies(String datestring) {
        SimpleDateFormat sdf = new SimpleDateFormat(Const.DATETIMEFORMAT);
        try {
            Date mDate = sdf.parse(datestring);
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long getDateStringtoMilies(String datestring, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date mDate = sdf.parse(datestring);
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static byte[] bitmapToByteArray(Bitmap b) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Date changeFromStringtoDate(String datevalue) {
        SimpleDateFormat formatter = new SimpleDateFormat(Const.DATETIMEFORMAT);
        String dateInString = datevalue;

        try {

            Date date = formatter.parse(dateInString);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String changeFromDatetoString(Date dateval) {
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        String datetime = dateformat.format(dateval);
        datetime = datetime.replace(".", "");
        return datetime;
    }


    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
        String formattedDate = dateformat.format(c.getTime());
        return formattedDate;
    }

    public static boolean isFackLocation(Context context, Location location) {
        if (location != null) {
            boolean isMock = MockLocationDetector.isLocationFromMockProvider(context, location);
            return isMock;
        } else {
            return false;
        }
    }

    public static String getImagePath(Context context) {
        String path = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name) + "/Image";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
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

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
