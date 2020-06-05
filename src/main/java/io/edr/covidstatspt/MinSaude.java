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
import java.util.ArrayList;

public class MinSaude {

    private static String MinSaudeCovid19ReportsURL = "https://covid19.min-saude.pt/relatorio-de-situacao/";

    public static ArrayList<String> getPortugueseCovidReportURLs() throws IOException {
        ArrayList<String> list = new ArrayList<String>();

        Document doc = Jsoup.connect(MinSaudeCovid19ReportsURL).get();

        Elements links = doc.select(".single_content > ul:nth-child(1) > li > a");

        for (Element link: links) {
            list.add(link.absUrl("href"));
        }

        return list;
    }
}
