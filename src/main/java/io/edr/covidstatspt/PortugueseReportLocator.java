/*
 *  PortugueseReportLocator.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.ParseFailureException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PortugueseReportLocator implements ReportLocator {

    private final String reportsURL;

    PortugueseReportLocator() {
        this.reportsURL = "https://covid19.min-saude.pt/relatorio-de-situacao/";
    }

    PortugueseReportLocator(String reportsURL) {
        this.reportsURL = reportsURL;
    }

    private ReportMetadata reportForElement(Element element) throws MalformedURLException, ParseFailureException {
        String[] split = element.text().split(" \\| ");

        if (split.length != 2) {
            throw new ParseFailureException();
        }

        String name = split[split.length - 1];

        URL url = new URL(element.selectFirst("> a:nth-child(1)").absUrl("href"));

        return new ReportMetadata(name, url);
    }

    public ReportMetadata getReport() throws IOException, ParseFailureException {
        ArrayList<ReportMetadata> list = new ArrayList<>();

        Document doc = Jsoup.connect(reportsURL).get();

        return reportForElement(doc.selectFirst(".single_content > ul:nth-child(1) > li:nth-child(1)"));
    }

    public ArrayList<ReportMetadata> getReports(int count) throws IOException, ParseFailureException {
        ArrayList<ReportMetadata> list = new ArrayList<>();

        Document doc = Jsoup.connect(reportsURL).get();

        for (int i = 0; i < count; i++) {
            list.add(reportForElement(doc.selectFirst(".single_content > ul:nth-child(1) > li:nth-child(" + (i + 1) + ")")));
        }

        return list;
    }
}
