package io.edr.covidstatspt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Database {

    private String baseURL = null;

    Database(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getLastReportURL() throws IOException {
        StringBuilder result = new StringBuilder();

        URL url = new URL(this.baseURL + "latest_report_url");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;

        while ((line = rd.readLine()) != null)
            result.append(line);

        rd.close();

        return result.toString();
    }

    public String[] getTelegramRecipients() throws IOException {
        StringBuilder result = new StringBuilder();

        URL url = new URL(this.baseURL + "recipients");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;

        while ((line = rd.readLine()) != null)
            result.append(line);

        rd.close();

        return result.toString().split(",");
    }

    public void updateLastReportURL(String newReportURL) throws IOException {
        URL url = new URL(this.baseURL + "latest_report_url");

        URLConnection con = url.openConnection();

        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        byte[] out = newReportURL.getBytes();

        http.setFixedLengthStreamingMode(out.length);
        http.connect();

        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
    }
}
