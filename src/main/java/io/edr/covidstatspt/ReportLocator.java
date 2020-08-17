/*
 *  ReportLocator.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import java.io.IOException;

public interface ReportLocator {

    public ReportMetadata getReport() throws IOException;
}
