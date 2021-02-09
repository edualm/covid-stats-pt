/*
 *  DatabaseUtilitiesTests.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.DatabaseConnection;
import io.edr.covidstatspt.model.FullReport;
import io.edr.covidstatspt.model.MaxValuesData;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class DatabaseUtilitiesTests {

    private static class MockDatabaseConnection implements DatabaseConnection {

        private String[] telegramRecipients;

        MockDatabaseConnection() {

        }

        @Override
        public FullReport getLastReport() {
            return null;
        }

        @Override
        public String[] getTelegramRecipients() {
            return telegramRecipients;
        }

        @Override
        public MaxValuesData getMaxValuesData() {
            return null;
        }

        @Override
        public boolean setLastReport(FullReport report) {
            return false;
        }

        @Override
        public boolean setTelegramRecipients(String[] recipients) {
            telegramRecipients = recipients;

            return true;
        }

        @Override
        public boolean setMaxValuesData(MaxValuesData data) {
            return false;
        }
    }

    @Test
    public void testAddTelegramRecipients() {
        //  Given
        MockDatabaseConnection sut = new MockDatabaseConnection();
        DatabaseUtilities databaseUtilities = new DatabaseUtilities(sut);

        sut.setTelegramRecipients(new String[]{"123"});

        //  When
        databaseUtilities.addTelegramRecipient("456");

        //  Then
        assertArrayEquals(sut.getTelegramRecipients(), new String[]{"123", "456"});
    }

    @Test
    public void testRemoveTelegramRecipients() {
        //  Given
        MockDatabaseConnection sut = new MockDatabaseConnection();
        DatabaseUtilities databaseUtilities = new DatabaseUtilities(sut);

        sut.setTelegramRecipients(new String[]{"123"});

        //  When
        databaseUtilities.removeTelegramRecipient("123");

        //  Then
        assertArrayEquals(sut.getTelegramRecipients(), new String[]{});
    }

    @Test
    public void testRemoveNonexistentTelegramRecipient() {
        //  Given
        MockDatabaseConnection sut = new MockDatabaseConnection();
        DatabaseUtilities databaseUtilities = new DatabaseUtilities(sut);

        sut.setTelegramRecipients(new String[]{});

        //  When
        databaseUtilities.removeTelegramRecipient("123");

        //  Then
        assertArrayEquals(sut.getTelegramRecipients(), new String[]{});
    }
}
