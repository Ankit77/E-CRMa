package com.symphony_ecrm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;

import com.symphony_ecrm.model.CRMModel;
import com.symphony_ecrm.model.CustomerListModel;
import com.symphony_ecrm.model.NextActionModel;
import com.symphony_ecrm.model.PurposeModel;
import com.symphony_ecrm.model.TownModel;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;

public class SymphonyDB extends SQLiteOpenHelper {

    private SQLiteDatabase sqLiteDatabase;

    public SymphonyDB(Context context) {
        super(context, DB.DATABASE_NAME, null, DB.DATABASE_VERSION);
        // TODO Auto-generated constructor stub


    }

    @Override
    public void onCreate(SQLiteDatabase sqldb) {

        // TODO Auto-generated method stub

        sqldb.execSQL("PRAGMA foreign_keys = ON");
        //	sqldb.execSQL(DB.CREATE_DATABASE);
        sqldb.execSQL(DB.CREATE_USER_TABLE);

        sqldb.execSQL(DB.CREATE_DISTRIBUTER_TABLE);
        sqldb.execSQL(DB.CREATE_CHECK_TABLE);
        sqldb.execSQL(DB.CREATE_DISTRIBUTER_META_DATA_TABLE);
        //	sqldb.execSQL(DB.CREATE_DISTRIBUTER_VIEW);
        sqldb.execSQL(DB.CREATE_NOTIFICATION_TABLE);
        sqldb.execSQL(DB.CREATE_CUSTOMER_TABLE);
        sqldb.execSQL(DB.CREATE_NEXTACTION_TABLE);
        sqldb.execSQL(DB.CREATE_PURPOSE_TABLE);
        sqldb.execSQL(DB.CREATE_TOWN_TABLE);
        sqldb.execSQL(DB.CREATE_CRM_TABLE);
        sqLiteDatabase = sqldb;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqldb, int newversion, int oldversion) {
        // TODO Auto-generated method stub
        if (newversion > oldversion) {
            sqldb.execSQL("ALTER TABLE " + DB.CRM_CHECKINFO + " ADD COLUMN " + DB.CRM_CHECKINFO_REFERENCEID + " TEXT");
        }
        onCreate(sqldb);

    }

    public void openDataBase() throws SQLException {
        sqLiteDatabase = this.getWritableDatabase(DB.DATABASE_STRING);

    }

    public long insertCRM(CRMModel crmModel) {
        long val = -1;
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {

            openDataBase();
        }
        try {
            // database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DB.CRM_CHECKINFO_CUST_ID, crmModel.getCusId());
            values.put(DB.CRM_CHECKINFO_COMPANYNAME, crmModel.getCompanyname());
            values.put(DB.CRM_CHECKINFO_CONTACTPERSON, crmModel.getConttactPerson());
            values.put(DB.CRM_CHECKINFO_LOCATION, crmModel.getLocation());
            values.put(DB.CRM_CHECKINFO_DISCUSSION, crmModel.getDiscussion());
            values.put(DB.CRM_CHECKINFO_PURPOSEVISIT, crmModel.getPurpose());
            values.put(DB.CRM_CHECKINFO_PURPOSEVISITID, crmModel.getPurposeId());
            values.put(DB.CRM_CHECKINFO_NEXTACTION, crmModel.getNextaction());
            values.put(DB.CRM_CHECKINFO_NEXTACTIONID, crmModel.getNextactionId());
            values.put(DB.CRM_CHECKINFO_CHECKFLAG, crmModel.getCheckFlag());
            values.put(DB.CRM_CHECKINFO_NEXTACTIONDATE, crmModel.getActiondate());
            values.put(DB.CRM_CHECKINFO_CHECKINLAT, crmModel.getCheckInLat());
            values.put(DB.CRM_CHECKINFO_CHECKINLONG, crmModel.getCheckInLong());
            values.put(DB.CRM_CHECKINFO_CHECKINTIMESTEMP, crmModel.getCheckInTimeStemp());
            values.put(DB.CRM_CHECKINFO_CHECKINIMAGEPATH, crmModel.getCheckInImagePath());
            values.put(DB.CRM_CHECKINFO_CHECKOUTLAT, crmModel.getCheckOutLat());
            values.put(DB.CRM_CHECKINFO_CHECKOUTLONG, crmModel.getCheckOutLong());
            values.put(DB.CRM_CHECKINFO_CHECKOUTIMESTEMP, crmModel.getCheckOutTimeStemp());
            values.put(DB.CRM_CHECKINFO_CHECKOUTIMAGEPATH, crmModel.getCheckOutImagePath());
            values.put(DB.CRM_CHECKINFO_CHECKSTATUS, crmModel.getCheckStatus());
            values.put(DB.CRM_CHECKINFO_CHECKFLAG, crmModel.getCheckFlag());
            values.put(DB.CRM_CHECKINFO_COMPLETEVISIT, crmModel.getIsCompleteVisit());
            values.put(DB.CRM_CHECKINFO_ISSENDSERVER, crmModel.getIsSendtoServer());
            values.put(DB.CRM_CHECKINFO_REFERENCEID, crmModel.getReferenceVisitId());
            val = sqLiteDatabase.insert(DB.CRM_CHECKINFO, null, values);

        } catch (Exception e) {
            e.printStackTrace();
            val = -1;
        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
        return val;
    }


    /**
     * Update SMS
     *
     * @return
     */
    public long updateCRM(CRMModel crmModel) {
        long i = 0;
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        try {
            // database.beginTransaction();
            ContentValues values = new ContentValues();
            if (!TextUtils.isEmpty(crmModel.getCompanyname())) {
                values.put(DB.CRM_CHECKINFO_COMPANYNAME, crmModel.getCompanyname());
            }

            if (!TextUtils.isEmpty(crmModel.getConttactPerson())) {
                values.put(DB.CRM_CHECKINFO_CONTACTPERSON, crmModel.getConttactPerson());
            }

            if (!TextUtils.isEmpty(crmModel.getLocation())) {
                values.put(DB.CRM_CHECKINFO_LOCATION, crmModel.getLocation());
            }

            if (!TextUtils.isEmpty(crmModel.getDiscussion())) {
                values.put(DB.CRM_CHECKINFO_DISCUSSION, crmModel.getDiscussion());
            }
            if (!TextUtils.isEmpty(crmModel.getPurpose())) {
                values.put(DB.CRM_CHECKINFO_PURPOSEVISIT, crmModel.getPurpose());
            }
            if (!TextUtils.isEmpty(crmModel.getPurposeId())) {
                values.put(DB.CRM_CHECKINFO_PURPOSEVISITID, crmModel.getPurposeId());
            }
            if (!TextUtils.isEmpty(crmModel.getNextaction())) {
                values.put(DB.CRM_CHECKINFO_NEXTACTION, crmModel.getNextaction());
            }
            if (!TextUtils.isEmpty(crmModel.getNextactionId())) {
                values.put(DB.CRM_CHECKINFO_NEXTACTIONID, crmModel.getNextactionId());
            }
            if (!TextUtils.isEmpty(crmModel.getActiondate())) {
                values.put(DB.CRM_CHECKINFO_NEXTACTIONDATE, crmModel.getActiondate());
            }
            if (!TextUtils.isEmpty(crmModel.getCheckInLat())) {
                values.put(DB.CRM_CHECKINFO_CHECKINLAT, crmModel.getCheckInLat());
            }
            if (!TextUtils.isEmpty(crmModel.getCheckInLong())) {
                values.put(DB.CRM_CHECKINFO_CHECKINLONG, crmModel.getCheckInLong());
            }
            if (!TextUtils.isEmpty(crmModel.getCheckInTimeStemp())) {
                values.put(DB.CRM_CHECKINFO_CHECKINTIMESTEMP, crmModel.getCheckInTimeStemp());
            }
            if (!TextUtils.isEmpty(crmModel.getCheckInImagePath())) {
                values.put(DB.CRM_CHECKINFO_CHECKINIMAGEPATH, crmModel.getCheckInImagePath());
            }
            if (!TextUtils.isEmpty(crmModel.getCheckOutLat())) {
                values.put(DB.CRM_CHECKINFO_CHECKOUTLAT, crmModel.getCheckOutLat());
            }
            if (!TextUtils.isEmpty(crmModel.getCheckOutLong())) {
                values.put(DB.CRM_CHECKINFO_CHECKOUTLONG, crmModel.getCheckOutLong());
            }
            if (!TextUtils.isEmpty(crmModel.getCheckOutTimeStemp())) {
                values.put(DB.CRM_CHECKINFO_CHECKOUTIMESTEMP, crmModel.getCheckOutTimeStemp());
            }
            if (!TextUtils.isEmpty(crmModel.getCheckOutImagePath())) {
                values.put(DB.CRM_CHECKINFO_CHECKOUTIMAGEPATH, crmModel.getCheckOutImagePath());
            }

            values.put(DB.CRM_CHECKINFO_CHECKSTATUS, crmModel.getCheckStatus());
            values.put(DB.CRM_CHECKINFO_CHECKFLAG, crmModel.getCheckFlag());
            values.put(DB.CRM_CHECKINFO_COMPLETEVISIT, crmModel.getIsCompleteVisit());
            values.put(DB.CRM_CHECKINFO_ISSENDSERVER, crmModel.getIsSendtoServer());
            values.put(DB.CRM_CHECKINFO_REFERENCEID, crmModel.getReferenceVisitId());

            i = sqLiteDatabase.update(DB.CRM_CHECKINFO, values, DB.CRM_CHECKINFO_CRMID + "=?", new String[]{"" + crmModel.getCrmId()});

        } catch (Exception e) {
            e.printStackTrace();
            i = -1;

        } finally {
            close();
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
        return i;
    }


    /**
     * get SMS All SMSList
     *
     * @param
     * @return
     */
    public ArrayList<CRMModel> getVisitList() {
        final ArrayList<CRMModel> visitList = new ArrayList<CRMModel>();
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.CRM_CHECKINFO + " where " + DB.CRM_CHECKINFO_COMPLETEVISIT + " = 1";
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                CRMModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    model = new CRMModel();
                    model.setCrmId(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CRMID)));
                    model.setCusId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CUST_ID)));
                    model.setConttactPerson(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CONTACTPERSON)));
                    model.setCompanyname(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_COMPANYNAME)));
                    model.setLocation(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_LOCATION)));
                    model.setDiscussion(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_DISCUSSION)));
                    model.setPurpose(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_PURPOSEVISIT)));
                    model.setPurposeId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_PURPOSEVISITID)));
                    model.setNextaction(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTION)));
                    model.setNextactionId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTIONID)));
                    model.setActiondate(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTIONDATE)));
                    model.setCheckInLat(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINLAT)));
                    model.setCheckInLong(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINLONG)));
                    model.setCheckInTimeStemp(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINTIMESTEMP)));
                    model.setCheckInImagePath(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINIMAGEPATH)));
                    model.setCheckOutLat(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTLAT)));
                    model.setCheckOutLong(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTLONG)));
                    model.setCheckOutTimeStemp(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTIMESTEMP)));
                    model.setCheckOutImagePath(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTIMAGEPATH)));
                    model.setCheckStatus(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKSTATUS)));
                    model.setCheckFlag(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKFLAG)));
                    model.setIsCompleteVisit(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_COMPLETEVISIT)));
                    model.setIsSendtoServer(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_ISSENDSERVER)));
                    model.setReferenceVisitId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_REFERENCEID)));

                    visitList.add(model);
                    cursor.moveToNext();
                }
                visitList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return visitList;
    }


    /**
     * get visit list which have to sync to server
     *
     * @return
     */
    public ArrayList<CRMModel> getVisitForSynctoServer() {
        final ArrayList<CRMModel> visitList = new ArrayList<CRMModel>();
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.CRM_CHECKINFO + " where " + DB.CRM_CHECKINFO_ISSENDSERVER + " = 1 and " + DB.CRM_CHECKINFO_CHECKSTATUS + " = 0";
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                CRMModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    model = new CRMModel();
                    model.setCrmId(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CRMID)));
                    model.setCusId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CUST_ID)));
                    model.setConttactPerson(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CONTACTPERSON)));
                    model.setCompanyname(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_COMPANYNAME)));
                    model.setLocation(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_LOCATION)));
                    model.setDiscussion(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_DISCUSSION)));
                    model.setPurpose(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_PURPOSEVISIT)));
                    model.setPurposeId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_PURPOSEVISITID)));
                    model.setNextaction(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTION)));
                    model.setNextactionId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTIONID)));
                    model.setActiondate(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTIONDATE)));
                    model.setCheckInLat(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINLAT)));
                    model.setCheckInLong(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINLONG)));
                    model.setCheckInTimeStemp(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINTIMESTEMP)));
                    model.setCheckInImagePath(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINIMAGEPATH)));
                    model.setCheckOutLat(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTLAT)));
                    model.setCheckOutLong(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTLONG)));
                    model.setCheckOutTimeStemp(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTIMESTEMP)));
                    model.setCheckOutImagePath(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTIMAGEPATH)));
                    model.setCheckStatus(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKSTATUS)));
                    model.setCheckFlag(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKFLAG)));
                    model.setIsCompleteVisit(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_COMPLETEVISIT)));
                    model.setIsSendtoServer(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_ISSENDSERVER)));
                    model.setReferenceVisitId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_REFERENCEID)));
                    visitList.add(model);
                    cursor.moveToNext();
                }
                visitList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return visitList;
    }


    /**
     * get all visit which are synced
     *
     * @return
     */
    public ArrayList<CRMModel> getAllSyncedVisit() {
        final ArrayList<CRMModel> visitList = new ArrayList<CRMModel>();
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.CRM_CHECKINFO + " where " + DB.CRM_CHECKINFO_CHECKSTATUS + " = 1";
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                CRMModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    model = new CRMModel();
                    model.setCrmId(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CRMID)));
                    model.setCusId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CUST_ID)));
                    model.setConttactPerson(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CONTACTPERSON)));
                    model.setCompanyname(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_COMPANYNAME)));
                    model.setLocation(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_LOCATION)));
                    model.setDiscussion(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_DISCUSSION)));
                    model.setPurpose(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_PURPOSEVISIT)));
                    model.setPurposeId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_PURPOSEVISITID)));
                    model.setNextaction(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTION)));
                    model.setNextactionId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTIONID)));
                    model.setActiondate(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTIONDATE)));
                    model.setCheckInLat(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINLAT)));
                    model.setCheckInLong(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINLONG)));
                    model.setCheckInTimeStemp(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINTIMESTEMP)));
                    model.setCheckInImagePath(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINIMAGEPATH)));
                    model.setCheckOutLat(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTLAT)));
                    model.setCheckOutLong(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTLONG)));
                    model.setCheckOutTimeStemp(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTIMESTEMP)));
                    model.setCheckOutImagePath(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTIMAGEPATH)));
                    model.setCheckStatus(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKSTATUS)));
                    model.setCheckFlag(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKFLAG)));
                    model.setIsCompleteVisit(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_COMPLETEVISIT)));
                    model.setIsSendtoServer(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_ISSENDSERVER)));
                    model.setReferenceVisitId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_REFERENCEID)));

                    visitList.add(model);
                    cursor.moveToNext();
                }
                visitList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return visitList;
    }


    public CRMModel getVisit(int visitId) {
        CRMModel crmModel = null;
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.CRM_CHECKINFO + " where " + DB.CRM_CHECKINFO_CRMID + "=" + visitId;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                crmModel = new CRMModel();
                crmModel.setCrmId(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CRMID)));
                crmModel.setCusId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CUST_ID)));
                crmModel.setCompanyname(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_COMPANYNAME)));
                crmModel.setConttactPerson(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CONTACTPERSON)));
                crmModel.setLocation(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_LOCATION)));
                crmModel.setDiscussion(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_DISCUSSION)));
                crmModel.setPurpose(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_PURPOSEVISIT)));
                crmModel.setPurposeId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_PURPOSEVISITID)));
                crmModel.setNextaction(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTION)));
                crmModel.setNextactionId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTIONID)));
                crmModel.setActiondate(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_NEXTACTIONDATE)));
                crmModel.setCheckInLat(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINLAT)));
                crmModel.setCheckInLong(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINLONG)));
                crmModel.setCheckInTimeStemp(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINTIMESTEMP)));
                crmModel.setCheckInImagePath(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKINIMAGEPATH)));
                crmModel.setCheckOutLat(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTLAT)));
                crmModel.setCheckOutLong(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTLONG)));
                crmModel.setCheckOutTimeStemp(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTIMESTEMP)));
                crmModel.setCheckOutImagePath(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKOUTIMAGEPATH)));
                crmModel.setCheckStatus(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKSTATUS)));
                crmModel.setCheckFlag(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_CHECKFLAG)));
                crmModel.setIsCompleteVisit(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_COMPLETEVISIT)));
                crmModel.setIsSendtoServer(cursor.getInt(cursor.getColumnIndex(DB.CRM_CHECKINFO_ISSENDSERVER)));
                crmModel.setReferenceVisitId(cursor.getString(cursor.getColumnIndex(DB.CRM_CHECKINFO_REFERENCEID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return crmModel;
    }

    public void deleteAllVisit() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        try {
            sqLiteDatabase.delete(DB.CRM_CHECKINFO, DB.CRM_CHECKINFO_CHECKSTATUS + "=?", new String[]{"1"});
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
    }

    //------------------------------CUSTOMER---------------------


    public void deleteAllCustomer() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        try {
            sqLiteDatabase.delete(DB.CUSTOMER, null, null);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
    }
    //insert customer

    public void insertCustomer(ArrayList<CustomerListModel> customerList) {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {

            openDataBase();
        }
        try {
            sqLiteDatabase.beginTransaction();
            for (int i = 0; i < customerList.size(); i++) {
                CustomerListModel customerListModel = customerList.get(i);
                ContentValues values = new ContentValues();
                values.put(DB.CUSTOMER_ID, customerListModel.getId());
                values.put(DB.CUSTOMER_NAME, customerListModel.getCustomername());
                values.put(DB.CUSTOMER_ADDRESS, customerListModel.getAddress());
                values.put(DB.CUSTOMER_CONTACT, customerListModel.getContact());
                values.put(DB.CUSTOMER_TOWN_ID, customerListModel.getTown());
                sqLiteDatabase.insert(DB.CUSTOMER, null, values);
            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
    }

    /**
     * get SMS All SMSList
     *
     * @param
     * @return
     */
    public ArrayList<CustomerListModel> getCustomerList(String searchtext) {
        final ArrayList<CustomerListModel> customerList = new ArrayList<CustomerListModel>();
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "";
            if (TextUtils.isEmpty(searchtext)) {
                query = "Select * from " + DB.CUSTOMER;
            } else {
//                return new CursorLoader(getActivity(),
//                        Uri.parse("content://com.symphony.database.DBProvider/getDistributerByName"),
//                        DISTRIBUTER_INFO.PROJECTION,
//                        DB.DIST_NAME + " LIKE '" + searchTerm + "%' OR " + DB.DIST_ADDRESS + " LIKE '%" + searchTerm + "%'", null, null);

                query = "Select * from " + DB.CUSTOMER + " where " + DB.CUSTOMER_NAME + " LIKE '" + searchtext + "%' OR " + DB.CUSTOMER_ADDRESS + " LIKE '%" + searchtext + "%'  OR " + DB.CUSTOMER_CONTACT + " LIKE '" + searchtext + "%' OR " + DB.CUSTOMER_TOWN_ID + " LIKE '" + searchtext + "%'";
            }
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                CustomerListModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    model = new CustomerListModel();
                    model.setId(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_ID)));
                    model.setCustomername(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_NAME)));
                    model.setAddress(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_ADDRESS)));
                    model.setContact(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_CONTACT)));
                    model.setTown(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_TOWN_ID)));
                    customerList.add(model);
                    cursor.moveToNext();
                }
                customerList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return customerList;
    }

    public String getCustomerName(String cusid) {
        String townid = "";
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.CUSTOMER + " where " + DB.CUSTOMER_ID + "='" + cusid + "'";
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                townid = cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return townid;
    }


    public CustomerListModel getCustomerInfo(String cusid) {
        CustomerListModel customerListModel = null;
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.CUSTOMER + " where " + DB.CUSTOMER_ID + "='" + cusid + "'";
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                customerListModel = new CustomerListModel();
                customerListModel.setId(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_ID)));
                customerListModel.setCustomername(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_NAME)));
                customerListModel.setAddress(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_ADDRESS)));
                customerListModel.setContact(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_CONTACT)));
                customerListModel.setTown(cursor.getString(cursor.getColumnIndex(DB.CUSTOMER_TOWN_ID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return customerListModel;
    }
    //-----------------------TOWN--------------------------------
    //insert Town

    public void insertTown(ArrayList<TownModel> townList) {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {

            openDataBase();
        }
        try {

            sqLiteDatabase.beginTransaction();
            for (int i = 0; i < townList.size(); i++) {
                TownModel townModel = townList.get(i);
                ContentValues values = new ContentValues();
                values.put(DB.TOWN_ID, townModel.getTownId());
                values.put(DB.TOWN_NAME, townModel.getTown());
                sqLiteDatabase.insert(DB.TOWN, null, values);
            }

            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
    }


    /**
     * @param
     * @return
     */
    public ArrayList<TownModel> getTownList() {
        final ArrayList<TownModel> townList = new ArrayList<TownModel>();
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {

            String query = "Select * from " + DB.TOWN;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                TownModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    model = new TownModel();
                    model.setTownId(cursor.getString(cursor.getColumnIndex(DB.TOWN_ID)));
                    model.setTown(cursor.getString(cursor.getColumnIndex(DB.TOWN_NAME)));
                    townList.add(model);
                    cursor.moveToNext();
                }
                townList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return townList;
    }


    /**
     * @param
     * @return
     */
    public ArrayList<String> getTownListOnly() {
        final ArrayList<String> townList = new ArrayList<String>();
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {

            String query = "Select * from " + DB.TOWN;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                TownModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {

                    townList.add(cursor.getString(cursor.getColumnIndex(DB.TOWN_NAME)));
                    cursor.moveToNext();
                }
                townList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return townList;
    }

    /**
     * get townid from town
     *
     * @param town
     * @return
     */
    public String getTown(String town) {
        String townid = "";
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.TOWN + " where " + DB.TOWN_NAME + "='" + town + "'";
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                townid = cursor.getString(cursor.getColumnIndex(DB.TOWN_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return townid;
    }

    public long getMaxTownID() {
        int id = 0;
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        try {

            final String MY_QUERY = "SELECT MAX(" + DB.TOWN_ID + ") AS _id FROM " + DB.TOWN;
            Cursor mCursor = sqLiteDatabase.rawQuery(MY_QUERY, null);

            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            }
            mCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            id = -1;


        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }

        return id;
    }
    //-----------------------Purpose--------------------------------
    //insert Purpose

    public long insertPurpose(PurposeModel purposeModel) {
        long val = -1;
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {

            openDataBase();
        }
        try {
            // database.beginTransaction();
            ContentValues values = new ContentValues();

            values.put(DB.PURPOSE_ID, purposeModel.getId());
            values.put(DB.PURPOSE_NAME, purposeModel.getPurpose());
            val = sqLiteDatabase.insert(DB.PURPOSE, null, values);

        } catch (Exception e) {
            e.printStackTrace();
            val = -1;

        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
        return val;
    }

    public void deleteAllPurpose() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        try {
            sqLiteDatabase.delete(DB.PURPOSE, null, null);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
    }

    /**
     * @param
     * @return
     */
    public ArrayList<PurposeModel> getPurposeList() {
        final ArrayList<PurposeModel> purposeList = new ArrayList<PurposeModel>();
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.PURPOSE;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                PurposeModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    model = new PurposeModel();
                    model.setId(cursor.getString(cursor.getColumnIndex(DB.PURPOSE_ID)));
                    model.setPurpose(cursor.getString(cursor.getColumnIndex(DB.PURPOSE_NAME)));
                    purposeList.add(model);
                    cursor.moveToNext();
                }
                purposeList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return purposeList;
    }


    //-----------------------NextAction--------------------------------
    //insert Purpose

    public long insertNextAction(NextActionModel nextActionModel) {
        long val;
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {

            openDataBase();
        }
        try {
            // database.beginTransaction();
            ContentValues values = new ContentValues();

            values.put(DB.NEXTACTION_ID, nextActionModel.getTypeId());
            values.put(DB.NEXTACTION_NAME, nextActionModel.getType());
            val = sqLiteDatabase.insert(DB.NEXTACTION, null, values);

        } catch (Exception e) {
            e.printStackTrace();
            val = -1;

        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
        return val;
    }

    public void deleteAllNextAction() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        try {
            sqLiteDatabase.delete(DB.NEXTACTION, null, null);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
    }

    /**
     * @param
     * @return
     */
    public ArrayList<NextActionModel> getNextActionList() {
        final ArrayList<NextActionModel> nextactionList = new ArrayList<NextActionModel>();
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            openDataBase();
        }
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.NEXTACTION;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                NextActionModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    model = new NextActionModel();
                    model.setTypeId(cursor.getString(cursor.getColumnIndex(DB.NEXTACTION_ID)));
                    model.setType(cursor.getString(cursor.getColumnIndex(DB.NEXTACTION_NAME)));
                    nextactionList.add(model);
                    cursor.moveToNext();
                }
                nextactionList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return nextactionList;
    }


}
