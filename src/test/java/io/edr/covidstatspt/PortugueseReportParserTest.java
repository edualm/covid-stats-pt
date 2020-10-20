/*
 *  PortugueseReportParserTest.java
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PortugueseReportParserTest {

    static ArrayList<ReportMetadata> reports;

    @BeforeClass
    public static void setUp() throws IOException, ParseFailureException {
        PortugueseReportLocator locator = new PortugueseReportLocator();

        //  Currently set to `1` as the report format changed recently,
        //  and it would break if this was >1 for now.

        reports = locator.getReports(1);
    }

    @Test
    public void testGetCasesAndDeaths() throws IOException, ParseFailureException {
        //  We are just asserting that an exception isn't thrown, so this is enough.

        for (ReportMetadata report: reports) {
            PDDocument doc = PDDocument.load(report.getURL().openStream());
            PortugueseReportParser parser = new PortugueseReportParser(doc);

            parser.getRegionReports();

            doc.close();
        }
    }

    @Test
    public void testGetTableData() throws IOException, ParseFailureException {
        //  We are just asserting that an exception isn't thrown, so this is enough.

        for (ReportMetadata report: reports) {
            PDDocument doc = PDDocument.load(report.getURL().openStream());
            PortugueseReportParser parser = new PortugueseReportParser(doc);

            parser.getCountryReport();
            doc.close();
        }
    }

    @Test
    public void testCountryDataIsCorrectlyAcquired() throws IOException, ParseFailureException {
        PDDocument doc = PDDocument.load(new URL("https://covid19.min-saude.pt/wp-content/uploads/2020/09/202_DGS_boletim_20200920.pdf").openStream());
        PortugueseReportParser parser = new PortugueseReportParser(doc);

        CountryReport countryReport = parser.getCountryReport();

        CountryReport expectedReport = new CountryReport(
                new CountryReport.Report(552, 13, 347, 192),
                new CountryReport.Report(68577, 1912, 21069, 45596)
        );

        assertEquals(expectedReport, countryReport);
    }

    @Test
    public void testRegionDataIsCorrectlyAcquired() throws IOException, ParseFailureException {
        PDDocument doc = PDDocument.load(new URL("https://covid19.min-saude.pt/wp-content/uploads/2020/09/202_DGS_boletim_20200920.pdf").openStream());
        PortugueseReportParser parser = new PortugueseReportParser(doc);

        Map<String, RegionReport> regionReports = parser.getRegionReports();

        Map<String, RegionReport> expectedReports = new HashMap<String, RegionReport>();

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
    public void testBigNumbersOnCumulativeCases() throws IOException, ParseFailureException {
        PDDocument doc = PDDocument.load(new URL("https://covid19.min-saude.pt/wp-content/uploads/2020/10/232_DGS_boletim_20201020.pdf").openStream());
        PortugueseReportParser parser = new PortugueseReportParser(doc);

        CountryReport countryReport = parser.getCountryReport();

        assertEquals(countryReport.cumulative.cases, 103736);
    }
}