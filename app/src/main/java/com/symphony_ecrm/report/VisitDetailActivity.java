package com.symphony_ecrm.report;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.adapter.NextActionAdapter;
import com.symphony_ecrm.adapter.PurposeAdapter;
import com.symphony_ecrm.http.WSVisit;
import com.symphony_ecrm.model.CRMModel;
import com.symphony_ecrm.model.NextActionModel;
import com.symphony_ecrm.model.PurposeModel;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;
import com.symphony_ecrm.utils.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Ankit on 6/20/2016.
 */
public class VisitDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<PurposeModel> purposeList;
    private ArrayList<NextActionModel> nextActionList;

    private EditText etDiscussion;
    private TextView tvContactPerson;


    private TextView tvCompanyName;
    private TextView tvLocation;
    private TextView tvPurposeofVisit;
    private TextView tvNextAction;
    private TextView tvNextActionDate;
    private Spinner spnrPurposeofVisit;
    private Spinner spnrNextAction;
    private LinearLayout llPurposeofVisit;
    private LinearLayout llNextAction;

    private TextView tvCheckInLatLng;
    private TextView tvCheckInTimeStemp;
    private TextView tvCheckOutLatLng;
    private TextView tvCheckOutTimeStemp;
    private TextView tvLabelCompanyName;
    private TextView tvLabelLocation;
    private TextView tvLabelContactPerson;
    private TextView tvLabelDiscusstion;
    private TextView tvLabelPurposeofVisit;
    private TextView tvLabelNextAction;
    private TextView tvLabelDateOfNextAction;
    private Button btnSynctoServer;
    private Button btnCancel;
    private Button btnSave;
    private ImageView imgCheckInImage;
    private ImageView imgCheckoutImage;
    private View view;
    private CRMModel crmModel;
    private int CRMID;
    private E_CRM e_crm;
    private Calendar calendar = Calendar.getInstance();
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Menu mmenu;
    private String purposeId;
    private String nextActionId;
    private boolean isViewOnly = false;
    private ProgressDialog progress;
    private AsyncSendVisit asyncSendVisit;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";
    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private FirebaseStorage storage;
    private String checkinUrl = "";
    private String checkouturl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_visit_detail);
        storage = FirebaseStorage.getInstance();
        if (getIntent().getExtras() != null) {
            CRMID = getIntent().getExtras().getInt("CRMID", 0);
            isViewOnly = getIntent().getExtras().getBoolean("ISVIEWONLY", false);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); // disable the button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // remove the left caret
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Visit Detail");
        e_crm = (E_CRM) getApplicationContext();
        loadNextAction();
        loadPurpose();
        etDiscussion = (EditText) findViewById(R.id.fragment_visit_detail_etDiscussion);
        tvContactPerson = (TextView) findViewById(R.id.fragment_visit_detail_tvContactPerson);
        spnrNextAction = (Spinner) findViewById(R.id.fragment_visit_detail_spnrNextAction);
        spnrPurposeofVisit = (Spinner) findViewById(R.id.fragment_visit_detail_spnrPurposeofvisit);
        llNextAction = (LinearLayout) findViewById(R.id.fragment_visit_detail_llNextAction);
        llPurposeofVisit = (LinearLayout) findViewById(R.id.fragment_visit_detail_llPurposeofvisit);

        tvCompanyName = (TextView) findViewById(R.id.fragment_visit_detail_tvCompnayName);
        tvLocation = (TextView) findViewById(R.id.fragment_visit_detail_tvlocation);
        tvPurposeofVisit = (TextView) findViewById(R.id.fragment_visit_detail_tvPurposeofvisit);
        tvNextAction = (TextView) findViewById(R.id.fragment_visit_detail_tvNextAction);
        tvNextActionDate = (TextView) findViewById(R.id.fragment_visit_detail_tvDateNextAction);
        tvLabelCompanyName = (TextView) findViewById(R.id.fragment_visit_detail_tvLblCompnayName);
        tvLabelLocation = (TextView) findViewById(R.id.fragment_visit_detail_tvLbllocation);
        tvLabelContactPerson = (TextView) findViewById(R.id.fragment_visit_detail_tvLblContactPerson);
        tvLabelDiscusstion = (TextView) findViewById(R.id.fragment_visit_detail_tvlblDiscussion);
        tvLabelPurposeofVisit = (TextView) findViewById(R.id.fragment_visit_detail_tvlblPurposeofvisit);
        tvLabelNextAction = (TextView) findViewById(R.id.fragment_visit_detail_tvlblNextAction);
        tvLabelDateOfNextAction = (TextView) findViewById(R.id.fragment_visit_detail_tvlblDateNextAction);

        tvCompanyName.setOnClickListener(this);
        tvNextAction.setOnClickListener(this);
        tvPurposeofVisit.setOnClickListener(this);
        tvNextActionDate.setOnClickListener(this);
        loadNextActionSpinner();
        loadPurposeSpinner();


        tvCheckInLatLng = (TextView) findViewById(R.id.fragment_visit_detail_tvcheckInLatlong);
        tvCheckInTimeStemp = (TextView) findViewById(R.id.fragment_visit_detail_tvcheckinTime);
        tvCheckOutLatLng = (TextView) findViewById(R.id.fragment_visit_detail_tvcheckOutLatlong);
        tvCheckOutTimeStemp = (TextView) findViewById(R.id.fragment_visit_detail_tvcheckOutTime);
        imgCheckInImage = (ImageView) findViewById(R.id.fragment_visit_detail_tvcheckInImage);
        imgCheckoutImage = (ImageView) findViewById(R.id.fragment_visit_detail_tvcheckOutImage);
        btnCancel = (Button) findViewById(R.id.fragment_visit_detail_btnCancel);
        btnSynctoServer = (Button) findViewById(R.id.fragment_visit_detail_btnSendtoServer);
        btnSave = (Button) findViewById(R.id.fragment_visit_detail_btnSave);
        btnSynctoServer.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        //Company Name & Location will never change
        tvCompanyName.setEnabled(false);
        tvLocation.setEnabled(false);


        spnrPurposeofVisit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvPurposeofVisit.setText(purposeList.get(i).getPurpose());
                purposeId = purposeList.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnrNextAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                tvNextAction.setText(nextActionList.get(i).getType());
                nextActionId = nextActionList.get(i).getTypeId();

                if (nextActionList.get(i).getType().equalsIgnoreCase("CloseLead")) {
                    tvNextActionDate.setVisibility(View.GONE);
                    tvLabelDateOfNextAction.setVisibility(View.GONE);
                } else {
                    tvNextActionDate.setVisibility(View.VISIBLE);
                    if (tvNextAction.isEnabled()) {
                        tvLabelDateOfNextAction.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadData(false);
        enableControl(false);


    }

    @Override
    public void onClick(View view) {
        if (view == btnSynctoServer) {
            if (validateSyncToserver()) {
                updateVISIT(true);
            }

        } else if (view == btnCancel) {
            finish();
        } else if (view == btnSave) {
            updateVISIT(false);
            finish();
        } else if (view == tvNextAction) {
            spnrNextAction.performClick();
        } else if (view == tvPurposeofVisit) {
            spnrPurposeofVisit.performClick();
        } else if (view == tvNextActionDate) {
            showDatePicker();
        }
    }


//    private void validateSyncServer()
//    {
//        if(!TextUtils.isEmpty(etDiscussion.getText().toString()) && )
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mmenu = menu;
        getMenuInflater().inflate(R.menu.menu_visitdetail, menu);
        if (isViewOnly) {
            menu.findItem(R.id.edit).setVisible(false);
        } else {
            menu.findItem(R.id.edit).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.edit:
                mmenu.findItem(R.id.edit).setVisible(false);
                loadData(true);
                enableControl(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enableControl(boolean isEnable) {
        if (!isEnable) {
            tvCompanyName.setTextColor(Color.WHITE);
            tvCompanyName.setBackgroundColor(Color.TRANSPARENT);
            tvPurposeofVisit.setTextColor(Color.WHITE);
            llPurposeofVisit.setBackgroundColor(Color.TRANSPARENT);
            tvLocation.setTextColor(Color.WHITE);
            tvLocation.setTextColor(Color.WHITE);
            tvLocation.setBackgroundColor(Color.TRANSPARENT);
            etDiscussion.setTextColor(Color.WHITE);
            etDiscussion.setBackgroundColor(Color.TRANSPARENT);
            tvContactPerson.setTextColor(Color.WHITE);
            tvContactPerson.setBackgroundColor(Color.TRANSPARENT);
            tvNextAction.setTextColor(Color.WHITE);
            llNextAction.setBackgroundColor(Color.TRANSPARENT);
            tvNextActionDate.setTextColor(Color.WHITE);
            tvNextActionDate.setBackgroundColor(Color.TRANSPARENT);
            btnSave.setVisibility(View.GONE);
            btnSynctoServer.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            tvLabelCompanyName.setVisibility(View.GONE);
            tvLabelLocation.setVisibility(View.GONE);
            tvLabelContactPerson.setVisibility(View.GONE);
            tvLabelPurposeofVisit.setVisibility(View.GONE);
            tvLabelDiscusstion.setVisibility(View.GONE);
            tvLabelNextAction.setVisibility(View.GONE);
            tvLabelDateOfNextAction.setVisibility(View.GONE);

        } else {
            tvCompanyName.setTextColor(Color.BLACK);
            tvCompanyName.setBackgroundResource(R.drawable.edit_box);
            tvPurposeofVisit.setTextColor(Color.BLACK);
            llPurposeofVisit.setBackgroundResource(R.drawable.edit_box_drop_sel);
            tvLocation.setTextColor(Color.BLACK);
            tvLocation.setBackgroundResource(R.drawable.edit_box);
            etDiscussion.setTextColor(Color.BLACK);
            etDiscussion.setBackgroundResource(R.drawable.edit_box);
            tvContactPerson.setTextColor(Color.BLACK);
            tvContactPerson.setBackgroundResource(R.drawable.edit_box);
            tvNextAction.setTextColor(Color.BLACK);
            llNextAction.setBackgroundResource(R.drawable.edit_box_drop_sel);
            tvNextActionDate.setTextColor(Color.BLACK);
            tvNextActionDate.setBackgroundResource(R.drawable.edit_box);
            btnSave.setVisibility(View.VISIBLE);
            btnSynctoServer.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            tvLabelCompanyName.setVisibility(View.VISIBLE);
            tvLabelLocation.setVisibility(View.VISIBLE);
            tvLabelContactPerson.setVisibility(View.VISIBLE);
            tvLabelPurposeofVisit.setVisibility(View.VISIBLE);
            tvLabelDiscusstion.setVisibility(View.VISIBLE);
            tvLabelNextAction.setVisibility(View.VISIBLE);
            tvLabelDateOfNextAction.setVisibility(View.VISIBLE);
        }
        tvCompanyName.setEnabled(isEnable);
        tvPurposeofVisit.setEnabled(isEnable);
        tvContactPerson.setEnabled(isEnable);
        etDiscussion.setEnabled(isEnable);
        tvNextAction.setEnabled(isEnable);
        tvNextActionDate.setEnabled(isEnable);

    }

    private void loadData(final boolean isEnable) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                crmModel = e_crm.getSymphonyDB().getVisit(CRMID);
                if (crmModel != null) {
                    if (crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
                        tvNextActionDate.setVisibility(View.GONE);
                    } else {
                        tvNextActionDate.setVisibility(View.VISIBLE);
                    }
                    if (isEnable) {
                        tvCompanyName.setText(crmModel.getCompanyname());
                        tvPurposeofVisit.setText(crmModel.getPurpose());
                        tvLocation.setText(crmModel.getLocation());
                        tvContactPerson.setText(crmModel.getConttactPerson());
                        etDiscussion.setText(crmModel.getDiscussion());
                        tvNextAction.setText(crmModel.getNextaction());
                        tvNextActionDate.setText(crmModel.getActiondate());
                    } else {
                        tvCompanyName.setText("Company : " + crmModel.getCompanyname());
                        tvPurposeofVisit.setText("Purpose : " + crmModel.getPurpose());
                        tvLocation.setText("Location : " + crmModel.getLocation());
                        tvContactPerson.setText("Contact Person : " + crmModel.getConttactPerson());
                        etDiscussion.setText("Discussion : " + crmModel.getDiscussion());
                        tvNextAction.setText("Next Action : " + crmModel.getNextaction());
                        tvNextActionDate.setText("Date : " + crmModel.getActiondate());
                    }
                    tvCheckInLatLng.setText("Lat,Long : " + crmModel.getCheckInLat() + "," + crmModel.getCheckInLong());
                    tvCheckInTimeStemp.setText("Time : " + crmModel.getCheckInTimeStemp());
                    tvCheckOutLatLng.setText("Lat,Long : " + crmModel.getCheckOutLat() + "," + crmModel.getCheckOutLong());
                    tvCheckOutTimeStemp.setText("Time : " + crmModel.getCheckOutTimeStemp());
                    Glide.with(VisitDetailActivity.this).load(crmModel.getCheckInImagePath())
                            .placeholder(R.drawable.ic_placeholder)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgCheckInImage);
                    Glide.with(VisitDetailActivity.this).load(crmModel.getCheckOutImagePath())
                            .placeholder(R.drawable.ic_placeholder)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgCheckoutImage);
                    calendar = Calendar.getInstance();
                    if (!TextUtils.isEmpty(crmModel.getActiondate())) {
                        calendar.setTimeInMillis(Util.getDateStringtoMilies(crmModel.getActiondate()));
                    }
                } else {
                    if (mmenu != null)
                        mmenu.findItem(R.id.edit).setVisible(false);
                }
            }
        }, 200);

    }


    private void loadPurpose() {
        purposeList = new ArrayList<>();
        purposeList = e_crm.getSymphonyDB().getPurposeList();
    }

    private void loadNextAction() {
        nextActionList = new ArrayList<>();
        nextActionList = e_crm.getSymphonyDB().getNextActionList();
    }


    // add items into spinner dynamically
    public void loadPurposeSpinner() {
        PurposeAdapter dataAdapter = new PurposeAdapter(VisitDetailActivity.this,
                android.R.layout.simple_spinner_item, purposeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrPurposeofVisit.setAdapter(dataAdapter);
        purposeId = purposeList.get(0).getId();
    }

    public void loadNextActionSpinner() {
        NextActionAdapter dataAdapter = new NextActionAdapter(VisitDetailActivity.this,
                android.R.layout.simple_spinner_item, nextActionList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrNextAction.setAdapter(dataAdapter);
        nextActionId = nextActionList.get(0).getTypeId();
    }


    private void showDatePicker() {
        // Get Current Date
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(VisitDetailActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        showTimePicker();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Get Current Date

        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(VisitDetailActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat(Const.DATETIMEFORMAT);
                        String timeStamp = timeStampFormat.format(calendar.getTimeInMillis());
                        tvNextActionDate.setText(timeStamp);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private boolean validateSyncToserver() {
        if (TextUtils.isEmpty(etDiscussion.getText().toString())) {
            Toast.makeText(VisitDetailActivity.this, "Please enter Discussion", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(tvContactPerson.getText().toString())) {
            Toast.makeText(VisitDetailActivity.this, "Please enter Contact Person", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(tvPurposeofVisit.getText().toString())) {
            Toast.makeText(VisitDetailActivity.this, "Please enter Purpose of visit", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(tvNextAction.getText().toString())) {
            Toast.makeText(VisitDetailActivity.this, "Please enter NextAction", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(tvNextActionDate.getText().toString()) && !tvNextAction.getText().toString().equalsIgnoreCase("CloseLead")) {

            Toast.makeText(VisitDetailActivity.this, "Please enter Date", Toast.LENGTH_LONG).show();
            return false;
        } else if (!tvNextAction.getText().toString().equalsIgnoreCase("CloseLead") && !(calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())) {

            Toast.makeText(VisitDetailActivity.this, "Datetime must be greater than current date ", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private void updateVISIT(boolean isSyncToserver) {
        if (isSyncToserver) {
            final String url = HTTP_ENDPOINT;
            if (Util.isNetworkAvailable(VisitDetailActivity.this)) {
                showProgressDailog();
                StorageReference storageRef_checkin = storage.getReferenceFromUrl(Const.BUCKET);
                Uri file = Uri.fromFile(new File(crmModel.getCheckInImagePath()));
                StorageReference riversRef_checkin = storageRef_checkin.child(Const.FOLDER_NAME + "/" + file.getLastPathSegment());
                UploadTask uploadTask = riversRef_checkin.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                        }
                        crmModel.setConttactPerson(etDiscussion.getText().toString());
                        crmModel.setDiscussion(etDiscussion.getText().toString());
                        crmModel.setPurpose(tvPurposeofVisit.getText().toString());
                        crmModel.setPurposeId(purposeId);
                        crmModel.setNextaction(tvNextAction.getText().toString());
                        crmModel.setNextactionId(nextActionId);
                        if (!crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
                            crmModel.setActiondate(tvNextActionDate.getText().toString());
                        }
                        crmModel.setCheckStatus(0);
                        crmModel.setCheckFlag(0);
                        crmModel.setIsSendtoServer(1);
                        crmModel.setIsCompleteVisit(1);
                        exception.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        checkinUrl = downloadUrl.toString();
                        StorageReference storageRef_checkout = storage.getReferenceFromUrl(Const.BUCKET);
                        Uri file = Uri.fromFile(new File(crmModel.getCheckOutImagePath()));
                        StorageReference riversRef_checkout = storageRef_checkout.child(Const.FOLDER_NAME + "/" + file.getLastPathSegment());
                        final UploadTask uploadTask = riversRef_checkout.putFile(file);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                if (progress != null && progress.isShowing()) {
                                    progress.dismiss();
                                }
                                crmModel.setConttactPerson(etDiscussion.getText().toString());
                                crmModel.setDiscussion(etDiscussion.getText().toString());
                                crmModel.setPurpose(tvPurposeofVisit.getText().toString());
                                crmModel.setPurposeId(purposeId);
                                crmModel.setNextaction(tvNextAction.getText().toString());
                                crmModel.setNextactionId(nextActionId);
                                if (!crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
                                    crmModel.setActiondate(tvNextActionDate.getText().toString());
                                }
                                crmModel.setCheckStatus(0);
                                crmModel.setCheckFlag(0);
                                crmModel.setIsSendtoServer(1);
                                crmModel.setIsCompleteVisit(1);
                                exception.printStackTrace();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                checkouturl = downloadUrl.toString();
                                crmModel.setCheckInImagePath(checkinUrl);
                                crmModel.setCheckOutImagePath(checkouturl);
                                if (progress != null && progress.isShowing()) {
                                    progress.dismiss();
                                }
                                asyncSendVisit = new AsyncSendVisit();
                                asyncSendVisit.execute(url, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), crmModel.getCusId(), crmModel.getLocation(), etDiscussion.getText().toString(), purposeId, nextActionId, checkinUrl, crmModel.getCheckInLat(), crmModel.getCheckInLong(), crmModel.getCheckInTimeStemp(), checkouturl, crmModel.getCheckOutLat(), crmModel.getCheckOutLong(), crmModel.getCheckOutTimeStemp(), tvNextActionDate.getText().toString(), tvContactPerson.getText().toString(), tvPurposeofVisit.getText().toString(), tvNextAction.getText().toString(), crmModel.getReferenceVisitId());

                            }
                        });

                    }
                });


//                asyncSendVisit = new AsyncSendVisit();
//                asyncSendVisit.execute(url, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), crmModel.getCusId(), crmModel.getLocation(), etDiscussion.getText().toString(), purposeId, nextActionId, crmModel.getCheckInImagePath(), crmModel.getCheckInLat(), crmModel.getCheckInLong(), crmModel.getCheckInTimeStemp(), crmModel.getCheckOutImagePath(), crmModel.getCheckOutLat(), crmModel.getCheckOutLong(), crmModel.getCheckOutTimeStemp(), tvNextActionDate.getText().toString(), tvContactPerson.getText().toString(), tvPurposeofVisit.getText().toString(), tvNextAction.getText().toString());
            } else {
                showAlertDialog(VisitDetailActivity.this, "No Internet Connectivit available,Visit is added locally");
                crmModel.setConttactPerson(etDiscussion.getText().toString());
                crmModel.setDiscussion(etDiscussion.getText().toString());
                crmModel.setPurpose(tvPurposeofVisit.getText().toString());
                crmModel.setPurposeId(purposeId);
                crmModel.setNextaction(tvNextAction.getText().toString());
                crmModel.setNextactionId(nextActionId);
                if (!crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
                    crmModel.setActiondate(tvNextActionDate.getText().toString());
                }
                crmModel.setCheckStatus(0);
                crmModel.setCheckFlag(0);
                crmModel.setIsSendtoServer(1);
                crmModel.setIsCompleteVisit(1);
                long id = e_crm.getSymphonyDB().updateCRM(crmModel);
            }
        } else {
            crmModel.setConttactPerson(etDiscussion.getText().toString());
            crmModel.setDiscussion(etDiscussion.getText().toString());
            crmModel.setPurpose(tvPurposeofVisit.getText().toString());
            crmModel.setPurposeId(purposeId);
            crmModel.setNextaction(tvNextAction.getText().toString());
            crmModel.setNextactionId(nextActionId);
            if (!crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
                crmModel.setActiondate(tvNextActionDate.getText().toString());
            }
            crmModel.setCheckStatus(0);
            crmModel.setCheckFlag(0);
            crmModel.setIsSendtoServer(0);
            crmModel.setIsCompleteVisit(1);
            long id = e_crm.getSymphonyDB().updateCRM(crmModel);
        }

    }


    private class AsyncSendVisit extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDailog();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            String mobileNumber = params[1];
            String empid = params[2];
            String customerid = params[3];
            String location = params[4];
            String discussion = params[5];
            String puposeid = params[6];
            String nextactionid = params[7];
            String checkinimage = params[8];
            String checkinLat = params[9];
            String checkinLong = params[10];
            String checkinTime = params[11];
            String checkoutimage = params[12];
            String checkoutLat = params[13];
            String checkoutLong = params[14];
            String checkoutTime = params[15];
            String dateofnextaction = params[16];
            String contactPerson = params[17];
            String purpose = params[18];
            String nextaction = params[19];
            String referencevisitId = params[20];
            crmModel.setConttactPerson(contactPerson);
            crmModel.setDiscussion(discussion);
            crmModel.setPurpose(purpose);
            crmModel.setPurposeId(purposeId);
            crmModel.setNextaction(nextaction);
            crmModel.setNextactionId(nextActionId);
            if (!crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
                crmModel.setActiondate(dateofnextaction);
            }
            crmModel.setCheckFlag(1);
            crmModel.setIsCompleteVisit(1);
            crmModel.setIsSendtoServer(1);
            boolean issuccess = false;
            WSVisit wsVisit = new WSVisit();
            issuccess = wsVisit.executeAddCustomer(url, mobileNumber, empid, customerid, location, discussion, puposeid, nextactionid, checkinimage, checkinLat, checkinLong, checkinTime, checkoutimage, checkoutLat, checkoutLong, checkoutTime, dateofnextaction, contactPerson, SymphonyUtils.getAppVersion(VisitDetailActivity.this),referencevisitId);
            return issuccess;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (crmModel != null) {

                if (aVoid) {
                    crmModel.setCheckStatus(1);
                } else {
                    crmModel.setCheckStatus(0);
                }
                long id = e_crm.getSymphonyDB().updateCRM(crmModel);
                finish();
            }

        }
    }


    private void showProgressDailog() {
        progress = new ProgressDialog(VisitDetailActivity.this);
        progress.setMessage("Please Wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    public void showAlertDialog(Context context, final String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                        finish();
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }
}
