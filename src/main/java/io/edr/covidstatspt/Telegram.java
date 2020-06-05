/*
 *  Telegram.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Telegram {

    private Database database = null;
    private String token = null;

    Telegram(Database database, String token) {
        this.database = database;
        this.token = token;
    }

    public void broadcast(String message) throws IOException {
        for (String recipient: database.getTelegramRecipients()) {
            URL url = new URL("https://api.telegram.org/bot" +
                    this.token +
                    "/sendMessage?parse_mode=HTML&chat_id=" +
                    recipient +
                    "&text=" +
                    URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            rd.close();
        }
    }

    public void send(String recipient, String message, boolean html) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" +
                this.token +
                "/sendMessage?chat_id=" +
                recipient +
                (html ? "&parse_mode=HTML" : "") +
                "&text=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        rd.close();
    }

}
