package com.symphony_ecrm.register;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.http.WSNextAction;
import com.symphony_ecrm.http.WSPurpose;
import com.symphony_ecrm.http.WSTown;
import com.symphony_ecrm.model.NextActionModel;
import com.symphony_ecrm.model.PurposeModel;
import com.symphony_ecrm.model.TownModel;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.Util;

import java.util.ArrayList;

/**
 * Created by user on 27-Jun-16.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ImageView imgRefreshTown;
    private ImageView imgRefreshPurpose;
    private ImageView imgRefreshNextAction;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";

    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private AsyncLoadTown asyncLoadTown;
    private AsyncLoadPurpose asyncLoadPurpose;
    private AsyncLoadNextAction asyncLoadNextAction;
    private ProgressDialog progress;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, null);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Setting");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgRefreshNextAction = (ImageView) view.findViewById(R.id.fragment_setting_imgRefshNextAction);
        imgRefreshPurpose = (ImageView) view.findViewById(R.id.fragment_setting_imgRefshPurpose);
        imgRefreshTown = (ImageView) view.findViewById(R.id.fragment_setting_imgRefshTown);
        imgRefreshTown.setOnClickListener(this);
        imgRefreshPurpose.setOnClickListener(this);
        imgRefreshNextAction.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_setting, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.distributer_listview) item.setVisible(false);

        switch (item.getItemId()) {


            case android.R.id.home:
                // work around
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
                ((AppCompatActivity) getActivity()).getSupportFragmentManager().popBackStack();
                return true;
            case R.id.symphony_exit:
                System.exit(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == imgRefreshPurpose) {
            if (Util.isNetworkAvailable(getActivity())) {
                asyncLoadPurpose = new AsyncLoadPurpose();
                asyncLoadPurpose.execute();
            } else {
                Util.showAlertDialog(getActivity(), getString(R.string.alert_noconnectivy));
            }
        } else if (view == imgRefreshNextAction) {
            if (Util.isNetworkAvailable(getActivity())) {
                asyncLoadNextAction = new AsyncLoadNextAction();
                asyncLoadNextAction.execute();
            } else {
                Util.showAlertDialog(getActivity(), getString(R.string.alert_noconnectivy));
            }

        } else if (view == imgRefreshTown) {
            if (Util.isNetworkAvailable(getActivity())) {
                long id = E_CRM.getsInstance().getSymphonyDB().getMaxTownID();
                asyncLoadTown = new AsyncLoadTown();
                asyncLoadTown.execute(String.valueOf(id));
            } else {
                Util.showAlertDialog(getActivity(), getString(R.string.alert_noconnectivy));
            }
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
            showProgressDailog();
        }

        @Override
        protected ArrayList<PurposeModel> doInBackground(Void... params) {
            String url = HTTP_ENDPOINT + "/CACS_Get_Pur_of_visit.asp?user=track_new&pass=track123&MNO=" + E_CRM.getsInstance().getSharedPreferences().getString("usermobilenumber", "") + "&EMPID=" + E_CRM.getsInstance().getSharedPreferences().getString(Const.EMPID, "");
            wsPurpose = new WSPurpose();
            return wsPurpose.executePorposeLst(url);
        }

        @Override
        protected void onPostExecute(ArrayList<PurposeModel> purposeModels) {
            super.onPostExecute(purposeModels);
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (!isCancelled()) {
                if (purposeModels != null && purposeModels.size() > 0) {
                    E_CRM.getsInstance().getSymphonyDB().deleteAllPurpose();
                    for (int i = 0; i < purposeModels.size(); i++) {
                        E_CRM.getsInstance().getSymphonyDB().insertPurpose(purposeModels.get(i));
                    }
                }

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
            showProgressDailog();

        }

        @Override
        protected ArrayList<NextActionModel> doInBackground(Void... params) {
            String url = HTTP_ENDPOINT + "/CACS_Get_NextAction.asp?user=track_new&pass=track123&MNO=" + E_CRM.getsInstance().getSharedPreferences().getString("usermobilenumber", "") + "&EMPID=" + E_CRM.getsInstance().getSharedPreferences().getString(Const.EMPID, "");
            wsNextAction = new WSNextAction();
            return wsNextAction.executeNextActionList(url);
        }

        @Override
        protected void onPostExecute(ArrayList<NextActionModel> nextActionModels) {
            super.onPostExecute(nextActionModels);
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (!isCancelled()) {
                if (nextActionModels != null && nextActionModels.size() > 0) {
                    E_CRM.getsInstance().getSymphonyDB().deleteAllNextAction();
                    for (int i = 0; i < nextActionModels.size(); i++) {
                        E_CRM.getsInstance().getSymphonyDB().insertNextAction(nextActionModels.get(i));
                    }
                }


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
            String url = HTTP_ENDPOINT + "/CACS_Get_Townlist.asp?user=track_new&pass=track123&MNO=" + E_CRM.getsInstance().getSharedPreferences().getString("usermobilenumber", "") + "&EMPID=" + E_CRM.getsInstance().getSharedPreferences().getString(Const.EMPID, "") + "&lastid=" + lasttownid;
            wsTown = new WSTown();
            return wsTown.executeTown(url);
        }

        @Override
        protected void onPostExecute(ArrayList<TownModel> townlist) {
            super.onPostExecute(townlist);
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (!isCancelled()) {
                if (townlist != null && townlist.size() > 0) {
                    E_CRM.getsInstance().getSymphonyDB().insertTown(townlist);

                }


            }
        }
    }

    private void showProgressDailog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Please Wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }
}
