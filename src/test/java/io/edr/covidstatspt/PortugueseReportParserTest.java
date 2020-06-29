package io.edr.covidstatspt;

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

        reports = locator.getReports(10);
    }

    @Test
    public void getCasesAndDeaths() throws IOException, PortugueseReportParser.ParseFailureException {
        //  We are just asserting that an exception isn't thrown, so this is enough.

        for (int i = 0; i < 10; i++) {
            PDDocument doc = PDDocument.load(reports.get(i).getURL().openStream());
            PortugueseReportParser parser = new PortugueseReportParser(doc);

            for (int j = 0; j < 5; j++) {
                parser.getCasesAndDeaths(PortugueseReportParser.continentalRegions[j]);
            }

            doc.close();
        }
    }

    @Test
    public void getTableData() throws IOException, PortugueseReportParser.ParseFailureException {
        //  We are just asserting that an exception isn't thrown, so this is enough.

        for (int i = 0; i < 10; i++) {
            PDDocument doc = PDDocument.load(reports.get(i).getURL().openStream());
            PortugueseReportParser parser = new PortugueseReportParser(doc);

            parser.getTableData();
            doc.close();
        }
    }
}