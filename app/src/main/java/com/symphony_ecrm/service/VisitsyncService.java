package com.symphony_ecrm.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.VisitSyncReceiver;
import com.symphony_ecrm.http.WSVisit;
import com.symphony_ecrm.model.CRMModel;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;
import com.symphony_ecrm.utils.Util;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by indianic on 13/01/17.
 */

public class VisitsyncService extends Service {
    private E_CRM e_crm;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";
    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private ArrayList<CRMModel> visitList;
    private int totalCount = 0;
    private int currentVisit = 0;
    private FirebaseStorage storage;
    private String checkinUrl = "";
    private String checkouturl = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        e_crm = (E_CRM) getApplicationContext();
        storage = FirebaseStorage.getInstance();
        visitList = e_crm.getSymphonyDB().getVisitForSynctoServer();
        if (visitList != null && visitList.size() > 0) {
            totalCount = visitList.size();
            currentVisit = 0;
            sendVisit(visitList.get(currentVisit));
        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);

    }

    private synchronized void sendVisit(final CRMModel crmModel) {
        Log.e(VisitsyncService.class.getSimpleName(), "VISI SYNC CALL");


        if (Util.isNetworkAvailable(VisitsyncService.this)) {

            File file_checkin = new File(crmModel.getCheckInImagePath());
            if (file_checkin.exists()) {
                StorageReference storageRef_checkin = storage.getReferenceFromUrl(Const.BUCKET);
                Uri file = Uri.fromFile(new File(crmModel.getCheckInImagePath()));
                StorageReference riversRef_checkin = storageRef_checkin.child(Const.FOLDER_NAME + "/" + file.getLastPathSegment());
                UploadTask uploadTask = riversRef_checkin.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        currentVisit = currentVisit + 1;
                        if (currentVisit < totalCount) {
                            sendVisit(visitList.get(currentVisit));
                        } else {
                            stopSelf();
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        checkinUrl = downloadUrl.toString();
                        StorageReference storageRef_checkout = storage.getReferenceFromUrl(Const.BUCKET);
                        Uri file = Uri.fromFile(new File(crmModel.getCheckOutImagePath()));
                        StorageReference riversRef_checkout = storageRef_checkout.child(Const.FOLDER_NAME + "/" + file.getLastPathSegment());
                        final UploadTask uploadTask = riversRef_checkout.putFile(file);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                currentVisit = currentVisit + 1;
                                if (currentVisit < totalCount) {
                                    sendVisit(visitList.get(currentVisit));
                                } else {
                                    stopSelf();
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                checkouturl = downloadUrl.toString();
                                crmModel.setCheckInImagePath(checkinUrl);
                                crmModel.setCheckOutImagePath(checkouturl);

                                AsyncSendVisit asyncSendVisit = new AsyncSendVisit(crmModel);
                                asyncSendVisit.execute(HTTP_ENDPOINT, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), crmModel.getCusId(), crmModel.getLocation(), crmModel.getDiscussion(), crmModel.getPurposeId(), crmModel.getNextactionId(), checkinUrl, crmModel.getCheckInLat(), crmModel.getCheckInLong(), crmModel.getCheckInTimeStemp(), checkouturl, crmModel.getCheckOutLat(), crmModel.getCheckOutLong(), crmModel.getCheckOutTimeStemp(), crmModel.getActiondate(), crmModel.getConttactPerson(), SymphonyUtils.getAppVersion(VisitsyncService.this), crmModel.getReferenceVisitId());

                            }
                        });
                    }
                });

//            AsyncSendVisit asyncSendVisit = new AsyncSendVisit(crmModel);
//            asyncSendVisit.execute(HTTP_ENDPOINT, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), crmModel.getCusId(), crmModel.getLocation(), crmModel.getDiscussion(), crmModel.getPurposeId(), crmModel.getNextactionId(), crmModel.getCheckInImagePath(), crmModel.getCheckInLat(), crmModel.getCheckInLong(), crmModel.getCheckInTimeStemp(), crmModel.getCheckOutImagePath(), crmModel.getCheckOutLat(), crmModel.getCheckOutLong(), crmModel.getCheckOutTimeStemp(), crmModel.getActiondate(), crmModel.getConttactPerson(), SymphonyUtils.getAppVersion(VisitsyncService.this), crmModel.getReferenceVisitId());
//                    boolean issuccess = wsVisit.executeAddCustomer(HTTP_ENDPOINT, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), crmModel.getCusId(), crmModel.getLocation(), crmModel.getDiscussion(), crmModel.getPurposeId(), crmModel.getNextactionId(), crmModel.getCheckInImagePath(), crmModel.getCheckInLat(), crmModel.getCheckInLong(), crmModel.getCheckInTimeStemp(), crmModel.getCheckOutImagePath(), crmModel.getCheckOutLat(), crmModel.getCheckOutLong(), crmModel.getCheckOutTimeStemp(), crmModel.getActiondate(), crmModel.getConttactPerson(), SymphonyUtils.getAppVersion(VisitSyncReceiver.this));
            } else {
                AsyncSendVisit asyncSendVisit = new AsyncSendVisit(crmModel);
                asyncSendVisit.execute(HTTP_ENDPOINT, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), crmModel.getCusId(), crmModel.getLocation(), crmModel.getDiscussion(), crmModel.getPurposeId(), crmModel.getNextactionId(), crmModel.getCheckInImagePath(), crmModel.getCheckInLat(), crmModel.getCheckInLong(), crmModel.getCheckInTimeStemp(), crmModel.getCheckOutImagePath(), crmModel.getCheckOutLat(), crmModel.getCheckOutLong(), crmModel.getCheckOutTimeStemp(), crmModel.getActiondate(), crmModel.getConttactPerson(), SymphonyUtils.getAppVersion(VisitsyncService.this), crmModel.getReferenceVisitId());
            }
        }
    }

    private class AsyncSendVisit extends AsyncTask<String, Void, Boolean> {
        private CRMModel crmModel;
        private boolean issuccess;

        public AsyncSendVisit(CRMModel mCrmModel) {
            crmModel = mCrmModel;
        }

        @Override
        protected Boolean doInBackground(final String... params) {
            WSVisit wsVisit = new WSVisit();
            issuccess = wsVisit.executeAddCustomer(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11], params[12], params[13], params[14], params[15], params[16], params[17], params[18], params[19]);
            return issuccess;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            crmModel.setCheckFlag(1);
            crmModel.setIsCompleteVisit(1);
            crmModel.setIsSendtoServer(1);
            if (aBoolean) {
                crmModel.setCheckStatus(1);
            } else {
                crmModel.setCheckStatus(0);
            }

            long id = e_crm.getSymphonyDB().updateCRM(crmModel);
            Intent refreshIntent = new Intent();
            refreshIntent.setAction("REFRESH");
            sendBroadcast(refreshIntent);
            currentVisit = currentVisit + 1;
            if (currentVisit < totalCount) {
                sendVisit(visitList.get(currentVisit));
            } else {
                stopSelf();
            }
        }
    }
}
