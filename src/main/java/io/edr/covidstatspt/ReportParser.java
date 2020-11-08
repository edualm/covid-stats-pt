/*
 *  ReportParser.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;

import java.util.Map;

public interface ReportParser {

    @SuppressWarnings("SameReturnValue")
    String[] getOrderedRegions();

    Map<String, RegionReport> getRegionReports();
    CountryReport getCountryReport();
}
