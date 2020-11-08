/*
 *  MaxValuesData.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MaxValuesData {

    public static class DatedValue {

        public String date;
        public int value;

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

    public String serialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static MaxValuesData deserialize(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, MaxValuesData.class);
    }
}
