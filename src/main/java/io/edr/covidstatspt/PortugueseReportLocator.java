/*
 *  PortugueseReportLocator.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

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

    private ReportMetadata reportForElement(Element element) throws MalformedURLException {
        String[] split = element.text().split(" | ");
        String name = split[split.length - 1];

        URL url = new URL(element.selectFirst("> a:nth-child(1)").absUrl("href"));

        return new ReportMetadata(name, url);
    }

    public ArrayList<ReportMetadata> getReports() throws IOException {
        ArrayList<ReportMetadata> list = new ArrayList<>();

        Document doc = Jsoup.connect(reportsURL).get();

        list.add(reportForElement(doc.selectFirst(".single_content > ul:nth-child(1) > li:nth-child(1)")));
        list.add(reportForElement(doc.selectFirst(".single_content > ul:nth-child(1) > li:nth-child(2)")));

        return list;
    }
}
