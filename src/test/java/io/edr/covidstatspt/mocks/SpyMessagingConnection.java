/*
 *  SpyMessagingConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.mocks;

import io.edr.covidstatspt.MessagingConnection;

import java.util.ArrayList;

public class SpyMessagingConnection implements MessagingConnection {

    public final ArrayList<String> messages;

    public SpyMessagingConnection() {
        messages = new ArrayList<>();
    }

    @Override
    public void broadcast(String message) {
        messages.add(message);
    }

    @Override
    public void send(String recipient, String message, boolean html, boolean silent) {}

    @Override
    public void send(String recipient, String message, boolean html) {}

    @Override
    public void sendToAdmin(String message, boolean html, boolean silent) {}
}
