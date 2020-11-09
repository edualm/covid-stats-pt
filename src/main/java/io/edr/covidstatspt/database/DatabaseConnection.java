/*
 *  DatabaseConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

import io.edr.covidstatspt.model.FullReport;
import io.edr.covidstatspt.model.MaxValuesData;

@SuppressWarnings("UnusedReturnValue")
public interface DatabaseConnection {

    FullReport getLastReport();
    String[] getTelegramRecipients();
    MaxValuesData getMaxValuesData();

    boolean setLastReport(FullReport report);
    boolean setTelegramRecipients(String[] recipients);
    boolean setMaxValuesData(MaxValuesData data);
}
