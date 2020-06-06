/*
 *  Database.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

public interface Database {

    String getCachedResponse();
    String getLastReportName();
    String[] getTelegramRecipients();

    boolean setCachedResponse(String cachedResponse);
    boolean setLastReportName(String newReportName);
    boolean setTelegramRecipients(String[] recipients);
}
