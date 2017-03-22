package com.symphony_ecrm.distributer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.symphony_ecrm.R;
import com.symphony_ecrm.database.CheckData;
import com.symphony_ecrm.database.DB;
import com.symphony_ecrm.http.HttpManager;
import com.symphony_ecrm.http.HttpStatusListener;

public class DistributerList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, DistributerListListener {

    private ListView distributerList;
    private DistributerAdapter distributerAdapter;
    private SearchView searchView;
    private Cursor searchCursor;
    private static String searchTerm;
    private DistributerActivityListener mDistributerListener;
    private ProgressDialog mProgressBar;
    private TextView emptyView;
    private SharedPreferences prefs;
    private String userMobileNumber;
    private MenuItem seachMenuItem;
    private DeleteAllDistributer distributerObserver;
    private Handler handler = new Handler();

    class DeleteAllDistributer extends ContentObserver {
        public DeleteAllDistributer(Handler h) {
            super(h);
        }

        public void onChange(boolean selfChange) {
            Toast.makeText(getActivity(), "Contact observer: content changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
//			    ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Distributor List");
        prefs = getActivity().getSharedPreferences(getString(R.string.app_name), getActivity().MODE_PRIVATE);
        View v = inflater.inflate(R.layout.distributer_list, null);
        v.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }

        });
        return v;
    }

    private void registerContentObservers() {
        ContentResolver cr = getActivity().getContentResolver();
        distributerObserver = new DeleteAllDistributer(handler);
        cr.registerContentObserver(Uri.parse("content://com.symphony_ecrm.database.DBProvider/deleteAllDistributer"), true,
                distributerObserver);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerContentObservers();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressBar = new ProgressDialog(getActivity());
        mProgressBar.setTitle("Loading Customer List");
        mProgressBar.setMessage("Please wait  ...");
        mProgressBar.setProgressStyle(mProgressBar.STYLE_SPINNER);
        mProgressBar.hide();
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setCancelable(false);
            /*searchView = (EditText) getActivity().findViewById(R.id.mainSearch);
            searchView.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub


					//distributerAdapter.getFilter().filter(s);


					searchTerm = s.toString() ;
					getActivity().getSupportLoaderManager().restartLoader(DISTRIBUTER_INFO.ID, null,
							DistributerList.this);
				}



			});*/
        //	searchView.setOnQueryTextListener(this);
        //	searchView.setSubmitButtonEnabled(true);
        //	searchView.setIconified(true);

        mDistributerListener = (DistributerActivityListener) getActivity();
        emptyView = new TextView(getActivity());
        distributerList = (ListView) getActivity().findViewById(R.id.distributerList);
        emptyView = (TextView) getActivity().findViewById(R.id.emptyView);
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        //	View emptyView =  getActivity().findViewById();
        distributerAdapter = new DistributerAdapter(getActivity(),
                R.layout.distributer_list,
                null,
                DISTRIBUTER_INFO.PROJECTION,
                DISTRIBUTER_INFO.RESOURCES,
                0
        );


        distributerList.setAdapter(distributerAdapter);
        distributerList.setOnItemClickListener(new OnItemClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                if (seachMenuItem != null) {
                    seachMenuItem.collapseActionView();
                }
                Cursor cur = (Cursor) adapter.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("distname", cur.getString(cur.getColumnIndex(DB.DIST_NAME)));
                bundle.putString("distaddr", cur.getString(cur.getColumnIndex(DB.DIST_ADDRESS)));
                bundle.putString("distid", cur.getString(cur.getColumnIndex(DB.DIST_ID)));
                bundle.putString("distkey", cur.getString(cur.getColumnIndex(DB.DIST_KEY)));
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .add(R.id.distHome, new CheckStatus(), CheckStatus.class.getSimpleName()).addToBackStack(null).hide(DistributerList.this).commit();
                mDistributerListener.onDistributerListItemSelect(bundle);

					/*Intent intent = new Intent(DistributerList.this ,DistributerInfo.class);
                    intent.putExtra("distname",  cur.getString(cur.getColumnIndex(DB.DIST_NAME)));
					intent.putExtra("distaddr",  cur.getString(cur.getColumnIndex(DB.DIST_ADDRESS)));
					intent.putExtra("distcontactname",  cur.getString(cur.getColumnIndex(DB.DIST_CONTACT_PERSON)));					
					startActivity(intent); */

                //	Log.e("DISTRIBUTER NAME " , cur.getString(cur.getColumnIndex(DB.DIST_NAME))+"");
                //	Log.e("DISTRIBUTER KEY " , cur.getString(cur.getColumnIndex(DB.DIST_KEY))+"");
                //	Log.e("DISTRIBUTER ID " , cur.getString(cur.getColumnIndex(DB.DIST_ID))+"");
            }


        });

        distributerList.setTextFilterEnabled(true);
        if (searchView != null) {

        } else {
        }
            /*distributerAdapter.setFilterQueryProvider(new FilterQueryProvider(){

				@Override
				public Cursor runQuery(CharSequence constraint) {
					// TODO Auto-generated method stub
					
					Log.e("requery" , constraint+"");
					
					// Uri reQuery = Uri.withAppendedPath(DB.SALES_URI, Uri.encode(constraint));
				
					//if(searchCursor !=null) {
						
						
						//if(searchCursor.isClosed()) {
						
						if(!TextUtils.isEmpty(constraint)){
							searchTerm = constraint.toString();
							searchCursor =  getActivity().getBaseContext().getContentResolver().
								query(	Uri.parse("content://com.symphony.database.DBProvider/getDistributerByName"),
								DISTRIBUTER_INFO.PROJECTION, DB.DIST_NAME + " LIKE '"+ constraint+"%'", null, null);
						}
						
						//}
							 
				//	}
					return searchCursor;
					
					
				}
				
				
			});*/

        //Log.e("Search Term" , DistributerInfo.isDeleted +  " "  +searchTerm+" " +distributerAdapter.getCount());
        if (searchTerm == null || searchTerm == "" || DistributerInfo.isDeleted == true) {


            getActivity().getSupportLoaderManager().initLoader(DISTRIBUTER_INFO.ID, null, DistributerList.this);


        } else {

            getActivity().getSupportLoaderManager().initLoader(DISTRIBUTER_INFO.ID, null, DistributerList.this);

            //searchView.setText(searchTerm);
            //distributerAdapter.getFilter().filter(searchTerm);


        }

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
                        .hide(DistributerList.this)
                        .addToBackStack(AddCustomerFragment.class.getSimpleName())
                        .commit();
                return true;
            case R.id.distributer_refresh:


                if (!isNetworkAvailable()) {

                    Toast.makeText(getActivity(), "Network not available at this moment", Toast.LENGTH_SHORT).show();


                } else {

                    int count = getActivity().getBaseContext().getContentResolver().delete(Uri.parse("content://com.symphony_ecrm.database.DBProvider/deleteAllDistributer"), null, null);


                    // Log.e("REFRESH pressed" , count+"");

                    getActivity().getSupportLoaderManager().restartLoader(DISTRIBUTER_INFO.ID, null,
                            DistributerList.this);
                }
                // mProgressBar.show();
                // distributerAdapter.notifyDataSetChanged();
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
                new OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        searchTerm = "";
                        searchView.onActionViewCollapsed();
                        searchView.setQuery("", false);
                        searchView.clearFocus();
                        // Do something when collapsed
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {

                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    class DistributerAdapter extends SimpleCursorAdapter {
        private Context context;
        private LayoutInflater inflater;
        private Cursor cur;
        private int layout;

        public DistributerAdapter(Context context, int layout, Cursor c,
                                  String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            // TODO Auto-generated constructor stub

            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.cur = c;
            this.layout = layout;

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(R.layout.distributer_list_row, null);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            super.bindView(view, context, cursor);
            TextView distName = (TextView) view.findViewById(R.id.distName);
            TextView distAddress = (TextView) view.findViewById(R.id.distArea);
            ImageView distGo = (ImageView) view.findViewById(R.id.distgo);
            distGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckStatus checkStatus = new CheckStatus();
                    Bundle bundle = new Bundle();
                    bundle.putString("CUSID", cursor.getString(cursor.getColumnIndex(DB.DIST_ID)));
                    checkStatus.setArguments(bundle);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.distHome, checkStatus, CheckStatus.class.getSimpleName())
                            .commit();
                }
            });
            distName.setText(cursor.getString(cursor.getColumnIndex(DB.DIST_NAME)));
            String addr = cursor.getString(cursor.getColumnIndex(DB.DIST_AREA));

            if (addr != null)
                distAddress.setText(addr.toLowerCase());
            //distContactName.setText(cursor.getString(cursor.getColumnIndex(DB.DIST_CONTACT_PERSON)));
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle arg1) {
        // TODO Auto-generated method stub
        switch (id) {
            case DISTRIBUTER_INFO.ID:
                if (searchTerm == null || TextUtils.isEmpty(searchTerm)) {

                    return new CursorLoader(getActivity(),
                            Uri.parse("content://com.symphony_ecrm.database.DBProvider/distributer"),
                            DISTRIBUTER_INFO.PROJECTION,
                            null, null, null);

                } else {
                    return new CursorLoader(getActivity(),
                            Uri.parse("content://com.symphony_ecrm.database.DBProvider/getDistributerByName"),
                            DISTRIBUTER_INFO.PROJECTION,
                            DB.DIST_NAME + " LIKE '" + searchTerm + "%' OR " + DB.DIST_ADDRESS + " LIKE '%" + searchTerm + "%'", null, null);
                }

        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader,
                               final Cursor cursor) {
        // TODO Auto-generated method stub
        //	Log.e("ON LOADER FINISHED " , cursor.getCount() +" > "+searchTerm);
        if (cursor.getCount() == 0 && searchTerm != null) {
            //	Log.e("ON LOADER FINISHED " , "NO RECORD FOUND");
            emptyView.setText("NO RECORD(S) FOUND");
            emptyView.setVisibility(View.VISIBLE);
            //	if(mProgressBar.isShowing()) mProgressBar.dismiss();
        } else {
            emptyView.setText("");
            emptyView.setVisibility(View.GONE);

        }
        if (cursor.getCount() == 0 && TextUtils.isEmpty(searchTerm)) {
            mProgressBar.show();
            distributerAdapter.swapCursor(null);
            HttpManager httpMgr = new HttpManager(getActivity());
            userMobileNumber = prefs.getString("usermobilenumber", null);
            /** by pass**/

            //userMobileNumber = "9375494877";
            /** by pass**/
            if (userMobileNumber != null) {
                httpMgr.getDistributers(userMobileNumber, new HttpStatusListener() {

                    @Override
                    public void onAddCustomerStatus(Boolean status) {

                    }
                    @Override
                    public void onVerifyStatus(Boolean status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onDistributerListLoad(Boolean status) {
                        // TODO Auto-generated method stub

                        //Log.e("DISTRIBUTER LIST"  , "LIST RECEIVED" );
                        if (mProgressBar.isShowing()) {
                            mProgressBar.dismiss();
                        }
                        if (status == true) {

                            getActivity().getSupportLoaderManager().restartLoader(DISTRIBUTER_INFO.ID, null,
                                    DistributerList.this);

                        } else {

                            if (!emptyView.isShown()) {
                                emptyView.setVisibility(TextView.VISIBLE);
                                emptyView.setText("NO RECORD(S) FOUND");
                            }


                        }

                        //mProgressBar.hide();


                    }

                    @Override
                    public void onVerifyMobileStatus(Boolean status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onTimeOut() {
                        // TODO Auto-generated method stub
                        mProgressBar.dismiss();
                        Toast.makeText(getActivity(), "Request Timeout occurs , please try again", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkDisconnect() {
                        // TODO Auto-generated method stub
                        mProgressBar.dismiss();
                        Toast.makeText(getActivity(), "Network not available at this moment", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCheckStatus(CheckData checkData) {
                        // TODO Auto-generated method stub

                    }


                });

            }


        } else {

            distributerAdapter.swapCursor(cursor);
        }


    }


    @Override
    public void onLoaderReset(Loader loader) {
        // TODO Auto-generated method stub

        distributerAdapter.swapCursor(null);

    }


    public interface DISTRIBUTER_INFO {


        public static final int ID = 1;

        public static final String[] PROJECTION = {

                DB.DIST_KEY,
                DB.DIST_ID,
                DB.DIST_NAME,
                DB.DIST_ADDRESS,
                DB.DIST_CONTACT_PERSON,
                DB.DIST_AREA

        };

        public static int[] RESOURCES = new int[]{

                R.id.distName,
                R.id.distArea,
                R.id.distContactName

        };
    }

    public void addDistributer() {

        ContentValues valueOne = new ContentValues();
        valueOne.put(DB.DIST_NAME, "PQR Company");
        valueOne.put(DB.DIST_CONTACT_PERSON, "John Caro");
        valueOne.put(DB.DIST_ADDRESS, "55 club road");
        getActivity().getBaseContext().getContentResolver().insert(Uri.parse("content://com.symphony_ecrm.database.DBProvider/addDistributer"),

                valueOne);
    }

    @Override
    public boolean onQueryTextChange(String text) {
        // TODO Auto-generated method stub
        //Log.e("value" , text+"");
        if (TextUtils.isEmpty(text)) {

            if (distributerList != null) {
                searchTerm = "";
                //	Toast.makeText(getActivity(), " Text empty", Toast.LENGTH_LONG).show();
                getActivity().getSupportLoaderManager().restartLoader(DISTRIBUTER_INFO.ID, null,
                        DistributerList.this);
//				
//
//				getActivity().getSupportLoaderManager().restartLoader(DISTRIBUTER_INFO.ID, null,
//						DistributerList.this);

//				distributerList.clearTextFilter();
            }
        } else {
            //distributerList.setFilterText(text);
            //distributerAdapter.getFilter().filter(text);
            //distributerAdapter.notifyDataSetChanged();
            if (distributerList != null) {
                searchTerm = text;
                getActivity().getSupportLoaderManager().restartLoader(DISTRIBUTER_INFO.ID, null,
                        DistributerList.this);
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String submitText) {
        // TODO Auto-generated method stub
        //Log.e("SUBMIT" , submitText+"");
        getActivity().getSupportLoaderManager().restartLoader(DISTRIBUTER_INFO.ID, null,
                DistributerList.this);
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
    public void onListItemRemoved(String distKey, String distId) {
        // TODO Auto-generated method stub
        //Log.e("REMOVED " , distKey  +" " +distId);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Customer List");
            getActivity().getSupportLoaderManager().restartLoader(DISTRIBUTER_INFO.ID, null,
                    DistributerList.this);
        }
    }
}
