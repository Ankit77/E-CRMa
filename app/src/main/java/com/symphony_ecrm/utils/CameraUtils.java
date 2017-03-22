package com.symphony_ecrm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.symphony_ecrm.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Admin on 24-05-2016.
 */
public class CameraUtils {

    /**
     *
     */
    public static String saveSelectedMediaToSDCard(final Context context, String path, boolean deleteImageFlag) {
        String fileName;
        String sdPath;
        sdPath = (Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name) + "/Image");
        fileName = path.substring(path.lastIndexOf("/"), path.length());
        copyDataToSD(path, sdPath, fileName, deleteImageFlag);
        return sdPath + fileName;
    }

    public static void copyDataToSD(final String path, final String sdPath,
                                    final String fileName, boolean deleteImageFromPath) {

        if (!new File(sdPath).exists()) {
            new File(sdPath).mkdirs();
        }
        final File copy_File = new File(new File(sdPath) + fileName);
        Log.e("Copy_FilePath:", copy_File.toString());
        try {
            if (copy_File.exists()) {
                copy_File.delete();
            }
            copy_File.createNewFile();
            try {
                final Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(path));
                final FileOutputStream fos = new FileOutputStream(copy_File);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                fos.flush();
                fos.close();


            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            if (deleteImageFromPath) {
                if (new File(path).exists()) {
                    new File(path).delete();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int checkExIfInfo(String mediaFile) {
        final ExifInterface exif;
        int rotation = 0;
        try {
            exif = new ExifInterface(mediaFile);
            final String exifOrientation = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            if (exifOrientation.equals("6")) {
                rotation = 90;// Rotation angle
            } else if (exifOrientation.equals("1")) {
                rotation = 0;// Rotation angle
            } else if (exifOrientation.equals("8")) {
                rotation = 270;// Rotation angle
            } else if (exifOrientation.equals("3")) {
                rotation = 180;// Rotation angle
            } else if (exifOrientation.equals("0")) {
                rotation = 0;// Rotation angle
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotation;
    }


    /**
     * This method is use for rotation of image.
     *
     * @param mediaFile
     */
    public static void rotateImage(String mediaFile, int rotation) {
        if (rotation != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            final Bitmap bitmap = BitmapFactory.decodeFile(mediaFile, options);
            if (bitmap != null) {
                final Bitmap targetBitmap = Bitmap
                        .createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                                Bitmap.Config.RGB_565);
                final Canvas canvas = new Canvas(targetBitmap);
                Matrix matrix = new Matrix();
                matrix.setRotate(rotation, bitmap.getWidth() / 2,
                        bitmap.getHeight() / 2);
                canvas.drawBitmap(bitmap, matrix, new Paint());
                bitmap.recycle();
                try {
                    final FileOutputStream fos = new FileOutputStream(mediaFile);
                    targetBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void getExifValues(final String path, final double latitude, final double longitude) {
//        String exif = "Exif: " + path;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "" + latitude);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "" + longitude);
            Log.d("TAG", latitude + " : " + longitude);

            if (latitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            }

            if (longitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            }

            exif.saveAttributes();


            final ExifInterface exif2 = new ExifInterface(path);

            Log.e("TAG", "LATITUDE : " + exif2.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            Log.e("TAG", "LONGITUDE : " + exif2.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));


//            Log.e("TAG", exif);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    static public boolean setGeoTag(String image, double mLatitude, double mLongitude) {
        if (mLatitude != 0.0 && mLongitude != 0.0) {
            try {
                ExifInterface exif = new ExifInterface(
                        image);

                double latitude = Math.abs(mLatitude);
                double longitude = Math.abs(mLongitude);

                int num1Lat = (int) Math.floor(latitude);
                int num2Lat = (int) Math.floor((latitude - num1Lat) * 60);
                double num3Lat = (latitude - ((double) num1Lat + ((double) num2Lat / 60))) * 3600000;

                int num1Lon = (int) Math.floor(longitude);
                int num2Lon = (int) Math.floor((longitude - num1Lon) * 60);
                double num3Lon = (longitude - ((double) num1Lon + ((double) num2Lon / 60))) * 3600000;

                String lat = num1Lat + "/1," + num2Lat + "/1," + num3Lat + "/1000";
                String lon = num1Lon + "/1," + num2Lon + "/1," + num3Lon + "/1000";

                if (mLatitude > 0) {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
                } else {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
                }

                if (mLongitude > 0) {
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
                } else {
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
                }

                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat);
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon);

                exif.saveAttributes();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


}
