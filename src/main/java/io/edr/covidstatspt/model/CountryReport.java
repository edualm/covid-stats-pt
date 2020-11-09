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

        public final int cases;
        public final int deaths;

        public final int active;
        public final int recoveries;

        @SuppressWarnings("unused")
        public Report() {
            this.cases = 0;
            this.deaths = 0;

            this.active = 0;
            this.recoveries = 0;
        }

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

    public final CountryReport.Report day;
    public final CountryReport.Report cumulative;

    @SuppressWarnings("unused")
    public CountryReport() {
        this.day = new CountryReport.Report();
        this.cumulative = new CountryReport.Report();
    }

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
