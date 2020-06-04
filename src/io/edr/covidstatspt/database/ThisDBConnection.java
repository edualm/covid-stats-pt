/*
 *  ThisDBDatabase.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ThisDBConnection implements Database {

    private static String ThisDBEndpoint = "https://api.thisdb.com/v1/";

    private String apiKey = null;
    private String bucketId = null;

    public ThisDBConnection(String apiKey, String bucketID) {
        this.apiKey = apiKey;
        this.bucketId = bucketID;
    }

    private String getURLForKey(String key) {
        return ThisDBEndpoint + bucketId + "/" + key;
    }

    private String getValueForKey(String key) throws IOException {
        StringBuilder result = new StringBuilder();

        URL url = new URL(getURLForKey(key));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Api-Key", apiKey);

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;

        while ((line = rd.readLine()) != null)
            result.append(line);

        rd.close();

        return result.toString();
    }

    public String getLastReportURL() throws IOException {
        return getValueForKey("latest_report_url");
    }

    public String[] getTelegramRecipients() throws IOException {
        return getValueForKey("recipients").split(",");
    }

    public void updateLastReportURL(String newReportURL) throws IOException {
        URL url = new URL(getURLForKey("latest_report_url"));

        byte[] out = newReportURL.getBytes();

        URLConnection con = url.openConnection();

        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setRequestProperty("X-Api-Key", apiKey);
        http.setDoOutput(true);
        http.setFixedLengthStreamingMode(out.length);
        http.connect();

        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
    }
}
