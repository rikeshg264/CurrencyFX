package com.example.gurung_rikesh_s2426621;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RssFetcherParser {

    private static final String RSS_URL = "https://www.fx-exchange.com/gbp/rss.xml";

    /** -------------------------------
     *  PUBLIC METHOD: Fetch + Parse
     * ------------------------------- */
    public List<CurrencyRate> fetchAndParse() {
        try {
            String xml = downloadRss();
            if (xml == null) {
                return null;
            }
            return parseRss(xml);
        } catch (Exception e) {
            Log.e("RssFetcherParser", "Error: " + e.getMessage());
            return null;
        }
    }

    /** -------------------------------
     *  DOWNLOAD RSS XML AS STRING
     * ------------------------------- */
    private String downloadRss() {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(RSS_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(8000);
            urlConnection.setReadTimeout(8000);
            urlConnection.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StringBuilder builder = new StringBuilder();

            int byteData;
            while ((byteData = in.read()) != -1) {
                builder.append((char) byteData);
            }

            return builder.toString();

        } catch (Exception e) {
            Log.e("RssFetcherParser", "Download error: " + e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
    }

    /** -------------------------------
     *  FIXED: PULLPARSER LOGIC
     * ------------------------------- */
    private List<CurrencyRate> parseRss(String xmlData) {

        List<CurrencyRate> list = new ArrayList<>();
        CurrencyRate currentRate = null;

        String text = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = parser.getName();

                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        text = "";   // ❶ RESET TEXT for new tag
                        if ("item".equalsIgnoreCase(tagName)) {
                            currentRate = new CurrencyRate();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if (currentRate != null) {

                            if ("title".equalsIgnoreCase(tagName)) {
                                currentRate.setTitle(text.trim());
                            }

                            else if ("description".equalsIgnoreCase(tagName)) {

                                // Clean HTML
                                String clean = text.replaceAll("<.*?>", "").trim();

                                currentRate.setDescription(clean);

                                // Extract number from cleaned text
                                currentRate.setRate(extractRate(clean));
                            }

                            else if ("pubDate".equalsIgnoreCase(tagName)) {
                                currentRate.setPubDate(text.trim());
                            }

                            else if ("item".equalsIgnoreCase(tagName)) {
                                list.add(currentRate);
                            }
                        }
                        break;
                }

                eventType = parser.next();
            }

        } catch (Exception e) {
            Log.e("RssFetcherParser", "Parsing error: " + e.getMessage());
        }

        return list;
    }


    /** --------------------------------------------
     * Helper method to extract numeric rate
     * -------------------------------------------- */
    private double extractRate(String description) {
        try {
            // Example: "1 GBP = 1.25 USD"
            String[] parts = description.split("=");
            if (parts.length > 1) {
                return Double.parseDouble(parts[1].replaceAll("[^0-9.]", ""));
            }
        } catch (Exception e) {
            return 0.0;
        }
        return 0.0;
    }
}
