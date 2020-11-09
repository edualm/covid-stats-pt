/*
 *  MaxValuesDataTests.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.edr.covidstatspt.model.FullReport;
import io.edr.covidstatspt.model.MaxValuesData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SerializationTests {

    @Test
    public void testMaxValuesSerialization() throws JsonProcessingException {
        //  Given
        MaxValuesData data = new MaxValuesData(
                new MaxValuesData.DatedValue("11/11/11", 111),
                new MaxValuesData.DatedValue("22/22/22", 222)
        );

        //  When
        String serialized = (new Serializer<>(MaxValuesData.class)).serialize(data);

        //  Then
        assertEquals(
                "{\"cases\":{\"date\":\"11/11/11\",\"value\":111},\"deaths\":{\"date\":\"22/22/22\",\"value\":222}}",
                serialized
        );
    }

    @Test
    public void testMaxValuesDeserialization() throws JsonProcessingException {
        //  Given
        String serialized = "{\"cases\":{\"date\":\"11/11/11\",\"value\":111},\"deaths\":{\"date\":\"22/22/22\",\"value\":222}}";

        //  When
        MaxValuesData data = (new Serializer<>(MaxValuesData.class)).deserialize(serialized);

        //  Then
        assertEquals(
                data,
                new MaxValuesData(
                        new MaxValuesData.DatedValue("11/11/11", 111),
                        new MaxValuesData.DatedValue("22/22/22", 222)
                )
        );
    }

    @Test
    public void testFullReportDeserialization() throws JsonProcessingException {
        //  Given
        String serialized = "{\"name\":\"08/11\",\"metadata\":{\"name\":\"07/11/2020\",\"url\":\"https://covid19.min-saude.pt/wp-content/uploads/2020/11/250_DGS_boletim_20201107.pdf\"},\"countryReport\":{\"day\":{\"cases\":6640,\"deaths\":56,\"active\":2591,\"recoveries\":3993},\"cumulative\":{\"cases\":173540,\"deaths\":2848,\"active\":72945,\"recoveries\":97747}},\"regionReports\":{\"Alentejo\":{\"day\":{\"cases\":49,\"deaths\":3},\"cumulative\":{\"cases\":3361,\"deaths\":63}},\"Açores\":{\"day\":{\"cases\":22,\"deaths\":0},\"cumulative\":{\"cases\":435,\"deaths\":15}},\"Centro\":{\"day\":{\"cases\":712,\"deaths\":3},\"cumulative\":{\"cases\":5757,\"deaths\":352}},\"Algarve\":{\"day\":{\"cases\":82,\"deaths\":0},\"cumulative\":{\"cases\":3367,\"deaths\":29}},\"Madeira\":{\"day\":{\"cases\":19,\"deaths\":0},\"cumulative\":{\"cases\":534,\"deaths\":1}},\"Norte\":{\"day\":{\"cases\":3900,\"deaths\":31},\"cumulative\":{\"cases\":82361,\"deaths\":1279}},\"Lisboa e Vale do Tejo\":{\"day\":{\"cases\":1856,\"deaths\":19},\"cumulative\":{\"cases\":67725,\"deaths\":1109}}}}";

        //  When
        FullReport data = (new Serializer<>(FullReport.class)).deserialize(serialized);

        //  Then
        assertNotNull(data);
    }
}
