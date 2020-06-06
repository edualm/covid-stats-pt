/*
 *  Engine.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.Database;
import io.edr.covidstatspt.exceptions.MisconfigurationException;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Engine {

    Database database = null;
    Telegram telegram = null;

    public Engine(Database database, Telegram telegram) {
        this.database = database;
        this.telegram = telegram;
    }

    private static String buildRegionString(String regionName, Rectangle regionRectangle, Parser todayParser, Parser yesterdayParser) throws IOException {
        return "<b> \uD83C\uDFD9️ " + regionName + "</b>\nNovos: <code>\uD83E\uDDA0 " +
                (todayParser.getCasesAndDeaths(regionRectangle)[0] - yesterdayParser.getCasesAndDeaths(regionRectangle)[0]) +
                " casos, \uD83D\uDC80 " +
                (todayParser.getCasesAndDeaths(regionRectangle)[1] - yesterdayParser.getCasesAndDeaths(regionRectangle)[1]) +
                " mortes</code>\nCumulativo: <code>\uD83E\uDDA0 " +
                todayParser.getCasesAndDeaths(regionRectangle)[0] +
                " casos, \uD83D\uDC80 " +
                todayParser.getCasesAndDeaths(regionRectangle)[1] +
                " mortes</code>\n";
    }

    private static String buildCountryString(Parser todayParser, Parser yesterdayParser) throws IOException {
        return "<b> \uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\nNovos: <code>\uD83E\uDDA0 " +
                (todayParser.getTableData()[1] - yesterdayParser.getTableData()[1]) +
                " casos, \uD83D\uDFE2 " +
                (todayParser.getTableData()[4] - yesterdayParser.getTableData()[4]) +
                " recuperados, \uD83D\uDC80 " +
                (todayParser.getTableData()[5] - yesterdayParser.getTableData()[5]) +
                " mortes</code>\nCumulativo: <code>\uD83E\uDDA0 " +
                todayParser.getTableData()[1] +
                " casos, \uD83D\uDFE2 " +
                todayParser.getTableData()[4] +
                " recuperados, \uD83D\uDC80 " +
                todayParser.getTableData()[5] +
                " mortes</code>";
    }

    public boolean run() throws IOException, MisconfigurationException {
        ArrayList<MinSaude.Covid19Report> reports = MinSaude.getPortugueseCovidReports();

        if (reports.get(0).getName().equals(database.getLastReportName()))
            return false;

        PDDocument todayDocument = PDDocument
                .load(reports.get(0).getURL().openStream());

        PDDocument yesterdayDocument = PDDocument
                .load(reports.get(1).getURL().openStream());

        Parser todayParser = new Parser(todayDocument);
        Parser yesterdayParser = new Parser(yesterdayDocument);

        Date date = new Date();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        String todayStr = "" + (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month);

        String message = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n" +
                "\n" + buildRegionString("Norte", Parser.continentalRegions[0], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Centro", Parser.continentalRegions[1], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Lisboa e Vale do Tejo", Parser.continentalRegions[2], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Alentejo", Parser.continentalRegions[3], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Algarve", Parser.continentalRegions[4], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Madeira", Parser.madeiraRegion, todayParser, yesterdayParser) +
                "\n" + buildRegionString("Açores", Parser.azoresRegion, todayParser, yesterdayParser) +
                "\n" + buildCountryString(todayParser, yesterdayParser);

        telegram.broadcast(message);

        database.setCachedResponse(message);
        database.setLastReportName(reports.get(0).getName());

        return true;
    }
}
