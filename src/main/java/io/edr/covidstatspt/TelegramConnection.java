/*
 *  Telegram.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.DatabaseConnection;
import io.edr.covidstatspt.exceptions.MisconfigurationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramConnection {

    private final DatabaseConnection databaseConnection;
    private final String token;

    TelegramConnection(DatabaseConnection databaseConnection, String token) {
        this.databaseConnection = databaseConnection;
        this.token = token;
    }

    public void broadcast(String message) throws IOException {
        for (String recipient: databaseConnection.getTelegramRecipients()) {
            send(recipient, message, true);
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

    public void sendToAdmin(String message, boolean html) throws MisconfigurationException, IOException {
        //  The Telegram ID of the admin is assumed to be the first ID on the recipients array.

        if (databaseConnection.getTelegramRecipients().length == 0)
            throw new MisconfigurationException("No telegram recipients set while the server tried to send a message to an admin!");

        send(databaseConnection.getTelegramRecipients()[0], message, html);
    }

}
