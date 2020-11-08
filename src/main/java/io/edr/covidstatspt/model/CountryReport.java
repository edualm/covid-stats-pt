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

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Report))
                return false;

            Report r = (Report) obj;

            return (cases == r.cases &&
                    deaths == r.deaths &&
                    active == r.active &&
                    recoveries == r.recoveries);
        }

        @Override
        public String toString() {
            return "Cases: " + cases + ", Deaths: " + deaths + ", Active: " + active + ", Recoveries: " + recoveries;
        }
    }

    public CountryReport.Report day;
    public CountryReport.Report cumulative;

    public CountryReport(Report day, Report cumulative) {
        this.day = day;
        this.cumulative = cumulative;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CountryReport))
            return false;

        CountryReport cr = (CountryReport) obj;

        return (day.equals(cr.day) && cumulative.equals(cr.cumulative));
    }

    @Override
    public String toString() {
        return "Day: <" + day.toString() + ">, Cumulative: <" + cumulative.toString() + ">";
    }
}
