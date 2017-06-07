package com.symphony_ecrm.distributer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.adapter.CustomerAdapter;
import com.symphony_ecrm.http.WSCustomer;
import com.symphony_ecrm.model.CustomerListModel;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.Util;

import java.util.ArrayList;

/**
 * Created by user on 26-Jun-16.
 */
public class CustomerListFragment extends Fragment implements
        SearchView.OnQueryTextListener, CustomerAdapter.myClickListner {

    private ArrayList<CustomerListModel> customerList;
    private ListView lvCusList;
    private CustomerAdapter customerAdapter;
    private SearchView searchView;
    private static String searchTerm;
    private DistributerActivityListener mDistributerListener;
    private ProgressDialog mProgressBar;
    private TextView emptyView;
    private SharedPreferences prefs;
    private String userMobileNumber;
    private MenuItem seachMenuItem;
    private Handler handler = new Handler();
    private ProgressDialog progress;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";
    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private boolean expanded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
//			    ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Distributor List");

        prefs = getActivity().getSharedPreferences(getString(R.string.app_name), getActivity().MODE_PRIVATE);
        View v = inflater.inflate(R.layout.fragment_customer, null);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        lvCusList = (ListView) getActivity().findViewById(R.id.fragment_customer_lvCusList);
        emptyView = (TextView) getActivity().findViewById(R.id.fragment_customer_tvempty);
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        lvCusList.setDividerHeight(0);
        customerList = new ArrayList<>();
        customerAdapter = new CustomerAdapter(customerList, getActivity());
        customerAdapter.setMmyClickListner(this);
        lvCusList.setAdapter(customerAdapter);
        final ArrayList<CustomerListModel> cuslist = E_CRM.getsInstance().getSymphonyDB().getCustomerList("");
        if (cuslist == null || cuslist.size() == 0) {
            if (Util.isNetworkAvailable(getActivity()))
                new AsyncLoadCustomer().execute();
        } else {
            customerList.addAll(cuslist);
            customerAdapter.notifyDataSetChanged();
        }


        lvCusList.setTextFilterEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.distributer_listview) item.setVisible(false);

        switch (item.getItemId()) {

            case android.R.id.home:
                // work around
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN/OUT");
                getFragmentManager().popBackStack();
                return true;
            case R.id.distributer_add:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.distHome, new AddCustomerFragment(), AddCustomerFragment.class.getSimpleName())
                        .hide(CustomerListFragment.this)
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.distributer_refresh:
                if (Util.isNetworkAvailable(getActivity())) {
                    new AsyncLoadCustomer().execute();
                } else {
                    Util.showAlertDialog(getActivity(), E_CRM.getsInstance().getString(R.string.alert_noconnectivy));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        //Log.e("LOAD MENU " , "MENU LOADING");
        menu.clear();
        inflater.inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.distributer_add).setVisible(true);
        menu.findItem(R.id.distributer_refresh).setVisible(true);
        menu.findItem(R.id.distributer_search).setVisible(true);
        menu.findItem(R.id.distributer_listview).setVisible(false);
        menu.findItem(R.id.symphony_settings).setVisible(false);
        menu.findItem(R.id.symphony_exit).setVisible(false);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        seachMenuItem = menu.findItem(R.id.distributer_search);
        searchView =
                (SearchView) MenuItemCompat.getActionView(seachMenuItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(this);

        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        MenuItemCompat.setOnActionExpandListener(seachMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        searchTerm = "";
                        expanded = false;
                        searchView.onActionViewCollapsed();
                        searchView.setQuery("", false);
                        searchView.clearFocus();

                        // Do something when collapsed
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        expanded = true;
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    public boolean isExpanded() {
        return expanded;
    }


    @Override
    public boolean onQueryTextChange(String text) {
        // TODO Auto-generated method stub
        //Log.e("value" , text+"");
        if (expanded) {
            if (TextUtils.isEmpty(text)) {

                if (lvCusList != null) {
                    searchTerm = "";
                }
            } else {
                if (lvCusList != null) {
                    searchTerm = text;
                }
            }
            customerList.clear();
            customerList.addAll(E_CRM.getsInstance().getSymphonyDB().getCustomerList(searchTerm));
            customerAdapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String submitText) {
        // TODO Auto-generated method stub
        //Log.e("SUBMIT" , submitText+"");
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Customer List");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN/OUT");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        //  Log.e("SEARCH TERM DIST LIST " , "stop -> search term is null");
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Customer List");
        }
    }

    @Override
    public void onClick(int position) {
        if (seachMenuItem != null) {
            seachMenuItem.collapseActionView();
        }
        CheckStatus checkStatus = new CheckStatus();
        Bundle bundle = new Bundle();
        bundle.putString("CUSID", customerList.get(position).getId());
        E_CRM.getsInstance().getSharedPreferences().edit().putString(Const.PREF_CUSTID, customerList.get(position).getId()).commit();
        checkStatus.setArguments(bundle);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.distHome, checkStatus, CheckStatus.class.getSimpleName())
                .commit();
    }

    private class AsyncLoadCustomer extends AsyncTask<Void, Void, ArrayList<CustomerListModel>> {
        WSCustomer wsCustomer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDailog();
        }

        @Override
        protected ArrayList<CustomerListModel> doInBackground(Void... voids) {

            String url = HTTP_ENDPOINT + "/CACS_Get_Customerlist.asp?user=track_new&pass=track123&MNO=" + prefs.getString("usermobilenumber", "") + "&EMPID=" + E_CRM.getsInstance().getSharedPreferences().getString(Const.EMPID, "");
            wsCustomer = new WSCustomer();
            return wsCustomer.executeCustomerLst(url);
        }

        @Override
        protected void onPostExecute(ArrayList<CustomerListModel> customerListModels) {
            super.onPostExecute(customerListModels);
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (customerListModels != null && customerListModels.size() > 0) {
                E_CRM.getsInstance().getSymphonyDB().deleteAllCustomer();
                E_CRM.getsInstance().getSymphonyDB().insertCustomer(customerListModels);
                customerList.clear();
                customerList.addAll(E_CRM.getsInstance().getSymphonyDB().getCustomerList(""));
                customerAdapter.notifyDataSetChanged();
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
