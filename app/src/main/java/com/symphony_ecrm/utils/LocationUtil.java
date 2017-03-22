package com.symphony_ecrm.utils;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by indianic on 25/05/16.
 */
public class LocationUtil {

    private String mLatitude;
    private String mLongitude;
    private String mRefLatitudes;
    private String mRefLongitudes;

    private Float Latitude;
    private Float Longitude;


    /**
     * Method to Check whether the received Media File
     * has Latitude & Longitude associated with it, if Yes
     * then set them with it, set current latitude and longitude
     * in an otherwise case
     *
     * @param file      Media File (Image/Video)
     * @param latitude  Latitude
     * @param longitude Longitude
     */
    public void checkAndSetLocation(final String file, final String latitude, final String longitude) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
            mLatitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            mLongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            mRefLatitudes = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            mRefLongitudes = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            if ((mLatitude != null) && (mRefLatitudes != null) && (mLongitude != null) && (mRefLongitudes != null)) {
                if (mRefLatitudes.equals("N")) {
                    Latitude = convertToDegree(mLatitude);
                } else {
                    Latitude = 0 - convertToDegree(mLatitude);
                }
                if (mRefLongitudes.equals("E")) {
                    Longitude = convertToDegree(mLongitude);
                } else {
                    Longitude = 0 - convertToDegree(mLongitude);
                }

                if (Latitude != null && Longitude != null) {
                    mLatitude = Latitude.toString();
                    mLongitude = Longitude.toString();
                } else {
                    mLatitude = latitude;
                    mLongitude = longitude;
                }
            } else {
                mLatitude = latitude;
                mLongitude = longitude;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to convert the Latitudes & Longitudes
     * associated to the selected Media File from the
     * Phone's Gallery into Float Value from the one unsupported version retrieved by default
     *
     * @param stringDMS
     * @return
     */
    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    }


    public String getLatitude() {
        return mLatitude;
    }


    public String getLongitude() {
        return mLongitude;
    }


    public String getRefLatitudes() {
        return mRefLatitudes;
    }


    public String getRefLongitudes() {
        return mRefLongitudes;
    }

}
