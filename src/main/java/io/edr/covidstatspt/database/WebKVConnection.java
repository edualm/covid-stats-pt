/*
 *  WebKVConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

import io.edr.covidstatspt.Serializer;
import io.edr.covidstatspt.model.FullReport;
import io.edr.covidstatspt.model.MaxValuesData;

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
    public FullReport getLastReport() {
        try {
            return (new Serializer<>(FullReport.class))
                    .deserialize(getValueForKey("last_report"));
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
    public MaxValuesData getMaxValuesData() {
        try {
            return new Serializer<>(MaxValuesData.class)
                    .deserialize(getValueForKey("max_values_data"));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean setLastReport(FullReport data) {
        try {
            setValueForKey("last_report", new Serializer<>(FullReport.class).serialize(data));

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

    @Override
    public boolean setMaxValuesData(MaxValuesData data) {
        try {
            setValueForKey("max_values_data", new Serializer<>(MaxValuesData.class).serialize(data));

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
