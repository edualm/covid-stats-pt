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

    private static final String ThisDBEndpoint = "https://api.thisdb.com/v1/";

    private final String apiKey;
    private final String bucketId;

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
