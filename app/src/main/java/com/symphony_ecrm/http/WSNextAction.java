package com.symphony_ecrm.http;

import com.symphony_ecrm.model.NextActionModel;

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
public class WSNextAction {

    public WSNextAction() {
    }

    public ArrayList<NextActionModel> executeNextActionList(String murl) {
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
            return getNextActionList(stream);
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

    public ArrayList<NextActionModel> getNextActionList(InputStream stream) {

        ArrayList<NextActionModel> nextactionlist = new ArrayList<>();
        NextActionModel nextActionModel = null;
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
                        if (tagname.equalsIgnoreCase("ActivityTypeid")) {
                            // create a new instance of employee
                            nextActionModel = new NextActionModel();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("ActivityTypeid")) {
                            // add employee object to list
                            nextActionModel.setTypeId(text);
                            nextactionlist.add(nextActionModel);
                        } else if (tagname.equalsIgnoreCase("ActivityType")) {
                            nextActionModel.setType(text);
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

        return nextactionlist;
    }
}
