/*
 *  DatabaseUtilities.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.DatabaseConnection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseUtilities {

    private final DatabaseConnection databaseConnection;

    public DatabaseUtilities(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void addTelegramRecipient(String recipient) {
        String[] recipients = databaseConnection.getTelegramRecipients();

        recipients = Arrays.copyOf(recipients, recipients.length + 1);
        recipients[recipients.length - 1] = recipient;

        List<String> newRecipientsAsList = Arrays
                .stream(recipients)
                .filter(value -> !value.equals(""))
                .collect(Collectors.toList());

        String[] newRecipients = new String[newRecipientsAsList.size()];
        newRecipients = newRecipientsAsList.toArray(newRecipients);

        databaseConnection.setTelegramRecipients(newRecipients);
    }

    public void removeTelegramRecipient(String recipient) {
        List<String> newRecipientsAsList = Arrays
                .stream(databaseConnection.getTelegramRecipients())
                .filter(value -> !value.equals(recipient))
                .collect(Collectors.toList());

        String[] newRecipients = new String[newRecipientsAsList.size()];
        newRecipients = newRecipientsAsList.toArray(newRecipients);

        databaseConnection.setTelegramRecipients(newRecipients);
    }
}
