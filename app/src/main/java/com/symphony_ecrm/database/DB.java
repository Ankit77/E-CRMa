package com.symphony_ecrm.database;

import android.net.Uri;

public class DB {


    //database
    public static final String DATABASE_NAME = "salesapp";
    public static final String CREATE_DATABASE = "CREATE DATABASE " + DATABASE_NAME;
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_STRING = "symphonysales";


    //url matcher constant
    //fetch all distributer from database
    public static final int LIST_DISTRIBUTOR = 1;
    public static final int ADD_DISTRIBUTER = 2;
    public static final int SEARCH_DISTRIBUTER = 3;
    public static final int ADD_CHECK_STATUS = 4;
    public static final int DELETE_DISTRIBUTER = 5;
    public static final int DELETE_DISTRIBUTER_ID = 6;
    public static final int ADD_DISTRIBUTER_META_DATA = 8;
    public static final int LIST_DISTRIBUTER_META_DATA = 9;
    public static final int DELETE_DISTRIBUTER_METADATA = 10;


    public static final int UPDATE_DISTRIBUTER_METADATA = 11;
    public static final int UPDATE_CHECK_STATUS = 12;

    public static final int LIST_CHECK_DATA = 14;

    public static final int LIST_DISTRIBUTER_VIEW_DATA = 15;
    public static final int ADD_USER = 16;
    public static final int ADD_NOTIFICATION = 17;
    public static final int LIST_NOTIFICATION = 18;
    public static final int UPDATE_DISTRIBUTER_DATA = 19;

    public static final int DELETE_DISTRIBUTER_REPORT_DATA = 20;
    public static final int DELETE_CHECK_REPORT_DATA = 21;
    public static final int DELETE_NOTIFICATION_REPORT_DATA = 22;
    public static final int ADD_CUSTOMER_CODE = 23;
    public static final int ADD_CRM = 24;


    public static final String ADD_NEW_DISTRIBUTER = "addDistributer";
    public static final String SEARCH_DISTRIBUTER_NAME = "getDistributerByName";
    public static final String ADD_NEW_CHECK_STATUS = "addCheckStatus";
    public static final String DELETE_ALL_DISTRIBUTER = "deleteAllDistributer";
    public static final String DELETE_DISTRIBUTER_BY_ID = "deleteDistributerById";
    public static final String ADD_NEW_DISTRIBUTER_META_DATA = "addDistributerMetaData";
    public static final String GET_DISTRIBUTER_META_DATA = "getDistributerMetaData";
    public static final String DELETE_ALL_DISTRIBUTER_METADATA = "deleteAllDistributerData";
    public static final String UPDATE_FLAG_DISTRIBUTER_METADATA = "updateFlagDistributerMetaData";
    public static final String UPDATE_FLAG_CHECK_STATUS = "updateCheckFlagStatus";
    public static final String UPDATE_DISTRIBUTER = "updateDistributer";
    public static final String DELETE_DISTRIBUTER_REPORT = "deleteDistributerReport";
    public static final String DELETE_CHECK_REPORT = "deleteCheckReport";
    public static final String DELETE_NOTIFICATION_REPORT = "deleteNotificationReport";


    public static final String GET_CHECK_DATA = "getCheckData";
    public static final String GET_DISTRIBUTER_VIEW_DATA = "getDistributerViewData";

    public static final String ADD_NEW_USER = "addUser";
    public static final String ADD_NEW_NOTIFICATION = "addNewNotification";

    public static final String GET_NOTIFICATION_DATA = "getNotificationData";
    public static final String ADD_CUSTOMER_META_DATA = "addCustomerMetaData";


    public static final String DISTRIBUTER_META_DATA = "distributer_meta_data";
    public static final String DIST_META_KEY = "_id";
    public static final String DIST_META_ID = "dist_meta_id";
    public static final String DIST_LAT = "dist_lat";
    public static final String DIST_LNG = "dist_lng";
    public static final String DIST_IMG = "dist_img";
    public static final String DIST_IMG_URL = "dist_img_url";
    public static final String DIST_TIME = "dist_time";
    public static final String DIST_FLAG = "dist_flag";


    //table name
    public static final String DISTRIBUTER = "distributer_info";
    public static final String DISTRIBUTER_META_VIEW = "distributer_meta_view";

    //column names for distributor's list
    public static final String DIST_KEY = "_id";
    public static final String DIST_ID = "dist_id";
    public static final String DIST_NAME = "dist_name";
    public static final String DIST_CONTACT_PERSON = "dist_contact_name";
    public static final String DIST_ADDRESS = "dist_address";
    public static final String DIST_AREA = "dist_area";
    public static final String DIST_TIMESTAMP = "dist_timestamp";


    // create statement
    public static final String CREATE_DISTRIBUTER_TABLE =

            "CREATE TABLE IF NOT EXISTS " + DISTRIBUTER + " ( " +

                    DIST_KEY + " INTEGER primary key autoincrement , " +
                    DIST_ID + " TEXT , " +
                    DIST_NAME + " TEXT , " +
                    DIST_CONTACT_PERSON + " TEXT , " +
                    DIST_AREA + " TEXT ,  " +
                    DIST_ADDRESS + " TEXT " +

                    " ); ";


    // create statement
    public static final String CREATE_DISTRIBUTER_META_DATA_TABLE =

            "CREATE TABLE IF NOT EXISTS " + DISTRIBUTER_META_DATA + " ( " +

                    DIST_META_KEY + " INTEGER primary key autoincrement , " +
                    DIST_META_ID + " INTEGER , " +
                    DIST_NAME + " TEXT , " +
                    DIST_TIME + " TEXT , " +
                    DIST_LAT + " TEXT , " +
                    DIST_LNG + " TEXT , " +
                    DIST_IMG + " BLOB ,  " +
                    DIST_IMG_URL + " TEXT ," +
                    DIST_FLAG + " BOOLEAN , " +
                    DIST_TIMESTAMP + " TEXT  " +

									/*"FOREIGN KEY ( "+DIST_META_ID+" ) REFERENCES "+
                                    DISTRIBUTER + " ( "+DIST_KEY+" ) " +*/
                    " ); ";


    public static final String CREATE_DISTRIBUTER_VIEW =

            "CREATE VIEW IF NOT EXISTS " + DISTRIBUTER_META_VIEW + " AS " +
                    " SELECT  " +
                    DISTRIBUTER + "." + DIST_KEY + "  as _id , " +
                    DIST_META_ID + " , " +
                    DIST_TIME + " , " +
                    DIST_FLAG + " , " +
                    DIST_NAME + " , " +
                    DIST_ID + " , " +
                    DB.DIST_TIMESTAMP +


                    " FROM " + DISTRIBUTER_META_DATA + " , " + DISTRIBUTER + " " +
                    " WHERE " + DIST_META_ID + " =  " + DISTRIBUTER + "." + DIST_ID + ";";


    //table name
    public static final String CHECK = "check_info";

    //column names for check in & check out
    public static final String CHECK_ID = "_id";
    public static final String CHECK_STATUS = "check_status";
    public static final String CHECK_SMS = "check_sms";
    public static final String DIST_CHECK_KEY = "dist_check_id";
    public static final String CHECK_FLAG = "check_flag";
    public static final String CHECK_LAT = "check_lat";
    public static final String CHECK_LNG = "check_lng";
    public static final String DIST_CHECK_NAME = "dist_check_name";
    public static final String CHECK_TIMESTAMP = "check_timestamp";


    //table name
    public static final String CRM_CHECKINFO = "crm_check_info";

    //column names for check in & check out
    public static final String CRM_CHECKINFO_CRMID = "_id";
    public static final String CRM_CHECKINFO_CUST_ID = "crm_checkinfo_cust_id";
    public static final String CRM_CHECKINFO_COMPANYNAME = "crm_checkinfo_companyname";
    public static final String CRM_CHECKINFO_CONTACTPERSON = "crm_checkinfo_contactperson";
    public static final String CRM_CHECKINFO_LOCATION = "crm_checkinfo_location";
    public static final String CRM_CHECKINFO_DISCUSSION = "crm_checkinfo_discussion";
    public static final String CRM_CHECKINFO_PURPOSEVISIT = "crm_checkinfo_purposevisit";
    public static final String CRM_CHECKINFO_PURPOSEVISITID = "crm_checkinfo_purposevisitId";
    public static final String CRM_CHECKINFO_NEXTACTION = "crm_checkinfo_nextaction";
    public static final String CRM_CHECKINFO_NEXTACTIONID = "crm_checkinfo_nextactionId";
    public static final String CRM_CHECKINFO_NEXTACTIONDATE = "crm_checkinfo_nextactiondate";
    public static final String CRM_CHECKINFO_CHECKINLAT = "crm_checkinfo_checkinlat";
    public static final String CRM_CHECKINFO_CHECKINLONG = "crm_checkinfo_checkinlong";
    public static final String CRM_CHECKINFO_CHECKINTIMESTEMP = "crm_checkinfo_checkintimestemp";
    public static final String CRM_CHECKINFO_CHECKINIMAGEPATH = "crm_checkinfo_checkinimagepath";

    public static final String CRM_CHECKINFO_CHECKOUTLAT = "crm_checkinfo_checkoutlat";
    public static final String CRM_CHECKINFO_CHECKOUTLONG = "crm_checkinfo_checkoutlong";
    public static final String CRM_CHECKINFO_CHECKOUTIMESTEMP = "crm_checkinfo_checkoutimestemp";
    public static final String CRM_CHECKINFO_CHECKOUTIMAGEPATH = "crm_checkinfo_checkoutimagepath";

    public static final String CRM_CHECKINFO_CHECKSTATUS = "crm_checkinfo_checkstatus";
    public static final String CRM_CHECKINFO_CHECKFLAG = "crm_checkinfo_checkflag";
    public static final String CRM_CHECKINFO_ISSENDSERVER = "crm_checkinfo_issendserver";
    public static final String CRM_CHECKINFO_COMPLETEVISIT = "crm_checkinfo_completevisit";
    public static final String CRM_CHECKINFO_REFERENCEID = "crm_checkinfo_referenceid";


    // create statement
    public static final String CREATE_CRM_TABLE =

            "CREATE TABLE IF NOT EXISTS " + CRM_CHECKINFO + " ( " +

                    CRM_CHECKINFO_CRMID + " INTEGER primary key autoincrement , " +
                    CRM_CHECKINFO_CUST_ID + " TEXT , " +
                    CRM_CHECKINFO_COMPANYNAME + " TEXT , " +
                    CRM_CHECKINFO_CONTACTPERSON + " TEXT , " +
                    CRM_CHECKINFO_LOCATION + " TEXT  , " +
                    CRM_CHECKINFO_DISCUSSION + " TEXT , " +
                    CRM_CHECKINFO_PURPOSEVISIT + " TEXT , " +
                    CRM_CHECKINFO_PURPOSEVISITID + " TEXT , " +
                    CRM_CHECKINFO_NEXTACTION + " TEXT , " +
                    CRM_CHECKINFO_NEXTACTIONID + " TEXT , " +
                    CRM_CHECKINFO_NEXTACTIONDATE + " TEXT , " +
                    CRM_CHECKINFO_CHECKINLAT + " TEXT , " +
                    CRM_CHECKINFO_CHECKINLONG + " TEXT , " +
                    CRM_CHECKINFO_CHECKINTIMESTEMP + " TEXT , " +
                    CRM_CHECKINFO_CHECKINIMAGEPATH + " TEXT , " +
                    CRM_CHECKINFO_CHECKOUTLAT + " TEXT , " +
                    CRM_CHECKINFO_CHECKOUTLONG + " TEXT , " +
                    CRM_CHECKINFO_CHECKOUTIMESTEMP + " TEXT , " +
                    CRM_CHECKINFO_CHECKOUTIMAGEPATH + " TEXT , " +
                    CRM_CHECKINFO_CHECKSTATUS + " TEXT , " +
                    CRM_CHECKINFO_CHECKFLAG + " TEXT, " + CRM_CHECKINFO_COMPLETEVISIT + " TEXT," + CRM_CHECKINFO_ISSENDSERVER + " TEXT," + CRM_CHECKINFO_REFERENCEID + " TEXT )";


    // create statement
    public static final String CREATE_CHECK_TABLE =

            "CREATE TABLE IF NOT EXISTS " + CHECK + " ( " +

                    CHECK_ID + " INTEGER primary key autoincrement , " +
                    CHECK_STATUS + " INTEGER , " +
                    CHECK_SMS + " TEXT , " +
                    DIST_CHECK_KEY + " INTEGER  , " +
                    DIST_CHECK_NAME + " TEXT , " +
                    CHECK_FLAG + " BOOLEAN , " +
                    CHECK_LAT + " TEXT , " +
                    CHECK_LNG + " TEXT , " +
                    CHECK_TIMESTAMP + " TEXT ," +

                    "FOREIGN KEY ( " + DIST_CHECK_KEY + " ) REFERENCES " +
                    DISTRIBUTER + " ( " + DIST_KEY + " ) " +

                    " );";


    //table name
    public static final String USER = "user_info";

    //column names for check in & check out
    public static final String USER_ID = "_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_MOBILE = "user_mobile";
    public static final String USER_DEVICE_ID = "user_device_id";
    public static final String USER_TIMESTAMP = "user_timestamp";


    // create statement
    public static final String CREATE_USER_TABLE =

            "CREATE TABLE IF NOT EXISTS " + USER + " ( " +

                    USER_ID + " INTEGER primary key autoincrement , " +
                    USER_NAME + " TEXT , " +
                    USER_MOBILE + " TEXT , " +
                    USER_DEVICE_ID + " TEXT,  " +
                    USER_TIMESTAMP + " TEXT " +


                    " );";

    public static final Uri SALES_URI =
            Uri.parse("content://com.symphony_ecrm.database.DBProvider/distributer");


    public static final String NOTIFICATION = "notification";


    public static final String NOTIFICATION_ID = "_id";
    public static final String NOTIFICATION_MESSAGE = "not_message";
    public static final String NOTIFICATION_TIMESTAMP = "not_timestamp";
    public static final String NOTIFICATION_TYPE = "not_type";


    public static final String CREATE_NOTIFICATION_TABLE =

            "CREATE TABLE IF NOT EXISTS " + NOTIFICATION + " ( " +

                    NOTIFICATION_ID + " INTEGER primary key autoincrement , " +
                    NOTIFICATION_MESSAGE + " TEXT , " +
                    NOTIFICATION_TIMESTAMP + " TEXT , " +
                    NOTIFICATION_TYPE + " INTEGER" +


                    " );";


    //Customer
    public static final String CUSTOMER = "customer";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_CONTACT = "customer_contact";
    public static final String CUSTOMER_TOWN_ID = "customer_townid";


    static final String CREATE_CUSTOMER_TABLE = "CREATE TABLE IF NOT EXISTS " + CUSTOMER + " (" + CUSTOMER_ID + " TEXT, " + CUSTOMER_NAME
            + " TEXT," + CUSTOMER_ADDRESS + " TEXT," + CUSTOMER_CONTACT + " TEXT," + CUSTOMER_TOWN_ID + " TEXT)";


    //Town

    public static final String TOWN = "town";
    public static final String TOWN_ID = "town_id";
    public static final String TOWN_NAME = "town_name";

    static final String CREATE_TOWN_TABLE = "CREATE TABLE IF NOT EXISTS " + TOWN + " (" + TOWN_ID + " TEXT, " + TOWN_NAME
            + " TEXT)";

    //Purpose

    public static final String PURPOSE = "purpose";
    public static final String PURPOSE_ID = "purpose_id";
    public static final String PURPOSE_NAME = "purpose_name";

    static final String CREATE_PURPOSE_TABLE = "CREATE TABLE IF NOT EXISTS " + PURPOSE + " (" + PURPOSE_ID + " TEXT, " + PURPOSE_NAME
            + " TEXT)";

    //Next Action

    public static final String NEXTACTION = "nextAction";
    public static final String NEXTACTION_ID = "purpose_id";
    public static final String NEXTACTION_NAME = "purpose_name";

    static final String CREATE_NEXTACTION_TABLE = "CREATE TABLE IF NOT EXISTS " + NEXTACTION + " (" + NEXTACTION_ID + " TEXT, " + NEXTACTION_NAME
            + " TEXT)";

}
