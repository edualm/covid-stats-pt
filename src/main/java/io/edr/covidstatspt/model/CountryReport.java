/*
 *  CountryReport.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.model;

public class CountryReport {

    public static class Report {
        public int cases;
        public int deaths;

        public int active;
        public int recoveries;

        public Report(int cases, int deaths, int active, int recoveries) {
            this.cases = cases;
            this.deaths = deaths;

            this.active = active;
            this.recoveries = recoveries;
        }
    }

    public CountryReport.Report day;
    public CountryReport.Report cumulative;

    public CountryReport(Report day, Report cumulative) {
        this.day = day;
        this.cumulative = cumulative;
    }
}
