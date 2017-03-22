package com.symphony_ecrm.distributer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.http.HttpManager;
import com.symphony_ecrm.http.WSAddCustomer;
import com.symphony_ecrm.model.TownModel;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.Util;

import java.util.ArrayList;

/**
 * Created by Admin on 18-06-2016.
 */
public class AddCustomerFragment extends Fragment implements View.OnClickListener {

    private AutoCompleteTextView text;
    private EditText etCustmerName;
    private EditText etAddress;
    private EditText ettown;
    private EditText etConttactName;
    private EditText etDesignation;
    private EditText etMobile;
    private EditText etemail;
    private EditText etPincode;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";

    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private HttpManager httpManger;


    //    private EditText editTextName;
//    private EditText editTextAddress;
//    private EditText editTextCity;
    private Button buttonCheckIn;
    private ArrayList<TownModel> townlist;
    private E_CRM e_crm;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        e_crm = (E_CRM) getActivity().getApplicationContext();
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), getActivity().MODE_PRIVATE);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_add_customer, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initView(final View view) {
        initActionBar();
        text = (AutoCompleteTextView) view.findViewById(R.id.fragment_add_costomer_ettown);
        etCustmerName = (EditText) view.findViewById(R.id.fragment_add_customer_edt_name);
        etAddress = (EditText) view.findViewById(R.id.fragment_add_customer_edt_address);
        ettown = (EditText) view.findViewById(R.id.fragment_add_costomer_ettown);
        etConttactName = (EditText) view.findViewById(R.id.fragment_add_customer_edtcontactname);
        etDesignation = (EditText) view.findViewById(R.id.fragment_add_customer_edtdesignation);
        etMobile = (EditText) view.findViewById(R.id.fragment_add_customer_edtMobile);
        etemail = (EditText) view.findViewById(R.id.fragment_add_customer_edtemail);
        etPincode = (EditText) view.findViewById(R.id.fragment_add_customer_edtPincoe);
        buttonCheckIn = (Button) view.findViewById(R.id.fragment_add_customer_btn_submit);
        buttonCheckIn.setOnClickListener(this);
        ArrayList<String> townlist = e_crm.getSymphonyDB().getTownListOnly();
        if (townlist != null && townlist.size() > 0) {
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, townlist);
            text.setAdapter(adapter);
            text.setThreshold(1);
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_add_customer_btn_submit:
                submit();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.distributer_listview) item.setVisible(false);

        switch (item.getItemId()) {

            case android.R.id.home:
                // work around
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private boolean isValid() {
        boolean isValid = false;
        if (TextUtils.isEmpty(etCustmerName.getText().toString())) {
            showAlertDialog("Please enter customer name.");
            return false;
        } else if (TextUtils.isEmpty(etAddress.getText().toString())) {
            showAlertDialog("Please enter Address.");
            return false;
        } else if (TextUtils.isEmpty(ettown.getText().toString())) {
            showAlertDialog("Please enter Town.");
            return false;
        } else if (TextUtils.isEmpty(etConttactName.getText().toString())) {
            showAlertDialog("Please enter Contact.");
            return false;
        } else if (TextUtils.isEmpty(etDesignation.getText().toString())) {
            showAlertDialog("Please enter Designation.");
            return false;
        } else if (TextUtils.isEmpty(etMobile.getText().toString())) {
            showAlertDialog("Please enter Mobile.");
            return false;
        } else if (TextUtils.isEmpty(etemail.getText().toString())) {
            showAlertDialog("Please enter Email.");
            return false;
        } else if (!isValidEmail(etemail.getText().toString())) {
            showAlertDialog("Please enter valid Email.");
            return false;
        } else if (TextUtils.isEmpty(e_crm.getSymphonyDB().getTown(ettown.getText().toString()))) {
            showAlertDialog("Please enter Valid Town.");
            return false;
        } else {
            isValid = true;
        }
        return isValid;
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void submit() {
        if (isValid()) {
            if (Util.isNetworkAvailable(getActivity())) {
                String url = HTTP_ENDPOINT + "/CACS_Create_Customer.asp";
                AsyncAddCustomer asyncAddCustomer = new AsyncAddCustomer();
                asyncAddCustomer.execute(url, sharedPreferences.getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), etCustmerName.getText().toString(), etAddress.getText().toString(), e_crm.getSymphonyDB().getTown(ettown.getText().toString()), etConttactName.getText().toString(), etDesignation.getText().toString(), etMobile.getText().toString(), etemail.getText().toString(), etPincode.getText().toString());
            } else {
                Util.showAlertDialog(getActivity(), getString(R.string.alert_noconnectivy));
            }
        }
    }


//    private boolean insertData(final String name, final String number, final String companyName, final String city, String detail) {
//        final ContentValues checkStatusValue = new ContentValues();
//        checkStatusValue.put(DB.CUSTOMER_NAME, name);
//        checkStatusValue.put(DB.CUSTOMER_NUMBER, number);
//        checkStatusValue.put(DB.COMPANY_NAME, companyName);
//        checkStatusValue.put(DB.CITY, city);
//        checkStatusValue.put(DB.DETAIL, detail);
//        final Uri insert = getActivity().getContentResolver().insert(Uri.parse("content://com.symphony.database.DBProvider/" + DB.ADD_CUSTOMER_META_DATA),
//                checkStatusValue);
//        return Integer.valueOf(insert.getLastPathSegment()) > 0;
//
//    }


    private void initActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.add_customer));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void showAlertDialog(final String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.app_name))
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

    private class AsyncAddCustomer extends AsyncTask<String, Void, Boolean> {
        WSAddCustomer wsAddCustomer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDailog();
        }

        @Override
        protected Boolean doInBackground(String... param) {
            wsAddCustomer = new WSAddCustomer();
            String mobileno = param[1];
            String empid = param[2];
            String customername = param[3];
            String address = param[4];
            String townid = param[5];
            String contactname = param[6];
            String designation = param[7];
            String mobile = param[8];
            String email = param[9];
            String pincode = param[10];
            return wsAddCustomer.executeAddCustomer(param[0], mobileno, empid, customername, address, townid, contactname, designation, mobile, email, pincode);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (aBoolean) {
                Toast.makeText(getActivity(), "Customer add Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Failed to add customer", Toast.LENGTH_LONG).show();
            }
            getFragmentManager().popBackStack();
        }
    }

    private void showProgressDailog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Please Wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }
}
