package com.symphony_ecrm.register;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.distributer.CheckStatus;
import com.symphony_ecrm.distributer.CustomerListFragment;
import com.symphony_ecrm.distributer.DistributerActivity;
import com.symphony_ecrm.http.WSNextAction;
import com.symphony_ecrm.http.WSPostpondVisit;
import com.symphony_ecrm.http.WSPurpose;
import com.symphony_ecrm.http.WSTown;
import com.symphony_ecrm.model.NextActionModel;
import com.symphony_ecrm.model.PurposeModel;
import com.symphony_ecrm.model.TownModel;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Ankit on 6/22/2016.
 */
public class HomeFragment extends Fragment {

    private View view;
    private E_CRM e_sampark;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";

    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progress;
    private AsyncLoadNextAction asyncLoadNextAction;
    private AsyncLoadPurpose asyncLoadPurpose;
    private AsyncLoadTown asyncLoadTown;
    private Calendar calendar = Calendar.getInstance();
    private int mYear, mMonth, mDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), getActivity().MODE_PRIVATE);
        e_sampark = (E_CRM) getActivity().getApplicationContext();
        ((DistributerActivity) getActivity()).getSupportActionBar().setTitle("Home");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        setHasOptionsMenu(true);
        long id = e_sampark.getSymphonyDB().getMaxTownID();
        if (!e_sampark.getSharedPreferences().getBoolean(Const.ISLOADDATA, false)) {
            if (Util.isNetworkAvailable(getActivity())) {
                asyncLoadTown = new AsyncLoadTown();
                asyncLoadTown.execute(String.valueOf(id));
            } else {
                showAlertDialog(getActivity(), "One time internet required for load Required data", false);
            }
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkin:
                if (!(e_sampark.getSharedPreferences().getBoolean(Const.PREF_COMPLETE_VISIT, true))) {
                    CheckStatus checkStatus = new CheckStatus();
                    Bundle bundle = new Bundle();
                    bundle.putString("CUSID", e_sampark.getSharedPreferences().getString(Const.PREF_CUSTID, ""));
                    checkStatus.setArguments(bundle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.distHome, checkStatus, CheckStatus.class.getSimpleName()).hide(this).addToBackStack(null)
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.distHome, new CustomerListFragment(), CustomerListFragment.class.getSimpleName()).hide(this)
                            .addToBackStack(null).commit();
                }
                return true;
            case R.id.exit:
                System.exit(1);
                return true;
            case R.id.setting:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.distHome, new SettingFragment(), SettingFragment.class.getSimpleName()).hide(this)
                        .addToBackStack(null).commit();
                return true;
            case R.id.list:
                ((DistributerActivity) getActivity()).onSettingsSelect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            ((DistributerActivity) getActivity()).getSupportActionBar().setTitle("Home");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false); // disable the button
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // remove the left caret
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    /**
     * Load purpose
     */
    private class AsyncLoadPurpose extends AsyncTask<Void, Void, ArrayList<PurposeModel>> {
        WSPurpose wsPurpose;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<PurposeModel> doInBackground(Void... params) {
            String url = HTTP_ENDPOINT + "/CACS_Get_Pur_of_visit.asp?user=track_new&pass=track123&MNO=" + sharedPreferences.getString("usermobilenumber", "") + "&EMPID=" + e_sampark.getSharedPreferences().getString(Const.EMPID, "");
            wsPurpose = new WSPurpose();
            return wsPurpose.executePorposeLst(url);
        }

        @Override
        protected void onPostExecute(ArrayList<PurposeModel> purposeModels) {
            super.onPostExecute(purposeModels);
            if (!isCancelled()) {
                if (purposeModels != null && purposeModels.size() > 0) {
                    for (int i = 0; i < purposeModels.size(); i++) {
                        e_sampark.getSymphonyDB().insertPurpose(purposeModels.get(i));
                    }
                }
                asyncLoadNextAction = new AsyncLoadNextAction();
                asyncLoadNextAction.execute();
            }
        }
    }


    /**
     * load next action
     */
    private class AsyncLoadNextAction extends AsyncTask<Void, Void, ArrayList<NextActionModel>> {
        WSNextAction wsNextAction;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<NextActionModel> doInBackground(Void... params) {
            String url = HTTP_ENDPOINT + "/CACS_Get_NextAction.asp?user=track_new&pass=track123&MNO=" + sharedPreferences.getString("usermobilenumber", "") + "&EMPID=" + e_sampark.getSharedPreferences().getString(Const.EMPID, "");
            wsNextAction = new WSNextAction();
            return wsNextAction.executeNextActionList(url);
        }

        @Override
        protected void onPostExecute(ArrayList<NextActionModel> nextActionModels) {
            super.onPostExecute(nextActionModels);
            if (!isCancelled()) {
                if (nextActionModels != null && nextActionModels.size() > 0) {
                    for (int i = 0; i < nextActionModels.size(); i++) {
                        e_sampark.getSymphonyDB().insertNextAction(nextActionModels.get(i));
                    }
                }
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }

                e_sampark.getSharedPreferences().edit().putBoolean(Const.ISLOADDATA, true).commit();
            }
        }
    }


    private class AsyncLoadTown extends AsyncTask<String, Void, ArrayList<TownModel>> {
        WSTown wsTown;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDailog();
        }

        @Override
        protected ArrayList<TownModel> doInBackground(String... params) {
            String lasttownid = params[0];
            String url = HTTP_ENDPOINT + "/CACS_Get_Townlist.asp?user=track_new&pass=track123&MNO=" + sharedPreferences.getString("usermobilenumber", "") + "&EMPID=" + e_sampark.getSharedPreferences().getString(Const.EMPID, "") + "&lastid=" + lasttownid;
            wsTown = new WSTown();
            return wsTown.executeTown(url);
        }

        @Override
        protected void onPostExecute(ArrayList<TownModel> townlist) {
            super.onPostExecute(townlist);
            if (!isCancelled()) {
                if (townlist != null && townlist.size() > 0) {

                    e_sampark.getSymphonyDB().insertTown(townlist);

                }

                asyncLoadPurpose = new AsyncLoadPurpose();
                asyncLoadPurpose.execute();

            }
        }
    }

    private void showProgressDailog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Please Wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }


    public void showAlertDialog(Context context, final String message, final boolean isforcheckoutfirst) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (isforcheckoutfirst) {
                            CheckStatus checkStatus = new CheckStatus();
                            Bundle bundle = new Bundle();
                            bundle.putString("CUSID", e_sampark.getSharedPreferences().getString(Const.PREF_CUSTID, ""));
                            checkStatus.setArguments(bundle);
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.distHome, checkStatus, CheckStatus.class.getSimpleName()).hide(HomeFragment.this).addToBackStack(null)
                                    .commit();

                        } else {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }


    // Postpone visit

    public void showNextActionDateDiglog(final String cacsvisitId, final String crmActID) {

//Initialize the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//Source of the data in the DIalog
        CharSequence[] array = {"Convert Into Call", "Postpone visit"};

// Set the dialog title
        builder.setTitle(getString(R.string.app_name))
// Specify the list array, the items to be selected by default (null for none),
// and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == 0) {
                            if ((e_sampark.getSharedPreferences().getBoolean(Const.PREF_COMPLETE_VISIT, true))) {
                                CheckStatus checkStatus = new CheckStatus();
                                Bundle bundle = new Bundle();
                                bundle.putString("CUSID", crmActID);
                                bundle.putString("VISITREFERENCEID", cacsvisitId);
                                e_sampark.getSharedPreferences().edit().putString(Const.PREF_CUSTID, crmActID).commit();
                                checkStatus.setArguments(bundle);
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.distHome, checkStatus, CheckStatus.class.getSimpleName()).hide(HomeFragment.this)
                                        .addToBackStack(null).commit();
                            } else {
                                showAlertDialog(getActivity(), getString(R.string.alert_checkout_first), true);
                            }
                            dialog.dismiss();
                        } else if (which == 1) {
                            showPostposndVisitDailog(cacsvisitId, crmActID);
                        }
                    }
                });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * Display Dialog for Postposne visit
     *
     * @param cacsvisitId
     * @param crmActID
     */
    public void showNextActionDateDialog(final String cacsvisitId, final String crmActID) {
//        // custom dialog
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_nextaction);

        // set the custom dialog components - text, image and button
        final RadioGroup rgOption = (RadioGroup) dialog.findViewById(R.id.dialog_nextaction_rgoption);
        RadioButton rbOption = null;

        Button btnSubmit = (Button) dialog.findViewById(R.id.dialog_nextaction_btnSubmit);
        // if button is clicked, close the custom dialog
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = rgOption.getCheckedRadioButtonId();
                RadioButton rbOption = (RadioButton) dialog.findViewById(selectedId);
                if (rbOption != null) {

                    if (selectedId == R.id.dialog_nextaction_rbconvertcall) {
                        if ((e_sampark.getSharedPreferences().getBoolean(Const.PREF_COMPLETE_VISIT, true))) {
                            CheckStatus checkStatus = new CheckStatus();
                            Bundle bundle = new Bundle();
                            bundle.putString("CUSID", crmActID);
                            bundle.putString("VISITREFERENCEID", cacsvisitId);
                            e_sampark.getSharedPreferences().edit().putString(Const.PREF_CUSTID, crmActID).commit();
                            checkStatus.setArguments(bundle);
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.distHome, checkStatus, CheckStatus.class.getSimpleName()).hide(HomeFragment.this)
                                    .addToBackStack(null).commit();
                        } else {
                            showAlertDialog(getActivity(), getString(R.string.alert_checkout_first), true);
                        }
                        dialog.dismiss();
                    } else if (selectedId == R.id.dialog_nextaction_rbpostpone) {
                        showPostposndVisitDailog(cacsvisitId, crmActID);
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select option", Toast.LENGTH_LONG).show();
                }
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    /**
     * Display Dialog for Postposne visit
     *
     * @param cacsvisitId
     * @param crmActID
     */
    private void showPostposndVisitDailog(final String cacsvisitId, final String crmActID) {
//        // custom dialog
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_postponevisit);

        // set the custom dialog components - text, image and button
        final EditText etDatetime = (EditText) dialog.findViewById(R.id.dialog_postponevisit_et_date);
        ImageView imgCalender = (ImageView) dialog.findViewById(R.id.dialog_postponevisit_img_calender);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_postponevisit_btn_postpone);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etDatetime.getText().toString())) {
                    if (Util.isNetworkAvailable(getActivity())) {
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat(Const.POSTPONE_DATETIMEFORMAT_API);
                        String timeStamp = timeStampFormat.format(calendar.getTimeInMillis());
                        AsyncPostponeVisit asyncPostponeVisit = new AsyncPostponeVisit();
                        asyncPostponeVisit.execute(cacsvisitId, crmActID, timeStamp);
                        dialog.dismiss();
                    } else {
                        Util.showAlertDialog(getActivity(), "Please Check Internet Connection");
                    }

                } else {
                    Util.showAlertDialog(getActivity(), "Please enter Datetime to Postpone Visit");
                }

            }
        });
        imgCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etDatetime);
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void showDatePicker(final EditText editText) {
        // Get Current Date
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat(Const.VISIT_DATETIMEFORMAT);
                        String timeStamp = timeStampFormat.format(calendar.getTimeInMillis());
                        editText.setText(timeStamp);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private class AsyncPostponeVisit extends AsyncTask<String, Void, Boolean> {
        private WSPostpondVisit wsPostpondVisit;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Util.displayProgressDialog(getActivity());
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String CACS_VISITID = params[0];
            String CRMACTID = params[1];
            String NEXT_ACTION_DATE = params[2];
            String url = HTTP_ENDPOINT + "/eCRM_PostponeVisit.asp?user=android&pass=xand123&CACS_VISITID=" + CACS_VISITID + "&CRMACTID=" + CRMACTID + "&STATUS=postpone&NEXT_ACTION_DATE=" + NEXT_ACTION_DATE;
            wsPostpondVisit = new WSPostpondVisit();
            return wsPostpondVisit.postPondVisit(url);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!isCancelled()) {
                Util.dismissProgressDialog(progressDialog);
                Toast.makeText(getActivity(), wsPostpondVisit.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
