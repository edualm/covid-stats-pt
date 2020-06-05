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
    String getLastReportURL();
    String[] getTelegramRecipients();

    boolean setCachedResponse(String cachedResponse);
    boolean setLastReportURL(String newReportURL);
    boolean setTelegramRecipients(String[] recipients);
}
