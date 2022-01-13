/*
 *  ReportLocator.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.ParseFailureException;
import io.edr.covidstatspt.model.ReportMetadata;

import java.io.IOException;

public interface ReportLocator {

    String getExpectedTodayReportNameComponent();
    ReportMetadata getReport() throws IOException, ParseFailureException;
}
