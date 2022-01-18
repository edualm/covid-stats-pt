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
        this.reportsURL = "https://covid19.min-saude.pt";
    }

    private ReportMetadata reportForElement(Element element) throws MalformedURLException {
        String name = element.text().split("[(]")[1].split("[)]")[0];

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

    public ReportMetadata getReport() throws IOException {
        Document doc = Jsoup.connect(reportsURL).get();

        Element el = doc.selectFirst("#submenu-item-68 > a");

        if (el == null)
            throw new IOException();

        return reportForElement(el);
    }
}
