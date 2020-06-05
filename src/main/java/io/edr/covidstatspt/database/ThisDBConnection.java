/*
 *  ThisDBDatabase.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

import java.net.HttpURLConnection;

public class ThisDBConnection extends WebKVConnection {

    private static String ThisDBEndpoint = "https://api.thisdb.com/v1/";

    private String apiKey = null;
    private String bucketId = null;

    public ThisDBConnection(String apiKey, String bucketID) {
        this.apiKey = apiKey;
        this.bucketId = bucketID;
    }

    String getURLForKey(String key) {
        return ThisDBEndpoint + bucketId + "/" + key;
    }

    @Override
    void setHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("X-Api-Key", apiKey);
    }
}
