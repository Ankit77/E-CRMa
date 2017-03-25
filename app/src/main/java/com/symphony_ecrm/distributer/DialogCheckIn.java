package com.symphony_ecrm.distributer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.symphony_ecrm.model.CustomerListModel;
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
 * 25-05-2016.
 */
public class DialogCheckIn extends DialogFragment implements View.OnClickListener {
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";

    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private Button btnSave;
    private Button btnSendToServer;
    private E_CRM e_crm;
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
    private TextView tvlabelActionDate;

    private String custmerID;
    private String checkInImage;
    private String checkInLat;
    private String checkInLong;
    private String checkInTimeStemp;
    private String checkOutImage;
    private String checkOutLat;
    private String checkOutLong;
    private String checkOutTimeStemp;
    private String visitType;
    private Calendar calendar = Calendar.getInstance();
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String purposeId;
    private String nextActionId;
    private CustomerListModel customerListModel;
    private CRMModel m_crmModel;
    private ProgressDialog progress;
    private FirebaseStorage storage;
    private String checkinUrl = "";
    private String checkouturl = "";
    private String refVisitId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        e_crm = (E_CRM) getActivity().getApplicationContext();
        storage = FirebaseStorage.getInstance();
        if (getArguments() != null) {
            visitType = getArguments().getString("TYPE");
            if (visitType.equalsIgnoreCase(Const.CHECKIN)) {
                checkInImage = getArguments().getString("CHECKINIMAGE", "");
                custmerID = getArguments().getString("CUSID", "");
                checkInLat = getArguments().getString("CHECKINLAT", "");
                checkInLong = getArguments().getString("CHECKINLONG", "");
                checkInTimeStemp = getArguments().getString("CHECKINTIMESTAMP", "");
                refVisitId = getArguments().getString("VISITREFERENCEID", "");

            } else if (visitType.equalsIgnoreCase(Const.CHECKOUT)) {
                checkOutImage = getArguments().getString("CHECKOUTIMAGE", "");
                checkOutLat = getArguments().getString("CHECKOUTLAT", "");
                checkOutLong = getArguments().getString("CHECKOUTLONG", "");
                checkOutTimeStemp = getArguments().getString("CHECKOUTTIMESTAMP", "");
            }
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.dialog_check_in, null);
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        return view;


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

    }

    private void initView(final View view) {
        loadNextAction();
        loadPurpose();


        etDiscussion = (EditText) view.findViewById(R.id.dialog_check_in_etDiscussion);
        tvContactPerson = (TextView) view.findViewById(R.id.dialog_check_in_tvContactPerson);
        spnrNextAction = (Spinner) view.findViewById(R.id.dialog_check_in_spnrNextAction);
        spnrPurposeofVisit = (Spinner) view.findViewById(R.id.dialog_check_in_spnrPurposeofvisit);
        tvLocation = (TextView) view.findViewById(R.id.dialog_check_in_tvLocation);
        tvCompanyName = (TextView) view.findViewById(R.id.dialog_check_in_tvCompanyName);
        tvPurposeofVisit = (TextView) view.findViewById(R.id.dialog_check_in_tvPurposeofvisit);
        tvNextAction = (TextView) view.findViewById(R.id.dialog_check_in_tvNextAction);
        tvNextActionDate = (TextView) view.findViewById(R.id.dialog_check_in_tvDateNextAction);
        tvlabelActionDate = (TextView) view.findViewById(R.id.dialog_check_in_tvlabelDateNextAction);
        btnSendToServer = (Button) view.findViewById(R.id.dialog_check_in_btnSendtoserver);
        btnSendToServer.setOnClickListener(this);
        btnSave = (Button) view.findViewById(R.id.dialog_check_in_btnSave);
        btnSave.setOnClickListener(this);
        tvCompanyName.setOnClickListener(this);
        tvNextAction.setOnClickListener(this);
        tvPurposeofVisit.setOnClickListener(this);
        tvNextActionDate.setOnClickListener(this);
        loadNextActionSpinner();
        loadPurposeSpinner();
        customerListModel = e_crm.getSymphonyDB().getCustomerInfo(custmerID);
        if (customerListModel != null) {
            tvCompanyName.setText(customerListModel.getCustomername());
            tvLocation.setText(customerListModel.getTown());
            tvContactPerson.setText(customerListModel.getContact());
        }

        if (visitType.equalsIgnoreCase(Const.CHECKOUT)) {
            btnSendToServer.setVisibility(View.VISIBLE);
        } else {
            btnSendToServer.setVisibility(View.GONE);
        }


//        final CheckStatus checkStatus = (CheckStatus) getTargetFragment();
//        if (checkStatus != null) {
//            if (checkStatus.isCheckStatus()) {
//                setCheckIn();
//            } else {
//                setCheckOut();
//            }
//        }


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
                    tvlabelActionDate.setVisibility(View.GONE);
                } else {
                    tvNextActionDate.setVisibility(View.VISIBLE);
                    tvlabelActionDate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (visitType.equalsIgnoreCase(Const.CHECKOUT)) {
            loadData();
        }

    }


    private void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                m_crmModel = e_crm.getSymphonyDB().getVisit((int) e_crm.getSharedPreferences().getLong("LASTROWID", 0));
                if (m_crmModel != null) {
                    custmerID = m_crmModel.getCusId();
                    tvCompanyName.setText(m_crmModel.getCompanyname());
                    tvLocation.setText(m_crmModel.getLocation());
                    etDiscussion.setText(m_crmModel.getDiscussion());
                    tvContactPerson.setText(m_crmModel.getConttactPerson());
                    if (!TextUtils.isEmpty(m_crmModel.getPurpose())) {
                        purposeId = m_crmModel.getPurposeId();
                        tvPurposeofVisit.setText(m_crmModel.getPurpose());
                    } else {
                        tvPurposeofVisit.setText(purposeList.get(0).getPurpose());
                        purposeId = purposeList.get(0).getId();
                    }
                    if (!TextUtils.isEmpty(m_crmModel.getNextaction())) {
                        tvNextAction.setText(m_crmModel.getNextaction());
                        nextActionId = m_crmModel.getNextactionId();
                    } else {
                        tvNextAction.setText(nextActionList.get(0).getType());
                        nextActionId = nextActionList.get(0).getTypeId();
                    }

                    tvNextActionDate.setText(m_crmModel.getActiondate());


                    calendar = Calendar.getInstance();
                    if (!TextUtils.isEmpty(m_crmModel.getActiondate())) {
                        calendar.setTimeInMillis(Util.getDateStringtoMilies(m_crmModel.getActiondate()));
                    }

                    if (TextUtils.isEmpty(m_crmModel.getNextaction())) {
                        tvNextActionDate.setVisibility(View.VISIBLE);
                        tvlabelActionDate.setVisibility(View.VISIBLE);
                    } else {
                        if (m_crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
                            tvNextActionDate.setVisibility(View.GONE);
                            tvlabelActionDate.setVisibility(View.GONE);
                        } else {
                            tvNextActionDate.setVisibility(View.VISIBLE);
                            tvlabelActionDate.setVisibility(View.VISIBLE);
                        }
                    }

                }
            }
        }, 200);

    }

    private boolean validateSyncToserver() {
        if (TextUtils.isEmpty(etDiscussion.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter Discussion", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(tvContactPerson.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter NextAction", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(tvPurposeofVisit.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter Purpose of visit", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(tvNextAction.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter NextAction", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(tvNextActionDate.getText().toString()) && !tvNextAction.getText().toString().equalsIgnoreCase("CloseLead")) {

            Toast.makeText(getActivity(), "Please enter Date", Toast.LENGTH_LONG).show();
            return false;
        } else if (!tvNextAction.getText().toString().equalsIgnoreCase("CloseLead") && !(calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())) {

            Toast.makeText(getActivity(), "Datetime must be greater than current date ", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_check_in_btnSave:
                if (visitType.equalsIgnoreCase(Const.CHECKIN)) {

                    checkIn();
                } else if (visitType.equalsIgnoreCase(Const.CHECKOUT)) {
                    checkOut(false);
                }
                break;
            case R.id.dialog_check_in_btnSendtoserver:
                if (validateSyncToserver()) {
                    if (visitType.equalsIgnoreCase(Const.CHECKOUT)) {
                        checkOut(true);
                    }
                }
                break;
            case R.id.dialog_check_in_tvPurposeofvisit:
                spnrPurposeofVisit.performClick();
                break;
            case R.id.dialog_check_in_tvNextAction:
                spnrNextAction.performClick();
                break;

            case R.id.dialog_check_in_tvDateNextAction:
                showDatePicker();
                break;
        }
    }

    private void checkIn() {

        e_crm.getSharedPreferences().edit().putString("TAG", Const.CHECKOUT).commit();
        e_crm.getSharedPreferences().edit().putBoolean(Const.PREF_COMPLETE_VISIT, false).commit();
        CheckStatus checkStatus = (CheckStatus) getActivity().getSupportFragmentManager().findFragmentByTag(CheckStatus.class.getSimpleName());
        if (checkStatus != null) {
            checkStatus.changeButtonState();
        }
        CRMModel crmModel = new CRMModel();
        crmModel.setCusId(custmerID);
        crmModel.setCheckInLat(checkInLat);
        crmModel.setCheckInLong(checkInLong);
        crmModel.setCheckInImagePath(checkInImage);
        crmModel.setCheckInTimeStemp(checkInTimeStemp);
        crmModel.setCompanyname(tvCompanyName.getText().toString());
        crmModel.setConttactPerson(tvContactPerson.getText().toString());
        crmModel.setLocation(tvLocation.getText().toString());
        crmModel.setDiscussion(etDiscussion.getText().toString());
        crmModel.setPurpose(tvPurposeofVisit.getText().toString());
        crmModel.setNextactionId(nextActionId);
        crmModel.setPurposeId(purposeId);
        crmModel.setNextaction(tvNextAction.getText().toString());
        crmModel.setReferenceVisitId(refVisitId);
        if (!crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
            crmModel.setActiondate(tvNextActionDate.getText().toString());
        }
        crmModel.setCheckStatus(0);
        crmModel.setCheckFlag(0);
        crmModel.setIsSendtoServer(0);
        crmModel.setIsCompleteVisit(0);
        long id = e_crm.getSymphonyDB().insertCRM(crmModel);
        e_crm.getSharedPreferences().edit().putLong("LASTROWID", id).commit();
        dismiss();

    }

    private void checkOut(boolean issendtoServer) {

        e_crm.getSharedPreferences().edit().putString("TAG", Const.CHECKIN).commit();
        e_crm.getSharedPreferences().edit().putBoolean(Const.PREF_COMPLETE_VISIT, true).commit();
        CheckStatus checkStatus = (CheckStatus) getActivity().getSupportFragmentManager().findFragmentByTag(CheckStatus.class.getSimpleName());
        if (checkStatus != null) {
            checkStatus.changeButtonState();
        }
        if (issendtoServer) {
            if (Util.isNetworkAvailable(getActivity())) {
                final String url = HTTP_ENDPOINT;

                // Create a storage reference from our app
//                For CheckIn
                showProgressDailog();
                if (new File(m_crmModel.getCheckInImagePath()).exists() && new File(checkOutImage).exists()) {
                    StorageReference storageRef_checkin = storage.getReferenceFromUrl(Const.BUCKET);
                    Uri file = Uri.fromFile(new File(m_crmModel.getCheckInImagePath()));
                    StorageReference riversRef_checkin = storageRef_checkin.child(Const.FOLDER_NAME + "/" + file.getLastPathSegment());
                    UploadTask uploadTask = riversRef_checkin.putFile(file);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            updateData(true);
                            exception.printStackTrace();
                            if (progress != null && progress.isShowing()) {
                                progress.dismiss();
                            }
                            dismiss();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            checkinUrl = downloadUrl.toString();
                            StorageReference storageRef_checkout = storage.getReferenceFromUrl(Const.BUCKET);
                            Uri file = Uri.fromFile(new File(checkOutImage));
                            StorageReference riversRef_checkout = storageRef_checkout.child(Const.FOLDER_NAME + "/" + file.getLastPathSegment());
                            final UploadTask uploadTask = riversRef_checkout.putFile(file);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    updateData(true);
                                    exception.printStackTrace();
                                    if (progress != null && progress.isShowing()) {
                                        progress.dismiss();
                                    }
                                    dismiss();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    checkouturl = downloadUrl.toString();
                                    m_crmModel.setCheckInImagePath(checkinUrl);
                                    m_crmModel.setCheckOutImagePath(checkouturl);
                                    if (progress != null && progress.isShowing()) {
                                        progress.dismiss();
                                    }
                                    AsyncSendVisit asyncSendVisit = new AsyncSendVisit();
                                    asyncSendVisit.execute(url, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), m_crmModel.getCusId(), m_crmModel.getLocation(), etDiscussion.getText().toString(), purposeId, nextActionId, checkinUrl, m_crmModel.getCheckInLat(), m_crmModel.getCheckInLong(), m_crmModel.getCheckInTimeStemp(), checkouturl, checkOutLat, checkOutLong, checkOutTimeStemp, tvNextActionDate.getText().toString(), tvContactPerson.getText().toString(), tvPurposeofVisit.getText().toString(), tvNextAction.getText().toString(), m_crmModel.getReferenceVisitId());

                                }
                            });

                        }
                    });
                } else {
                    AsyncSendVisit asyncSendVisit = new AsyncSendVisit();
                    asyncSendVisit.execute(url, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), m_crmModel.getCusId(), m_crmModel.getLocation(), etDiscussion.getText().toString(), purposeId, nextActionId, checkinUrl, m_crmModel.getCheckInLat(), m_crmModel.getCheckInLong(), m_crmModel.getCheckInTimeStemp(), checkouturl, checkOutLat, checkOutLong, checkOutTimeStemp, tvNextActionDate.getText().toString(), tvContactPerson.getText().toString(), tvPurposeofVisit.getText().toString(), tvNextAction.getText().toString(), m_crmModel.getReferenceVisitId());
                }

//                AsyncSendVisit asyncSendVisit = new AsyncSendVisit();
////                asyncSendVisit.execute(url, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), m_crmModel.getCusId(), m_crmModel.getLocation(), etDiscussion.getText().toString(), purposeId, nextActionId, m_crmModel.getCheckInImagePath(), m_crmModel.getCheckInLat(), m_crmModel.getCheckInLong(), m_crmModel.getCheckInTimeStemp(), checkOutImage, checkOutLat, checkOutLong, checkOutTimeStemp, tvNextActionDate.getText().toString(), tvContactPerson.getText().toString(), tvPurposeofVisit.getText().toString(), tvNextAction.getText().toString());
//                asyncSendVisit.execute(url, e_crm.getSharedPreferences().getString("usermobilenumber", ""), e_crm.getSharedPreferences().getString(Const.EMPID, ""), m_crmModel.getCusId(), m_crmModel.getLocation(), etDiscussion.getText().toString(), purposeId, nextActionId, checkinUrl, m_crmModel.getCheckInLat(), m_crmModel.getCheckInLong(), m_crmModel.getCheckInTimeStemp(), checkouturl, checkOutLat, checkOutLong, checkOutTimeStemp, tvNextActionDate.getText().toString(), tvContactPerson.getText().toString(), tvPurposeofVisit.getText().toString(), tvNextAction.getText().toString());
            } else {
                Util.showAlertDialog(getActivity(), "No Internet Connectivit available,Visit is added locally");
                updateData(true);
                dismiss();
            }
        } else {
            updateData(false);
            dismiss();
        }

    }


    private void updateData(boolean issendtoserver) {
        CRMModel crmModel = new CRMModel();
        crmModel.setCrmId((int) e_crm.getSharedPreferences().getLong("LASTROWID", 0));
        crmModel.setCheckOutLat(checkOutLat);
        crmModel.setCheckOutLong(checkOutLong);
        crmModel.setCheckOutImagePath(checkOutImage);
        crmModel.setCheckOutTimeStemp(checkOutTimeStemp);
        crmModel.setCompanyname(tvCompanyName.getText().toString());
        crmModel.setConttactPerson(tvContactPerson.getText().toString());
        crmModel.setLocation(tvLocation.getText().toString());
        crmModel.setDiscussion(etDiscussion.getText().toString());
        crmModel.setPurpose(tvPurposeofVisit.getText().toString());
        crmModel.setNextaction(tvNextAction.getText().toString());
        crmModel.setNextactionId(nextActionId);
        crmModel.setPurposeId(purposeId);

        if (!crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
            crmModel.setActiondate(tvNextActionDate.getText().toString());
        }
        crmModel.setCheckStatus(0);
        crmModel.setCheckFlag(0);
        if (issendtoserver) {
            crmModel.setIsSendtoServer(1);
        } else {
            crmModel.setIsSendtoServer(0);
        }


        crmModel.setIsCompleteVisit(1);
        long id = e_crm.getSymphonyDB().updateCRM(crmModel);
    }

    public void loadPurposeSpinner() {
        PurposeAdapter dataAdapter = new PurposeAdapter(getActivity(),
                android.R.layout.simple_spinner_item, purposeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrPurposeofVisit.setAdapter(dataAdapter);
        purposeId = purposeList.get(0).getId();
    }

    public void loadNextActionSpinner() {
        NextActionAdapter dataAdapter = new NextActionAdapter(getActivity(),
                android.R.layout.simple_spinner_item, nextActionList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrNextAction.setAdapter(dataAdapter);
        nextActionId = nextActionList.get(0).getTypeId();
    }


    private void loadPurpose() {
        purposeList = new ArrayList<>();
        purposeList = e_crm.getSymphonyDB().getPurposeList();
    }

    private void loadNextAction() {
        nextActionList = new ArrayList<>();
        nextActionList = e_crm.getSymphonyDB().getNextActionList();
    }


    private void showDatePicker() {
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat(Const.DATETIMEFORMAT);
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        String timeStamp = timeStampFormat.format(calendar.getTimeInMillis());
                        tvNextActionDate.setText(timeStamp);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    private class AsyncSendVisit extends AsyncTask<String, Void, Boolean> {
        CRMModel crmModel = new CRMModel();

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
            String referenceVisitId = params[20];

            WSVisit wsVisit = new WSVisit();
            boolean isSuccess = wsVisit.executeAddCustomer(url, mobileNumber, empid, customerid, location, discussion, puposeid, nextactionid, checkinimage, checkinLat, checkinLong, checkinTime, checkoutimage, checkoutLat, checkoutLong, checkoutTime, dateofnextaction, contactPerson, SymphonyUtils.getAppVersion(getActivity()), referenceVisitId);

            crmModel.setCrmId((int) e_crm.getSharedPreferences().getLong("LASTROWID", 0));
            crmModel.setCheckOutLat(checkoutLat);
            crmModel.setCheckOutLong(checkoutLong);
            crmModel.setCheckOutImagePath(checkoutimage);
            crmModel.setCheckInImagePath(checkinimage);
            crmModel.setCheckOutTimeStemp(checkoutTime);
            crmModel.setConttactPerson(contactPerson);
            crmModel.setLocation(location);
            crmModel.setDiscussion(discussion);
            crmModel.setPurpose(purpose);
            crmModel.setNextaction(nextaction);
            crmModel.setNextactionId(puposeid);
            crmModel.setPurposeId(nextactionid);
            if (!crmModel.getNextaction().equalsIgnoreCase("CloseLead")) {
                crmModel.setActiondate(dateofnextaction);
            }
            crmModel.setCheckFlag(1);
            crmModel.setIsCompleteVisit(1);
            crmModel.setIsSendtoServer(1);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (aVoid) {
                crmModel.setCheckStatus(1);

            } else {
                crmModel.setCheckStatus(0);
            }
            long id = e_crm.getSymphonyDB().updateCRM(crmModel);
            dismiss();
        }
    }

    private void showProgressDailog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Please Wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

}
