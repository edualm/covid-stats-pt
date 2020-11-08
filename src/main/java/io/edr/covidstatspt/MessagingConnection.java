package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.MisconfigurationException;

import java.io.IOException;

public interface MessagingConnection {

    void broadcast(String message) throws IOException;
    void send(String recipient, String message, boolean html) throws IOException;
    void sendToAdmin(String message, boolean html) throws MisconfigurationException, IOException;
}
