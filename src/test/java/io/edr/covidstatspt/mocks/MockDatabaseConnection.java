/*
 *  MockDatabaseConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.mocks;

import io.edr.covidstatspt.database.DatabaseConnection;
import io.edr.covidstatspt.model.FullReport;
import io.edr.covidstatspt.model.MaxValuesData;

public class MockDatabaseConnection implements DatabaseConnection {

    private MaxValuesData maxValuesData;

    public MockDatabaseConnection() {
        maxValuesData = new MaxValuesData(
                new MaxValuesData.DatedValue("---", 5000),
                new MaxValuesData.DatedValue("---", 50)
        );
    }

    @Override
    public FullReport getLastReport() {
        return null;
    }

    @Override
    public String[] getTelegramRecipients() {
        return new String[0];
    }

    @Override
    public MaxValuesData getMaxValuesData() {
        if (maxValuesData == null)
            return null;

        return new MaxValuesData(
                new MaxValuesData.DatedValue(maxValuesData.cases.date, maxValuesData.cases.value),
                new MaxValuesData.DatedValue(maxValuesData.deaths.date, maxValuesData.deaths.value)
        );
    }

    @Override
    public boolean setLastReport(FullReport report) {
        return false;
    }

    @Override
    public boolean setTelegramRecipients(String[] recipients) {
        return false;
    }

    @Override
    public boolean setMaxValuesData(MaxValuesData data) {
        maxValuesData = data;

        return true;
    }
}
