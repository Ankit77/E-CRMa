package com.symphony_ecrm.http;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.database.CheckData;
import com.symphony_ecrm.database.DB;
import com.symphony_ecrm.database.OTPData;
import com.symphony_ecrm.utils.Const;
import com.symphony_ecrm.utils.SymphonyUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HttpManager {


    private HttpClient httpClient;
    private HttpGet httpGet;
    private HttpResponse httpResponse;
    private HttpEntity httpEntity;
    private Context context;
    private boolean isTimeout = false;

    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";

    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;

//	private static final String HTTP_SERVER = "www.sl-erp.com";	
//	private static final String HTTP_PROTOCOL="http://";	
//	private String HTTP_ENDPOINT =HTTP_PROTOCOL+HTTP_SERVER; 


	/*public static  String OTP_CODE ="62514";
    public static  String USER_MOBILE_NUMBER="9375494877";*/

    public static String OTP_CODE;
    public static String USER_MOBILE_NUMBER;


    public static String HTTP_GET_DISTRIBUTER_LIST_URL;
    public static String HTTP_VERIFY_OPT_URL;
    public static String HTTP_VERIFY_MOBILE_URL;
    public static String HTTP_CHECK_URL;
    public static String HTTP_REGISTER_USER_URL;
    public static String HTTP_REGISTER_DEVICE_URL;
    public static String HTTP_REGISTER_DEVICE_URL_TEST;
    public static String HTTP_ADD_CUSTOMER_URL;


    public static final String HTTP_VERIFY_OPT = "1";
    public static final String HTTP_GET_DISTRIBUTER_LIST = "2";
    public static final String HTTP_VERIFY_MOBILE = "3";
    public static final String HTTP_CHECK_STATUS = "4";
    public static final String HTTP_REGISTER_USER = "5";
    public static final String HTTP_REGISTER_DEVICE = "6";
    public static final String HTTP_REGISTER_DEVICE_TEST = "7";
    public static final String HTTP_ADD_CUSTOMER = "8";

    private SharedPreferences prefs;

    public String versionNumber;

    private ArrayList<HttpStatusListener> httpStatusListener = new ArrayList<HttpStatusListener>();
    private ArrayList<OTPListener> otpListener = new ArrayList<OTPListener>();

    public HttpManager(Context context) {

        this.context = context;

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 120);
        HttpConnectionParams.setSoTimeout(httpParams, 1000 * 120);


        httpClient = new DefaultHttpClient(httpParams);
        versionNumber = SymphonyUtils.getAppVersion(context);

        prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);

        getMasterIP();

    }

    public void addCustomer(String mobileNumber, String empid, String customername, String address, String townid, String contactname, String designation, String mobile, String email, String pincode, HttpStatusListener listener) {


        if (httpStatusListener != null)
            httpStatusListener.add(listener);


        HTTP_ADD_CUSTOMER_URL = HTTP_ENDPOINT + "/CACS_Create_Customer.asp?user=track_new&pass=track123" +
                "&MNO=" + mobileNumber + "&EMPID=" + empid + "&CUSTOMERNAME=" + customername + "&Address=" + address + "&TownID=" + townid + "&ContactName=" + contactname + "&Designation=" + designation + "&Mobile=" + mobile + "&Email=" + email + "&pincode=" + pincode + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");
        //Log.e("HTTP REQUEST - Check Mobile Number " , HTTP_VERIFY_MOBILE_URL);

        new FireHttpRequest().execute(HTTP_ADD_CUSTOMER_URL, HTTP_ADD_CUSTOMER);
    }

    public void checkMobileNumber(String mobileNumber, HttpStatusListener listener) {


        if (httpStatusListener != null)
            httpStatusListener.add(listener);


        HTTP_VERIFY_MOBILE_URL = HTTP_ENDPOINT + "/CheckRegisterNumber_X1.asp?user=track_new&pass=track123" +
                "&MNO=" + mobileNumber + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");
        //Log.e("HTTP REQUEST - Check Mobile Number " , HTTP_VERIFY_MOBILE_URL);

        new FireHttpRequest().execute(HTTP_VERIFY_MOBILE_URL, HTTP_VERIFY_MOBILE);
    }

    public void verifyOTP(String otpCode, String userMobileNumber, HttpStatusListener listener) {


        OTP_CODE = otpCode;
        USER_MOBILE_NUMBER = userMobileNumber;


        HTTP_VERIFY_OPT_URL = HTTP_ENDPOINT + "/OTPCheck.asp?user=track_new&pass=track123&" +
                "OTP=" + OTP_CODE + "&MNO=" + USER_MOBILE_NUMBER + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");


        if (httpStatusListener != null)
            httpStatusListener.add(listener);

        //	Log.e("HTTP REQUEST - Verify URL" , HTTP_VERIFY_OPT_URL);

        new FireHttpRequest().execute(HTTP_VERIFY_OPT_URL, HTTP_VERIFY_OPT);
    }

    public void verifyOTP(String otpCode, String userMobileNumber, String registrationId, HttpStatusListener listener) {


        OTP_CODE = otpCode;
        USER_MOBILE_NUMBER = userMobileNumber;


        HTTP_VERIFY_OPT_URL = HTTP_ENDPOINT + "/OTPCheck_new_X1.asp?user=track_new&pass=track123&" +
                "OTP=" + OTP_CODE + "&MNO=" + USER_MOBILE_NUMBER + "&registrationId=" + registrationId + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");


        if (httpStatusListener != null)
            httpStatusListener.add(listener);

        //	Log.e("HTTP REQUEST - Verify URL" , HTTP_VERIFY_OPT_URL);

        new FireHttpRequest().execute(HTTP_VERIFY_OPT_URL, HTTP_VERIFY_OPT);
    }


    public void verifyOTP(String otpCode, String userMobileNumber) {

        OTP_CODE = otpCode;
        USER_MOBILE_NUMBER = userMobileNumber;


        HTTP_VERIFY_OPT_URL = HTTP_ENDPOINT + "/OTPCheck.asp?user=track_new&pass=track123&" +
                "OTP=" + OTP_CODE + "&MNO=" + USER_MOBILE_NUMBER + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");


        //	Log.e("HTTP REQUEST - Verify URL" , HTTP_VERIFY_OPT_URL);

        new FireHttpRequest().execute(HTTP_VERIFY_OPT_URL, HTTP_VERIFY_OPT);
    }


    public void registerUser(String userName, String mobileNumber, OTPListener listener) {


        HTTP_REGISTER_USER_URL = HTTP_ENDPOINT + "/esUserOTP.asp?user=track_new&pass=track123&" +
                "login=" + userName + "&MNO=" + mobileNumber + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");

        if (otpListener != null)
            otpListener.add(listener);

        //	Log.e("HTTP REQUEST - Register URL" , HTTP_REGISTER_USER_URL);


        new FireHttpRequest().execute(HTTP_REGISTER_USER_URL, HTTP_REGISTER_USER);

    }

    public void registerUser(String userName, String mobileNumber) {


        HTTP_REGISTER_USER_URL = HTTP_ENDPOINT + "/RegisterUser.asp?user=track_new&pass=track123&" +
                "USERNAME=" + userName + "&MNO=" + mobileNumber + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");

        new FireHttpRequest().execute(HTTP_REGISTER_USER_URL, HTTP_REGISTER_USER);

    }

    public void getDistributers(String userMobileNumber, HttpStatusListener listener) {

        USER_MOBILE_NUMBER = userMobileNumber;

        if (httpStatusListener != null)
            httpStatusListener.add(listener);

        HTTP_GET_DISTRIBUTER_LIST_URL = HTTP_ENDPOINT + "/Getdistlist.asp?user=track_new&pass=track123&" +
                "MNO=" + USER_MOBILE_NUMBER + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");

        new FireHttpRequest().execute(HTTP_GET_DISTRIBUTER_LIST_URL, HTTP_GET_DISTRIBUTER_LIST);

    }

    public void getDistributers(String userMobileNumber) {

        USER_MOBILE_NUMBER = userMobileNumber;

        HTTP_GET_DISTRIBUTER_LIST_URL = HTTP_ENDPOINT + "/Getdistlist.asp?user=track_new&pass=track123&" +
                "MNO=" + USER_MOBILE_NUMBER + (versionNumber != null ? "&v=v" + versionNumber : "v0.0");

        //Log.e("HTTP REQUEST - Distributer List" , HTTP_GET_DISTRIBUTER_LIST_URL);
        new FireHttpRequest().execute(HTTP_GET_DISTRIBUTER_LIST_URL, HTTP_GET_DISTRIBUTER_LIST);

    }

    public void sendCheckStatus(String smsBody, String id, HttpStatusListener listener) {


        if (smsBody != null) {

            if (httpStatusListener != null)
                httpStatusListener.add(listener);


            String smsArry[] = smsBody.split(",");

            //Log.e("sendCheckStatus" , smsArry.length + " " +smsBody);
            if (smsArry.length == 6) {

                HTTP_CHECK_URL = HTTP_ENDPOINT + "/MobilecheckIN_OUT_new.asp?user=track_new&pass=track123" +
                        "&Label=" + smsArry[0] +
                        "&mno=" + smsArry[1] +
                        "&Lat=" + smsArry[2] +
                        "&Long=" + smsArry[3] +
                        "&Timestamp=" + smsArry[4] +
                        "&v=" + smsArry[5];

                //	Log.e("CHECK STATUS URL" , HTTP_CHECK_URL+"");

                new FireHttpRequest().execute(HTTP_CHECK_URL, HTTP_CHECK_STATUS, smsBody, id);

            }


        }


        //HTTP_CHECK_URL =

    }

    public void start(String methodCall) {

        if (methodCall.equals(HTTP_GET_DISTRIBUTER_LIST))
            new FireHttpRequest().execute(HTTP_GET_DISTRIBUTER_LIST_URL, methodCall);
        else if (methodCall.equals(HTTP_VERIFY_OPT))
            new FireHttpRequest().execute(HTTP_VERIFY_OPT_URL, methodCall);
    }

    public void registerDeviceId(String username, String mobileNumber, String email, String regId, OTPListener listener) {


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss a");
        String currentDateandTime = sdf.format(new Date()).replace(" ", "");
        currentDateandTime = currentDateandTime.replace(".", "");


        HTTP_REGISTER_DEVICE_URL = HTTP_ENDPOINT + "/RegisterDevice_X1.asp?user=track_new&pass=track123&" +
                "username=" + username +
                "&email=" + email +
                "&MNO=" + mobileNumber +
                "&registrationId=" + regId +
                "&Timestamp=" + currentDateandTime +
                (versionNumber != null ? "&v=v" + versionNumber : "v0.0");


        if (otpListener != null)
            otpListener.add(listener);

        HTTP_REGISTER_DEVICE_URL_TEST = "http://121.244.200.162:3231/gcm_server_php/register.php?" +
                "name=" + username +
                "&email=" + email +
                "&regId=" + regId;


        //Log.e("REGISTER DEVICE URL" , HTTP_REGISTER_DEVICE_URL+"");


        // new FireHttpRequest().execute(HTTP_REGISTER_DEVICE_URL_TEST, HTTP_REGISTER_DEVICE_TEST);


        new FireHttpRequest().execute(HTTP_REGISTER_DEVICE_URL, HTTP_REGISTER_DEVICE);


    }

    class FireHttpRequest extends AsyncTask<String, Void, String[]> {


        @Override
        protected void onCancelled() {

            if (isTimeout)

                notifyOnTimeout();

            else
                notifyOnNetworkDisconnect();
        }

        @Override
        protected String[] doInBackground(String... params) {
            // TODO Auto-generated method stub


            try {


                String currentUrl = params[0];
                String methodCall = params[1];
                String smsBody = null;
                String custId = null;
                if (params.length == 4) {

                    smsBody = params[2];
                }


                httpGet = new HttpGet(currentUrl);

                if (!isNetworkAvailable()) {


                    cancel(false);

                    return null;

                }
                Log.e("TIMETOEXECUTE_URL", "url is " + currentUrl);
                Log.e("TIMETOEXECUTE", "Start-" + System.currentTimeMillis());
                long start = System.currentTimeMillis();
                httpResponse = httpClient.execute(httpGet);
                Log.e("TIMETOEXECUTE", "End-" + System.currentTimeMillis());
                long end = System.currentTimeMillis();
                long diff = end - start;
                Log.e("TIMETOEXECUTE_Diff", "diff-" + diff);

                //HTTP_REGISTER_USER

                if (methodCall.equals(HTTP_ADD_CUSTOMER)) {
                    addCustomer();
                }
                if (methodCall.equals(HTTP_REGISTER_DEVICE)) {


                    OTPData otpData = registerUser();

                    if (otpData != null) {

                        //Log.e("Data returned " , otpData.getOtp()+" "+otpData.isStatus());
                        String returnResult[] = {

                                HTTP_REGISTER_DEVICE,


                                String.valueOf(otpData.isStatus()),
                                String.valueOf(otpData.getOtp()),
                                String.valueOf(otpData.getMessage())


                        };

                        return returnResult;


                    } else {


                        String returnResult[] = {

                                HTTP_REGISTER_DEVICE,


                                null


                        };
                        return returnResult;

                    }

                }
                if (methodCall.equals(HTTP_GET_DISTRIBUTER_LIST)) {


                    String returnResult[] = {

                            HTTP_GET_DISTRIBUTER_LIST,
                            String.valueOf(getDistributer())

                    };

                    return returnResult;


                    //return getDistributer();
                }

                if (methodCall.equals(HTTP_VERIFY_OPT)) {


                    String returnResult[] = {

                            HTTP_VERIFY_OPT,
                            String.valueOf(verifyOTP())

                    };

                    return returnResult;


                }

                if (methodCall.equals(HTTP_REGISTER_USER)) {


                    OTPData otpData = registerUser();

                    //	Log.e("Data returned " , otpData.getOtp()+" "+otpData.isStatus());
                    String returnResult[] = {

                            HTTP_REGISTER_USER,


                            String.valueOf(otpData.isStatus()),
                            String.valueOf(otpData.getOtp()),


                    };

                    return returnResult;


                }


                if (methodCall.equals(HTTP_VERIFY_MOBILE)) {


                    String returnResult[] = {

                            HTTP_VERIFY_MOBILE,
                            String.valueOf(verifyMobile())

                    };

                    return returnResult;


                }

                if (methodCall.equals(HTTP_CHECK_STATUS)) {


                    //	Log.e("HTTP_CHECK_STATUS " , custId+"");

                    CheckData data = sendCheckINOUT(custId);
                    if (data == null) return null;
                    String returnResult[] = {

                            HTTP_CHECK_STATUS,
                            data.getCheckDistKey(),
                            data.getCheckDistName(),
                            data.getCheckLat(),
                            data.getCheckLng(),
                            String.valueOf(data.isCheckStatus())

                    };

                    return returnResult;


                }
                //Log.e("XML RESPONSE" , responseXml+"");


            } catch (HttpHostConnectException e) {


                isTimeout = true;
                cancel(false);

                e.printStackTrace();

                return null;
            } catch (SocketException e) {

                isTimeout = true;
                cancel(false);

                e.printStackTrace();

                return null;


            } catch (SocketTimeoutException e) {


                isTimeout = true;
                cancel(false);

                e.printStackTrace();

                return null;

            } catch (ConnectTimeoutException e) {


                isTimeout = true;
                cancel(false);

                e.printStackTrace();

                return null;


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {

            if (result == null) return;


            if (result.length <= 6) {

                if (result[0].equals(HTTP_GET_DISTRIBUTER_LIST)) {


                    //	Log.e("result data 1 " , result[1]+"");
                    notifyOnDistributerList(Boolean.parseBoolean(result[1]));

                }


                if (result[0].equals(HTTP_VERIFY_OPT)) {

                    //    Log.e("result data 2" , result[1]+"");
                    notifyOnVerifyOTP(Boolean.parseBoolean(result[1]));


                }
                if (result[0].equals(HTTP_VERIFY_MOBILE)) {

                    //    Log.e("result data 3" , result[1]+"");
                    notifyOnVerifyMobile(Boolean.parseBoolean(result[1]));


                }

                if (result[0].equals(HTTP_CHECK_STATUS)) {

                    //    Log.e("result data 4" , result[1]+" "+result.length);


                    notifyOnCheckStatus(new CheckData(result[1], result[2], result[3], result[4], result[5]));


                }

                if (result[0].equals(HTTP_REGISTER_USER)) {

                    //   Log.e("result data 5" , result[1]+" "+result.length);

                    notifyOtpReceived(

                            new OTPData(
                                    Boolean.parseBoolean(result[1]),
                                    result[2], result[3]
                            )
                    );


                }
                if (result[0].equals(HTTP_REGISTER_DEVICE)) {

                    //  Log.e("result data 6" , result[1]+"");

                    if (result[1] != null) {
                        notifyOtpReceived(

                                new OTPData(
                                        Boolean.parseBoolean(result[1]),
                                        result[2], "sdsd"
                                )
                        );
                    } else {


                        notifyOtpReceived(

                                null
                        );

                    }


                }

            }


        }


    }

    private void notifyOnCheckStatus(CheckData checkData) {


        for (HttpStatusListener listener : httpStatusListener) {

            listener.onCheckStatus(checkData);
        }

    }

    private void notifyOnDistributerList(boolean status) {
        // TODO Auto-generated method stub
        for (HttpStatusListener listener : httpStatusListener) {

            listener.onDistributerListLoad(status);
        }

    }

    private OTPData registerDevice() throws ParseException, IOException {

        httpEntity = httpResponse.getEntity();
        String responseXml = EntityUtils.toString(httpResponse.getEntity());

        StringReader strReader = new StringReader(responseXml);

        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(strReader);
            parser.nextTag();

            return parseRegisterDevice(parser);


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    private CheckData sendCheckINOUT(String id) throws ParseException, IOException {


        httpEntity = httpResponse.getEntity();

        String responseXml = EntityUtils.toString(httpResponse.getEntity());

        StringReader strReader = new StringReader(responseXml);


        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(strReader);
            parser.nextTag();

            return parseCheckINOUTResponse(parser, id);


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block


            e.printStackTrace();
            return null;
        }


    }

    private void notifyOnVerifyOTP(boolean status) {

        for (HttpStatusListener listener : httpStatusListener) {

            listener.onVerifyStatus(status);
        }


    }

    private void notifyOnTimeout() {

        for (HttpStatusListener listener : httpStatusListener) {

            listener.onTimeOut();
        }

        for (OTPListener listener : otpListener) {

            listener.onTimeOut();
        }

    }

    private void notifyOnNetworkDisconnect() {

        for (HttpStatusListener listener : httpStatusListener) {

            listener.onNetworkDisconnect();
        }

        for (OTPListener listener : otpListener) {

            listener.onNetworkDisconnect();
        }
    }

    private void notifyOnVerifyMobile(boolean status) {

        for (HttpStatusListener listener : httpStatusListener) {

            listener.onVerifyMobileStatus(status);
        }


    }

    private void notifyOtpReceived(OTPData otpData) {

        for (OTPListener listener : otpListener) {

            listener.onOtpReceived(otpData);
        }


    }


    private boolean verifyMobile() throws ParseException, IOException {


        httpEntity = httpResponse.getEntity();

        String responseXml = EntityUtils.toString(httpResponse.getEntity());

        StringReader strReader = new StringReader(responseXml);


        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(strReader);
            parser.nextTag();

            return parseVerfiyMobileResponse(parser);


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    private OTPData registerUser() throws ParseException, IOException {


        httpEntity = httpResponse.getEntity();

        String responseXml = EntityUtils.toString(httpResponse.getEntity());

        StringReader strReader = new StringReader(responseXml);


        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(strReader);
            parser.nextTag();

            return parseRegisterUsreResponse(parser);


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block


            e.printStackTrace();
        }
        return null;
    }


    private OTPData addCustomer() throws ParseException, IOException {


        httpEntity = httpResponse.getEntity();

        String responseXml = EntityUtils.toString(httpResponse.getEntity());

        StringReader strReader = new StringReader(responseXml);


        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(strReader);
            parser.nextTag();

            return parseRegisterUsreResponse(parser);


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block


            e.printStackTrace();
        }
        return null;
    }

    private boolean verifyOTP() throws ParseException, IOException {


        httpEntity = httpResponse.getEntity();

        String responseXml = EntityUtils.toString(httpResponse.getEntity());

        StringReader strReader = new StringReader(responseXml);


        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(strReader);
            parser.nextTag();

            return parseVerfiyOTPResponse(parser);


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }


    private boolean getDistributer() throws ParseException, IOException {


        httpEntity = httpResponse.getEntity();

        String responseXml = EntityUtils.toString(httpResponse.getEntity());


        StringReader strReader = new StringReader(responseXml);


        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(strReader);
            parser.nextTag();

            return parseDistributerListResponse(parser);


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }


    }

    private OTPData parseRegisterUsreResponse(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, null, "data");


        OTPData data = new OTPData();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if (name.equals("status")) {


                String status = readStatus(parser);
                //Log.e("REGISTER USER STATUS " , status+"");

                data.setStatus(status.equals("success") ? true : false);


            } else if (name.equals("otp")) {


                String otp = readOtp(parser);
                //Log.e("REGISTER USER OTP " , otp+"");

                data.setOtp(otp);


            } else if (name.equals("message")) {


                String message = readmessage(parser);
                //Log.e("REGISTER USER OTP " , otp+"");

                data.setMessage(message);
                E_CRM.getsInstance().getSharedPreferences().edit().putString(Const.MESSAGE, message).commit();


            } else if (name.equals("empid")) {


                String message = readempId(parser);
                //Log.e("REGISTER USER OTP " , otp+"");

                data.setMessage(message);


            } else if (name.equals("usertype")) {


                String message = readusertype(parser);
                //Log.e("REGISTER USER OTP " , otp+"");

                data.setMessage(message);


            }
        }
        return data;

    }


    private OTPData parseAddCustomerResponse(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, null, "data");


        OTPData data = new OTPData();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if (name.equals("status")) {


                String status = readStatus(parser);
                //Log.e("REGISTER USER STATUS " , status+"");

                data.setStatus(status.equals("success") ? true : false);


            } else if (name.equals("otp")) {


                String otp = readOtp(parser);
                //Log.e("REGISTER USER OTP " , otp+"");

                data.setOtp(otp);


            } else if (name.equals("message")) {


                String message = readmessage(parser);
                //Log.e("REGISTER USER OTP " , otp+"");

                data.setMessage(message);
                E_CRM.getsInstance().getSharedPreferences().edit().putString(Const.MESSAGE, message).commit();


            } else if (name.equals("empid")) {


                String message = readempId(parser);
                //Log.e("REGISTER USER OTP " , otp+"");

                data.setMessage(message);


            } else if (name.equals("usertype")) {


                String message = readusertype(parser);
                //Log.e("REGISTER USER OTP " , otp+"");

                data.setMessage(message);


            }
        }
        return data;

    }

    private OTPData parseRegisterDevice(XmlPullParser parser) throws XmlPullParserException, IOException {

        OTPData data = new OTPData();
        parser.require(XmlPullParser.START_TAG, null, "data");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("status")) {


                String status = readStatus(parser);
                //  	Log.e("REGISTER device STATUS " , status+"");

                data.setStatus(status.equals("success") ? true : false);


            } else if (name.equals("otp")) {


                String otp = readOtp(parser);
                //  	Log.e("REGISTER device OTP " , otp+"");

                data.setOtp(otp);

            }
        }
        return data;
    }

    private CheckData parseCheckINOUTResponse(XmlPullParser parser, String id) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "data");
        String latLng = null;
        String custId = null;
        boolean checkInStatus = false;
        String custName = null;

        CheckData data = new CheckData();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if (name.equals("status")) {


                String status = readStatus(parser);
                //	Log.e("VERIFY CHECK IN/OUT" , status+"");
                if (status.equals("success")) {


                    checkInStatus = true;


                } else {

                    checkInStatus = false;

                }


            } else if (name.equals("custname")) {

                custName = readCustName(parser);

            } else if (name.equals("custid")) {

                custId = readCustId(parser);


            } else if (name.equals("latlng")) {

                latLng = readLatLng(parser);

                String latLngArr[] = latLng.split(",");

                //	Log.e("CHECK  STATUS DATA ***********"    ,custName+" "+latLngArr[0] + " " + latLngArr[1] +" "+custId+ " " +id);


                data.setCheckDistKey(custId);
                data.setCheckDistName(custName);
                data.setCheckLat(latLngArr[0]);
                data.setCheckLng(latLngArr[1]);
                data.setCheckStatus(checkInStatus);

	        		/*ContentValues contentValues = new ContentValues();
                    contentValues.put(DB.CHECK_LAT, latLngArr[0]);
	        		contentValues.put(DB.CHECK_LNG, latLngArr[1]);
	        		contentValues.put(DB.DIST_CHECK_KEY,custId );
	        		contentValues.put(DB.DIST_CHECK_NAME, custName);
	        		updateCheckStatus(contentValues,id);*/


            } else {


                skip(parser);
                continue;
            }
        }


        return data;

    }

    private boolean parseVerfiyOTPResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean issuccess = false;
        parser.require(XmlPullParser.START_TAG, null, "data");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if (name.equals("status")) {


                String status = readStatus(parser);
                // 	Log.e("VERIFY OTP" , status+"");
                if (status.equals("success")) {


                    issuccess = true;

                } else {


                    issuccess = false;
                }


            } else if (name.equals("message")) {
                String message = readmessage(parser);
                E_CRM.getsInstance().getSharedPreferences().edit().putString(Const.MESSAGE, message).commit();
            }
        }
        return issuccess;

    }

    private boolean parseVerfiyMobileResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
        boolean issuccess = false;
        parser.require(XmlPullParser.START_TAG, null, "data");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if (name.equals("status")) {
                String status = readStatus(parser);
                Log.e("VERIFY OTP", status + "");
                if (status.equals("success")) {
                    issuccess = true;
                } else {
                    issuccess = false;
                }
            } else if (name.equalsIgnoreCase("usertype")) {
                E_CRM.getsInstance().getSharedPreferences().edit().putString(Const.USERTYPE, readusertype(parser)).commit();
            } else if (name.equalsIgnoreCase("empid")) {
                E_CRM.getsInstance().getSharedPreferences().edit().putString(Const.EMPID, readempId(parser)).commit();
            } else if (name.equalsIgnoreCase("message")) {
                E_CRM.getsInstance().getSharedPreferences().edit().putString(Const.MESSAGE, readmessage(parser)).commit();
            }
        }
        return issuccess;

    }

    private String readLatLng(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "latlng");

        //String title = readText(parser);
        String latlng = null;
        if (parser.next() == XmlPullParser.TEXT) {
            latlng = parser.getText();


            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "latlng");
        return latlng;

    }


    private String readStatus(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "status");

        //String title = readText(parser);
        String status = null;
        if (parser.next() == XmlPullParser.TEXT) {
            status = parser.getText();


            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "status");
        return status;

    }

    private String readOtp(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "otp");

        //String title = readText(parser);
        String otp = null;
        if (parser.next() == XmlPullParser.TEXT) {
            otp = parser.getText();


            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "otp");
        return otp;

    }

    private String readmessage(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "message");

        //String title = readText(parser);
        String message = null;
        if (parser.next() == XmlPullParser.TEXT) {
            message = parser.getText();


            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "message");
        return message;

    }

    private String readempId(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "empid");

        //String title = readText(parser);
        String message = null;
        if (parser.next() == XmlPullParser.TEXT) {
            message = parser.getText();


            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "empid");
        return message;

    }

    private String readusertype(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "usertype");

        //String title = readText(parser);
        String usertype = null;
        if (parser.next() == XmlPullParser.TEXT) {
            usertype = parser.getText();


            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "usertype");
        return usertype;

    }

    private boolean parseDistributerListResponse(XmlPullParser parser) {


        String distId = null;
        String distName = null;
        String distAddr = null;
        StringBuilder distArea = null;
        try {


            parser.require(XmlPullParser.START_TAG, null, "data");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();

                //ContentValues insertValue = new ContentValues();
                //insertValue.put(DB.DIST_NAME , )

                if (name.equals("count")) {

                    String count = readCount(parser);


                    //  	Log.e("COUNT->>>>>>>>>>>>>>", count+"");

                    if (Integer.parseInt(count) <= 0) {


                        return false;

                    }


                } else if (name.equals("custid")) {

                    distId = readCustId(parser);


                } else if (name.equals("custname")) {

                    distName = readCustName(parser);

                } else if (name.equals("addr")) {

                    distAddr = readCustAddr(parser);

			        	/*Log.e("         ","     ");

			        	Log.e("*****","********");
			        	Log.e("DIST NAME " ,distName+"");
			        	Log.e("DIST ID " ,distId+"");
			        	Log.e("DIST ADDR " ,distAddr+"");
			        	Log.e("*****","********");
			        	
			        	Log.e("         ","     ");*/


                    ContentValues insertValue = new ContentValues();


                    String distAddrSplit[] = distAddr.split(",");

                    //Log.e("SIZE" , distAddrSplit.length+"");
                    if (distAddrSplit.length >= 2) {


                        if (distAddrSplit.length < 2)
                            distArea = new StringBuilder(distAddrSplit[0] + "," + distAddrSplit[1] +
                                    "," + distAddrSplit[2]);
                        else {

                            distArea = new StringBuilder(distAddrSplit[0] + "," + distAddrSplit[1]);

                        }

                        insertValue.put(DB.DIST_AREA, distArea.toString());

                    }


                    insertValue.put(DB.DIST_ID, distId);
                    insertValue.put(DB.DIST_NAME, distName);
                    insertValue.put(DB.DIST_ADDRESS, distAddr);


                    addDistributer(insertValue);


                } else {


                    skip(parser);
                    continue;
                }


            }


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block

            //	Log.e("ERROR 1 " , e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //	Log.e("ERROR 2 " , e.getMessage());
            e.printStackTrace();
        }


        return true;


    }


    private String readCustAddr(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "addr");

        //String title = readText(parser);
        String distAddr = null;
        if (parser.next() == XmlPullParser.TEXT) {
            distAddr = parser.getText();

            // Log.d("ADDR " , addr+"");

            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "addr");

        return distAddr;
    }

    private String readCustName(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "custname");

        //
        String distName = null;
        if (parser.next() == XmlPullParser.TEXT) {
            distName = parser.getText();

            // Log.d("custname " , custname+"");

            parser.nextTag();
        }


        parser.require(XmlPullParser.END_TAG, null, "custname");


        return distName;
    }

    private String readCount(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "count");
        //String title = readText(parser);

        String count = null;
        if (parser.next() == XmlPullParser.TEXT) {

            count = parser.getText();

            //   Log.d("CUSTOMER ID " , distId+"");
            parser.nextTag();
        }


        parser.require(XmlPullParser.END_TAG, null, "count");

        return count;
    }

    private String readCustId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "custid");
        //String title = readText(parser);

        String distId = null;
        if (parser.next() == XmlPullParser.TEXT) {

            distId = parser.getText();

            //  Log.d("CUSTOMER ID " , distId+"");
            parser.nextTag();
        }


        parser.require(XmlPullParser.END_TAG, null, "custid");

        return distId;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

        //Log.e("EVENT TYPE" , parser.getEventType()+"");

        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    public void addDistributer(ContentValues insertValue) {

		
		
		
	/*	insertValue.put(DB.DIST_NAME, "PQR Company");
        insertValue.put(DB.DIST_CONTACT_PERSON, "John Caro");
		insertValue.put(DB.DIST_ADDRESS, "55 club road");
		
	*/


        context.getContentResolver().insert(Uri.parse("content://com.symphony_ecrm.database.DBProvider/addDistributer"),

                insertValue);


    }

    public void updateCheckStatus(ContentValues updateValue, String id) {

        int resVal = context.getContentResolver().update(Uri.parse("content://com.symphony_ecrm.database.DBProvider/updateCheckFlagStatus"),

                updateValue, DB.CHECK_ID + " = " + id, null);

        //Log.e("ADD CHECK STATUS" , resVal+"");


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getMasterIP() {


        String ip = prefs.getString("masterIP", null);
        String port = prefs.getString("masterPort", null);

        if (ip != null) {

            HTTP_ENDPOINT = ip;

            if (port != null)
                HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_ENDPOINT + ":" + port;
            else
                HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_ENDPOINT;
        }

        Log.e("Notification ip change ", HTTP_ENDPOINT + "");

    }
}


