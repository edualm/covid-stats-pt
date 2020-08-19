/*
 *  RegionReport.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.model;

public class RegionReport {

    public static class Report {
        public int cases;
        public int deaths;

        public Report(int cases, int deaths) {
            this.cases = cases;
            this.deaths = deaths;
        }
    }

    public Report day;
    public Report cumulative;

    public RegionReport(Report day, Report cumulative) {
        this.day = day;
        this.cumulative = cumulative;
    }
}
