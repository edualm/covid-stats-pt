/*
 *  Main.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.Database;
import io.edr.covidstatspt.database.ThisDBConnection;
import io.edr.covidstatspt.exceptions.MisconfigurationException;
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

    private static void checkConfiguration() throws MisconfigurationException {
        if (Secrets.KVdbURL != null && (Secrets.ThisDBAPIKey != null || Secrets.ThisDBBucketID != null)) {
            throw new MisconfigurationException("The database configuration isn't correct!");
        }

        if (Secrets.TelegramBotKey == null) {
            throw new MisconfigurationException("The telegram bot configuration isn't correct!");
        }
    }

    private static boolean mainLoop() throws IOException {
        Database database = new ThisDBConnection(Secrets.ThisDBAPIKey, Secrets.ThisDBBucketID);
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

        String message = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução Diária (referente a " + todayStr + ")</b>\n" +
                "\n" + buildRegionString("Norte", Parser.continentalRegions[0], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Centro", Parser.continentalRegions[1], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Lisboa e Vale do Tejo", Parser.continentalRegions[2], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Alentejo", Parser.continentalRegions[3], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Algarve", Parser.continentalRegions[4], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Madeira", Parser.madeiraRegion, todayParser, yesterdayParser) +
                "\n" + buildRegionString("Açores", Parser.azoresRegion, todayParser, yesterdayParser) +
                "\n\n" + buildCountryString(todayParser, yesterdayParser) +
                "\n\n<i>Regiões: + Casos / - Mortes\nPaís: + Casos / ~ Recuperados / - Mortes\nEstatísticas globais entre parênteses.</i>";

        telegram.send(message);
        database.updateLastReportURL(urls.get(0));

        return true;
    }

    private static int calculateSleepTime() {
        Date date = new Date();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay < 12) {
            int minutes = calendar.get(Calendar.MINUTE);

            return (12 - hourOfDay - 1) * 60 + (60 - minutes);
        }

        if (hourOfDay > 13) {
            int minutes = calendar.get(Calendar.MINUTE);

            return (12 + (24 - hourOfDay) - 1) * 60 + (60 - minutes);
        }

        return 0;
    }

    public static void main(String[] args) {
        System.out.println("Initializing Covid Stats PT...");

        try {
            System.out.println("Checking for configuration sanity...");

            checkConfiguration();
        } catch (MisconfigurationException e) {
            System.out.println(e.getMessage());

            System.exit(1);
        }

        System.out.println("\nEntering main loop...\n");

        while (true) {
            int minutesToSleep = calculateSleepTime();

            if (minutesToSleep > 0) {
                System.out.println("Sleeping for " + minutesToSleep + " minutes...");

                try {
                    TimeUnit.MINUTES.sleep(minutesToSleep);
                } catch (InterruptedException ignored) {
                    //  Do nothing.
                }

                continue;
            }

            try {
                while (!mainLoop()) {
                    System.out.println("Sleeping for 1 minute...");

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException ignored) {
                        //  Do nothing.
                    }
                }
            } catch (IOException e) {
                System.out.println("An IOException has occurred. We'll try again in a minute.");

                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException ignored) {
                    //  Do nothing.
                }

                continue;
            }

            System.out.println("Sleeping for 2 hours...");

            try {
                TimeUnit.HOURS.sleep(2);
            } catch (InterruptedException ignored) {
                //  Do nothing.
            }
        }
    }
}
