/*
 *  MessagingConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.MisconfigurationException;

import java.io.IOException;

public interface MessagingConnection {

    void broadcast(String message) throws IOException;
    void send(String recipient, String message, boolean html) throws IOException;
    void send(String recipient, String message, boolean html, boolean silent) throws IOException;
    void sendToAdmin(String message, boolean html, boolean silent) throws MisconfigurationException, IOException;
}
