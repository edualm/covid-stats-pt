/*
 *  PortugueseReportParserTests.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.ParseFailureException;
import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;
import io.edr.covidstatspt.model.ReportMetadata;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PortugueseReportParserTests {

    @Test
    public void testCountryDataIsCorrectlyAcquired() throws IOException {
        PDDocument doc = PDDocument.load(new URL("http://arm.robotlike.cloud/covid-test-data/202_DGS_boletim_20200920.pdf").openStream());
        PortugueseReportParser parser = new PortugueseReportParser(doc);

        CountryReport countryReport = parser.getCountryReport();

        CountryReport expectedReport = new CountryReport(
                new CountryReport.Report(552, 13, 347, 192),
                new CountryReport.Report(68577, 1912, 21069, 45596)
        );

        assertEquals(expectedReport, countryReport);
    }

    @Test
    public void testRegionDataIsCorrectlyAcquired() throws IOException {
        PDDocument doc = PDDocument.load(new URL("http://arm.robotlike.cloud/covid-test-data/202_DGS_boletim_20200920.pdf").openStream());
        PortugueseReportParser parser = new PortugueseReportParser(doc);

        Map<String, RegionReport> regionReports = parser.getRegionReports();

        Map<String, RegionReport> expectedReports = new HashMap<>();

        expectedReports.put("Norte", new RegionReport(
                new RegionReport.Report(273, 3),
                new RegionReport.Report(24795, 871)
        ));

        expectedReports.put("Centro", new RegionReport(
                new RegionReport.Report(29, 0),
                new RegionReport.Report(5621, 256)
        ));

        expectedReports.put("Lisboa e Vale do Tejo", new RegionReport(
                new RegionReport.Report(179, 10),
                new RegionReport.Report(35004, 728)
        ));

        expectedReports.put("Alentejo", new RegionReport(
                new RegionReport.Report(35, 0),
                new RegionReport.Report(1318, 23)
        ));

        expectedReports.put("Algarve", new RegionReport(
                new RegionReport.Report(33, 0),
                new RegionReport.Report(1392, 19)
        ));

        expectedReports.put("Açores", new RegionReport(
                new RegionReport.Report(2, 0),
                new RegionReport.Report(243, 15)
        ));

        expectedReports.put("Madeira", new RegionReport(
                new RegionReport.Report(1, 0),
                new RegionReport.Report(204, 0)
        ));

        assertEquals(expectedReports, regionReports);
    }

    @Test
    public void testMoreThanAMillionCumulativeCases() throws IOException {
        PDDocument doc = PDDocument.load(new URL("http://arm.robotlike.cloud/covid-test-data/12012022.pdf").openStream());
        PortugueseReportParser parser = new PortugueseReportParser(doc);

        CountryReport countryReport = parser.getCountryReport();

        assertEquals(40945, countryReport.day.cases);
        assertEquals(1734343, countryReport.cumulative.cases);
    }
}