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

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RegionReport.Report))
                return false;

            RegionReport.Report r = (RegionReport.Report) obj;

            return (cases == r.cases && deaths == r.deaths);
        }

        @Override
        public String toString() {
            return "Cases: " + cases + ", Deaths: " + deaths;
        }
    }

    public Report day;
    public Report cumulative;

    public RegionReport(Report day, Report cumulative) {
        this.day = day;
        this.cumulative = cumulative;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegionReport))
            return false;

        RegionReport cr = (RegionReport) obj;

        return (day.equals(cr.day) && cumulative.equals(cr.cumulative));
    }

    @Override
    public String toString() {
        return "Day: <" + day.toString() + ">, Cumulative: <" + cumulative.toString() + ">";
    }
}
