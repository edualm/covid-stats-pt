/*
 *  PortugueseReportLocatorTests.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.ParseFailureException;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;

public class PortugueseReportLocatorTests {

    @Test
    public void testLocateReport() throws IOException {
        PortugueseReportLocator locator = new PortugueseReportLocator();

        locator.getReport();
    }

    @Test
    public void testExpectedReportNameComponent() {
        PortugueseReportLocator locator = new PortugueseReportLocator();

        assertEquals("03032025", locator.getExpectedTodayReportNameComponent(Date.from(Instant.parse("2025-03-03T10:00:00Z"))));
    }
}
