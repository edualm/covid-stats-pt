/*
 *  Main.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    private static String buildRegionString(String regionName, Rectangle regionRectangle, Parser todayParser, Parser yesterdayParser) throws IOException {
        return "<b>" + regionName + "</b>: <code>+" +
                (todayParser.getCasesAndDeaths(regionRectangle)[0] - yesterdayParser.getCasesAndDeaths(regionRectangle)[0]) +
                " / -" +
                (todayParser.getCasesAndDeaths(regionRectangle)[1] - yesterdayParser.getCasesAndDeaths(regionRectangle)[1]) +
                " (+" +
                todayParser.getCasesAndDeaths(regionRectangle)[0] +
                " / -" +
                todayParser.getCasesAndDeaths(regionRectangle)[1] +
                ")</code>";
    }

    private static String buildCountryString(Parser todayParser, Parser yesterdayParser) throws IOException {
        return "<b>Portugal</b>: <code>+" +
                (todayParser.getTableData()[1] - yesterdayParser.getTableData()[1]) +
                " / ~" +
                (todayParser.getTableData()[4] - yesterdayParser.getTableData()[4]) +
                " / -" +
                (todayParser.getTableData()[5] - yesterdayParser.getTableData()[5]) +
                " (+" +
                todayParser.getTableData()[1] +
                " / ~" +
                todayParser.getTableData()[4] +
                " / -" +
                todayParser.getTableData()[5] +
                ")</code>";
    }

    public static boolean mainLoop() throws IOException {
        Database database = new Database(Secrets.DatabaseURL);
        Telegram telegram = new Telegram(database, Secrets.TelegramBotKey);

        ArrayList<String> urls = MinSaude.getPortugueseCovidReportURLs();

        if (urls.get(0).equals(database.getLastReportURL()))
            return false;

        PDDocument todayDocument = PDDocument
                .load(new URL(urls.get(0)).openStream());

        PDDocument yesterdayDocument = PDDocument
                .load(new URL(urls.get(1)).openStream());

        Parser todayParser = new Parser(todayDocument);
        Parser yesterdayParser = new Parser(yesterdayDocument);

        Date date = new Date();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        String todayStr = "" + (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month);

        String message = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Deltas to Previous Day (as of " + todayStr + ")</b>\n" +
                "\n" + buildRegionString("Norte", Parser.continentalRegions[0], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Centro", Parser.continentalRegions[1], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Lisboa e Vale do Tejo", Parser.continentalRegions[2], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Alentejo", Parser.continentalRegions[3], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Algarve", Parser.continentalRegions[4], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Madeira", Parser.madeiraRegion, todayParser, yesterdayParser) +
                "\n" + buildRegionString("Açores", Parser.azoresRegion, todayParser, yesterdayParser) +
                "\n\n" + buildCountryString(todayParser, yesterdayParser) +
                "\n\n<i>Regions: + Cases / - Deaths\nCountry: + Cases / ~ Recoveries / - Deaths\nTotal stats shown in parenthesis.</i>";

        telegram.send(message);
        database.updateLastReportURL(urls.get(0));

        return true;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
            Date date = new Date();

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

            if (hourOfDay < 12 || hourOfDay > 13) {
                System.out.println("Sleeping for 5 minutes...");

                TimeUnit.MINUTES.sleep(5);

                continue;
            }

            while (!mainLoop()) {
                System.out.println("Sleeping for 1 minute...");

                TimeUnit.MINUTES.sleep(1);
            }

            System.out.println("Sleeping for 2 hours...");

            TimeUnit.HOURS.sleep(2);
        }
    }
}
