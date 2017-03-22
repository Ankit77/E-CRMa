package com.symphony_ecrm.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.model.CRMModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ANKIT on 3/23/2017.
 */

public class WipeDataService extends Service {
    private E_CRM e_crm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        e_crm = (E_CRM) getApplicationContext();
        deleteSyncData();
        return super.onStartCommand(intent, flags, startId);
    }

    private void deleteSyncData() {
        //delete checkin checkout image which are sync to server
        ArrayList<CRMModel> getAllSyncedVist = e_crm.getSymphonyDB().getAllSyncedVisit();
        if (getAllSyncedVist != null && getAllSyncedVist.size() > 0) {
            for (int i = 0; i < getAllSyncedVist.size(); i++) {
                CRMModel crmModel = getAllSyncedVist.get(i);
                File file_checkIn = new File(crmModel.getCheckInImagePath());
                if (file_checkIn.exists()) {
                    file_checkIn.delete();
                }
                File file_checkout = new File(crmModel.getCheckOutImagePath());
                if (file_checkout.exists()) {
                    file_checkout.delete();
                }
            }
        }
        e_crm.getSymphonyDB().deleteAllVisit();
        getContentResolver()
                .delete(Uri.parse("content://com.symphony_ecrm.database.DBProvider/deleteNotificationReport"),
                        null,
                        null);
        stopSelf();
    }
}
