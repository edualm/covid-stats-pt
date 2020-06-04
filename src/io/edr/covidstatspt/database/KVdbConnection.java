package io.edr.covidstatspt.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class KVdb implements Database {
    private String baseURL = null;

    KVdb(String baseURL) {
        this.baseURL = baseURL;
    }

    private String getValueForKey(String key) throws IOException {
        StringBuilder result = new StringBuilder();

        URL url = new URL(this.baseURL + key);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

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
        URL url = new URL(this.baseURL + "latest_report_url");

        byte[] out = newReportURL.getBytes();

        URLConnection con = url.openConnection();

        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setFixedLengthStreamingMode(out.length);
        http.connect();

        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
    }
}
