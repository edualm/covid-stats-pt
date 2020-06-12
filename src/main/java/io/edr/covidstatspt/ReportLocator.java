/*
 *  ReportLocator.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import java.io.IOException;
import java.util.ArrayList;

public interface ReportLocator {

    ArrayList<ReportMetadata> getReports() throws IOException;
}
