package com.symphony_ecrm.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.http.WSCustomer;
import com.symphony_ecrm.model.CustomerListModel;
import com.symphony_ecrm.utils.Const;

import java.util.ArrayList;

/**
 * Created by ANKIT on 6/8/2017.
 */

public class CustomerListService extends IntentService {
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";
    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;

    public CustomerListService() {
        super(CustomerListService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String url = HTTP_ENDPOINT + "/CACS_Get_Customerlist.asp?user=track_new&pass=track123&MNO=" + E_CRM.getsInstance().getSharedPreferences().getString("usermobilenumber", "") + "&EMPID=" + E_CRM.getsInstance().getSharedPreferences().getString(Const.EMPID, "");
        WSCustomer wsCustomer = new WSCustomer();
        ArrayList<CustomerListModel> customerListModels = wsCustomer.executeCustomerLst(url);
        if (customerListModels != null && customerListModels.size() > 0) {
            E_CRM.getsInstance().getSymphonyDB().deleteAllCustomer();
            E_CRM.getsInstance().getSymphonyDB().insertCustomer(customerListModels);
        }
    }
}
