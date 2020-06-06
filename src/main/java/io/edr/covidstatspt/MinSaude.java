/*
 *  MinSaude.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MinSaude {

    public static class Covid19Report {
        private final String name;
        private final URL url;

        Covid19Report(String name, URL url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public URL getURL() {
            return url;
        }
    }

    private static String MinSaudeCovid19ReportsURL = "https://covid19.min-saude.pt/relatorio-de-situacao/";

    public static ArrayList<Covid19Report> getPortugueseCovidReports() throws IOException {
        ArrayList<Covid19Report> list = new ArrayList<>();

        Document doc = Jsoup.connect(MinSaudeCovid19ReportsURL).get();

        Elements links = doc.select(".single_content > ul:nth-child(1) > li > a");

        for (Element link: links) {
            String[] split = link.html().split(" | ");

            String name = split[split.length - 1];
            URL url = new URL(link.absUrl("href"));

            list.add(new Covid19Report(name, url));
        }

        return list;
    }
}
