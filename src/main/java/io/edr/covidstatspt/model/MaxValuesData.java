/*
 *  MaxValuesData.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.model;

public class MaxValuesData {

    public static class DatedValue {

        public final String date;
        public final int value;

        @SuppressWarnings("unused")
        public DatedValue() {
            this.date = "";
            this.value = 0;
        }

        public DatedValue(String date, int value) {
            this.date = date;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DatedValue))
                return false;

            DatedValue d = (DatedValue) obj;

            return (date.equals(d.date) &&
                    value == d.value);
        }

        @Override
        public String toString() {
            return "Date: " + date + ", Value: " + value;
        }
    }

    public DatedValue cases;
    public DatedValue deaths;

    @SuppressWarnings("unused")
    public MaxValuesData() {
        this.cases = null;
        this.deaths = null;
    }

    public MaxValuesData(DatedValue cases, DatedValue deaths) {
        this.cases = cases;
        this.deaths = deaths;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MaxValuesData))
            return false;

        MaxValuesData d = (MaxValuesData) obj;

        return (cases.equals(d.cases) &&
                deaths.equals(d.deaths));
    }

    @Override
    public String toString() {
        return "Cases: [" + cases.toString() + "], Deaths: [" + deaths.toString() + "]";
    }
}
