/*
 *  KVdbConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

import java.net.HttpURLConnection;

public class KVdbConnection extends WebKVConnection {

    private String baseURL = null;

    public KVdbConnection(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    String getURLForKey(String key) {
        return this.baseURL + key;
    }

    @Override
    void setHeaders(HttpURLConnection connection) {
        //  Do nothing.
    }
}
