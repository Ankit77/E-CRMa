package com.symphony_ecrm.http;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.symphony_ecrm.utils.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by indianic on 28/06/16.
 */
public class WSVisit {

    private HttpEntity httpEntity;

    public boolean executeAddCustomer(String murl, String mobileNumber, String empid, String customerid, String location, String discussion, String puposeid, String nextactionid, String checkinimage, String checkinLat, String checkinLong, String checkinTime, String checkoutimage, String checkoutLat, String checkoutLong, String checkoutTime, String dateofnextaction, String contactPerson, String version, String referencevisitid) {

        location = URLEncoder.encode(location);
        discussion = URLEncoder.encode(discussion);
        contactPerson = URLEncoder.encode(contactPerson);

        Date checkIn = Util.changeFromStringtoDate(checkinTime);
        checkinTime = Util.changeFromDatetoString(checkIn);

        Date checkout = Util.changeFromStringtoDate(checkoutTime);
        checkoutTime = Util.changeFromDatetoString(checkout);

        if (!TextUtils.isEmpty(dateofnextaction)) {
            Date nextAction = Util.changeFromStringtoDate(dateofnextaction);
            dateofnextaction = Util.changeFromDatetoString(nextAction);
        } else {
            dateofnextaction = Util.getCurrentDate();
        }
        try {

            Long uniqeid = Util.getDateStringtoMilies(checkinTime, "dd/MM/yyyy_HH:mm:ss");
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            murl = murl + "/CACS_CHECKIN_CHECKOUT_Google_upload.asp";
            HttpPost httpPost = new HttpPost(murl);
            httpPost.setHeader(HTTP.CONTENT_TYPE,
                    "application/x-www-form-urlencoded;charset=UTF-8");

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("user", "track_new"));
            nameValuePair.add(new BasicNameValuePair("pass", "track123"));
            nameValuePair.add(new BasicNameValuePair("MNO", mobileNumber));
            nameValuePair.add(new BasicNameValuePair("EMPID", empid));
            nameValuePair.add(new BasicNameValuePair("customer_id", customerid));
            nameValuePair.add(new BasicNameValuePair("Location", location));
            nameValuePair.add(new BasicNameValuePair("Discussion", discussion));
            nameValuePair.add(new BasicNameValuePair("Purpose_of_visitid", puposeid));
            nameValuePair.add(new BasicNameValuePair("NextActionID", nextactionid));
            nameValuePair.add(new BasicNameValuePair("CHECKIN_IMAGE", checkinimage));
            nameValuePair.add(new BasicNameValuePair("CHEKCIN_Lat", checkinLat));
            nameValuePair.add(new BasicNameValuePair("CHEKCIN_Long", checkinLong));
            nameValuePair.add(new BasicNameValuePair("CHECKIN_Timestamp", checkinTime));
            nameValuePair.add(new BasicNameValuePair("CHECKOUT_IMAGE", checkoutimage));
            nameValuePair.add(new BasicNameValuePair("CHEKCOUT_Lat", checkoutLat));
            nameValuePair.add(new BasicNameValuePair("CHEKCOUT_Long", checkoutLong));
            nameValuePair.add(new BasicNameValuePair("CHEKCOUT_Timestamp", checkoutTime));
            nameValuePair.add(new BasicNameValuePair("dateofnextaction", dateofnextaction));
            nameValuePair.add(new BasicNameValuePair("ContactPerson", contactPerson));
            nameValuePair.add(new BasicNameValuePair("Version", version));
            nameValuePair.add(new BasicNameValuePair("Unique_VisitID", String.valueOf(uniqeid)));
            if (!TextUtils.isEmpty(referencevisitid)) {
                nameValuePair.add(new BasicNameValuePair("REFRENCE_VISITID", referencevisitid));
            }

            //Encoding POST data
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            //making POST request.
            HttpResponse response = httpClient.execute(httpPost);
            httpEntity = response.getEntity();
            String responseXml = EntityUtils.toString(httpEntity);
            InputStream stream = new ByteArrayInputStream(responseXml.toString().getBytes());
            return parseVisit(stream);

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return false;
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            return false;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }


    public boolean parseVisit(InputStream stream) {

        boolean isSuccess = false;
        String text = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(stream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("sucess")) {
                            // add employee object to list
                            if (text.equalsIgnoreCase("true")) {
                                isSuccess = true;
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isSuccess;
    }
}
