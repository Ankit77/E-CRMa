package com.symphony_ecrm;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.symphony_ecrm.http.WSVisit;
import com.symphony_ecrm.model.CRMModel;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;

import java.util.ArrayList;

/**
 * Created by ANKIT on 7/13/2016.
 */
public class VisitSyncReceiver extends IntentService {

    private E_CRM e_crm;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";
    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;

    public VisitSyncReceiver() {
        super(VisitSyncReceiver.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(VisitSyncReceiver.class.getSimpleName(), "VISI SYNC CALL");
        e_crm = (E_CRM) getApplicationContext();
        if (isNetworkAvailable(VisitSyncReceiver.this)) {
            ArrayList<CRMModel> visitList = e_crm.getSymphonyDB().getVisitForSynctoServer();
            if (visitList != null && visitList.size() > 0) {
                for (int i = 0; i < visitList.size(); i++) {
                    WSVisit wsVisit = new WSVisit();
                    CRMModel crmModel = visitList.get(i);
                    boolean issuccess = wsVisit.executeAddCustomer(HTTP_ENDPOINT, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), crmModel.getCusId(), crmModel.getLocation(), crmModel.getDiscussion(), crmModel.getPurposeId(), crmModel.getNextactionId(), crmModel.getCheckInImagePath(), crmModel.getCheckInLat(), crmModel.getCheckInLong(), crmModel.getCheckInTimeStemp(), crmModel.getCheckOutImagePath(), crmModel.getCheckOutLat(), crmModel.getCheckOutLong(), crmModel.getCheckOutTimeStemp(), crmModel.getActiondate(), crmModel.getConttactPerson(), SymphonyUtils.getAppVersion(VisitSyncReceiver.this), crmModel.getReferenceVisitId());
                    crmModel.setCheckFlag(1);
                    crmModel.setIsCompleteVisit(1);
                    crmModel.setIsSendtoServer(1);
                    if (issuccess) {
                        crmModel.setCheckStatus(1);
                    } else {
                        crmModel.setCheckStatus(0);
                    }

                    long id = e_crm.getSymphonyDB().updateCRM(crmModel);
                    Intent refreshIntent = new Intent();
                    refreshIntent.setAction("REFRESH");
                    sendBroadcast(refreshIntent);
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
