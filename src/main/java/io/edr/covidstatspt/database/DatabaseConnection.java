/*
 *  DatabaseConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

import io.edr.covidstatspt.model.MaxValuesData;

@SuppressWarnings("UnusedReturnValue")
public interface DatabaseConnection {

    String getCachedResponse();
    String getLastReportName();
    String[] getTelegramRecipients();
    MaxValuesData getMaxValuesData();

    boolean setCachedResponse(String cachedResponse);
    boolean setLastReportName(String newReportName);
    boolean setTelegramRecipients(String[] recipients);
    boolean setMaxValuesData(MaxValuesData data);
}
