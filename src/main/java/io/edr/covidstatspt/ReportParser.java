/*
 *  ReportParser.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.ParseFailureException;
import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;

import java.io.IOException;
import java.util.Map;

public interface ReportParser {

    String[] getOrderedRegions();

    Map<String, RegionReport> getRegionReports() throws IOException, ParseFailureException;
    CountryReport getCountryReport() throws IOException, ParseFailureException;
}
