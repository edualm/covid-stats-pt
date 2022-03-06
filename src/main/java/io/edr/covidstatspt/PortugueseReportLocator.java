/*
 *  PortugueseReportLocator.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.ParseFailureException;
import io.edr.covidstatspt.model.ReportMetadata;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PortugueseReportLocator implements ReportLocator {

    private final String reportsURL;

    PortugueseReportLocator() {
        this.reportsURL = "https://covid19.min-saude.pt/relatorio-de-situacao/";
    }

    private ReportMetadata reportForElement(Element element) throws MalformedURLException, ParseFailureException {
        String[] split = element.text().split(" \\| ");

        if (split.length != 2) {
            throw new ParseFailureException();
        }

        String name = split[split.length - 1];

        URL url = new URL(element.absUrl("href"));

        return new ReportMetadata(name, url);
    }

    public String getExpectedTodayReportNameComponent() {
        return getExpectedTodayReportNameComponent(new Date());
    }

    public String getExpectedTodayReportNameComponent(Date today) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        return sdf.format(today);
    }

    public ReportMetadata getReport() throws IOException, ParseFailureException {
        Document doc = Jsoup.connect(reportsURL).get();

        Element el = doc.selectFirst("#acordeaoc-0 > p > a");

        if (el == null)
            throw new IOException();

        return reportForElement(el);
    }
}