package io.edr.covidstatspt;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.edr.covidstatspt.model.MaxValuesData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaxValuesDataTests {

    @Test
    public void testSerialization() throws JsonProcessingException {
        //  Given
        MaxValuesData data = new MaxValuesData(
                new MaxValuesData.DatedValue("11/11/11", 111),
                new MaxValuesData.DatedValue("22/22/22", 222)
        );

        //  When
        String serialized = data.serialize();

        //  Then
        assertEquals(
                "{\"cases\":{\"date\":\"11/11/11\",\"value\":111},\"deaths\":{\"date\":\"22/22/22\",\"value\":222}}",
                serialized
        );
    }

    @Test
    public void testDeserialization() throws JsonProcessingException {
        //  Given
        String serialized = "{\"cases\":{\"date\":\"11/11/11\",\"value\":111},\"deaths\":{\"date\":\"22/22/22\",\"value\":222}}";

        //  When
        MaxValuesData data = MaxValuesData.deserialize(serialized);

        //  Then
        assertEquals(
                data,
                new MaxValuesData(
                        new MaxValuesData.DatedValue("11/11/11", 111),
                        new MaxValuesData.DatedValue("22/22/22", 222)
                )
        );
    }
}
