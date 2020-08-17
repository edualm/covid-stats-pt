/*
 *  PortugueseReportParserTest.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.ParseFailureException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class PortugueseReportParserTest {

    static ArrayList<ReportMetadata> reports;

    @BeforeClass
    public static void setUp() throws IOException {
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
}