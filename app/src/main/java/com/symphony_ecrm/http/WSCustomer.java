package com.symphony_ecrm.http;


import com.symphony_ecrm.model.CustomerListModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by indianic on 25/06/16.
 */
public class WSCustomer {

    public WSCustomer() {
    }

    public ArrayList<CustomerListModel> executeCustomerLst(String murl) {
        URL url = null;
        try {
            url = new URL(murl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream stream = conn.getInputStream();
            return getcustomerList(stream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<CustomerListModel> getcustomerList(InputStream stream) {

        ArrayList<CustomerListModel> townlist = new ArrayList<>();
        CustomerListModel customerListModel = null;
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
                        if (tagname.equalsIgnoreCase("custid")) {
                            // create a new instance of employee
                            customerListModel = new CustomerListModel();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("custid")) {
                            // add employee object to list
                            customerListModel.setId(text);
                            townlist.add(customerListModel);
                        } else if (tagname.equalsIgnoreCase("custname")) {
                            customerListModel.setCustomername(text);
                        } else if (tagname.equalsIgnoreCase("address")) {
                            customerListModel.setAddress(text);
                        } else if (tagname.equalsIgnoreCase("contact")) {
                            customerListModel.setContact(text);
                        } else if (tagname.equalsIgnoreCase("Town")) {
                            customerListModel.setTown(text);
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

        return townlist;
    }

}
