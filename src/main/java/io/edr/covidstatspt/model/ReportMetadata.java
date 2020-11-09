/*
 *  ReportMetadata.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.model;

import java.net.URL;

public final class ReportMetadata {

    private final String name;
    private final URL url;

    @SuppressWarnings("unused")
    public ReportMetadata() {
        this.name = null;
        this.url = null;
    }

    public ReportMetadata(String name, URL url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public URL getURL() {
        return url;
    }
}
