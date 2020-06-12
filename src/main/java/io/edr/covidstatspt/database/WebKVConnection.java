/*
 *  WebKVConnection.java
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

public abstract class WebKVConnection implements DatabaseConnection {

    abstract String getURLForKey(String key);
    abstract void setHeaders(HttpURLConnection connection);

    private String getValueForKey(String key) throws IOException {
        StringBuilder result = new StringBuilder();

        URL url = new URL(getURLForKey(key));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        setHeaders(conn);

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;

        while ((line = rd.readLine()) != null)
            result.append(line);

        rd.close();

        return result.toString();
    }

    private void setValueForKey(String key, String value) throws IOException {
        URL url = new URL(getURLForKey(key));

        byte[] out = value.getBytes();

        URLConnection con = url.openConnection();

        HttpURLConnection http = (HttpURLConnection)con;
        setHeaders(http);

        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setFixedLengthStreamingMode(out.length);
        http.connect();

        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
    }

    @Override
    public String getCachedResponse() {
        try {
            return getValueForKey("cached_response");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getLastReportName() {
        try {
            return getValueForKey("latest_report_name");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String[] getTelegramRecipients() {
        try {
            return getValueForKey("recipients").split(",");
        } catch (Exception e) {
            return new String[]{};
        }
    }

    @Override
    public boolean setCachedResponse(String cachedResponse) {
        try {
            setValueForKey("cached_response", cachedResponse);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setLastReportName(String newReportName) {
        try {
            setValueForKey("latest_report_name", newReportName);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setTelegramRecipients(String[] recipients) {
        try {
            setValueForKey("recipients", String.join(",", recipients));

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
