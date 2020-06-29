/*
 *  Engine.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.DatabaseConnection;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.Rectangle;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Engine {

    private final DatabaseConnection databaseConnection;
    private final ReportLocator reportLocator;
    private final TelegramConnection telegramConnection;

    public Engine(DatabaseConnection databaseConnection, ReportLocator reportLocator, TelegramConnection telegramConnection) {
        this.databaseConnection = databaseConnection;
        this.reportLocator = reportLocator;
        this.telegramConnection = telegramConnection;
    }

    private static String buildRegionString(String regionName, Rectangle regionRectangle, PortugueseReportParser todayParser, PortugueseReportParser yesterdayParser) {
        try {
            int[] today = todayParser.getCasesAndDeaths(regionRectangle);
            int[] yesterday = yesterdayParser.getCasesAndDeaths(regionRectangle);

            String newCases = String.valueOf(today[0] - yesterday[0]);
            String newDeaths = String.valueOf(today[1] - yesterday[1]);
            String totalCases = String.valueOf(today[0]);
            String totalDeaths = String.valueOf(today[1]);

            return "<b> \uD83C\uDFD9️ " + regionName + "</b>\nNovos: <code>\uD83E\uDDA0 " +
                    newCases +
                    " casos, \uD83D\uDC80 " +
                    newDeaths +
                    " mortes</code>\nCumulativo: <code>\uD83E\uDDA0 " +
                    totalCases +
                    " casos, \uD83D\uDC80 " +
                    totalDeaths +
                    " mortes</code>\n";

        } catch (PortugueseReportParser.ParseFailureException e) {
            return "<b> \uD83C\uDFD9️ " + regionName + "</b>\n<code>Erro na leitura dos dados.</code>\n";
        }
    }

    private static String buildCountryString(PortugueseReportParser todayParser, PortugueseReportParser yesterdayParser) {
        try {
            int[] today = todayParser.getTableData();
            int[] yesterday = yesterdayParser.getTableData();

            String newCases = String.valueOf(today[1] - yesterday[1]);
            String newRecovered = String.valueOf(today[4] - yesterday[4]);
            String newDeaths = String.valueOf(today[5] - yesterday[5]);
            String totalCases = String.valueOf(today[1]);
            String totalDeaths = String.valueOf(today[4]);
            String totalRecovered = String.valueOf(today[5]);

            return "<b> \uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\nNovos: <code>\uD83E\uDDA0 " +
                    newCases +
                    " casos, \uD83D\uDFE2 " +
                    newRecovered +
                    " recuperados, \uD83D\uDC80 " +
                    newDeaths +
                    " mortes</code>\nCumulativo: <code>\uD83E\uDDA0 " +
                    totalCases +
                    " casos, \uD83D\uDFE2 " +
                    totalDeaths +
                    " recuperados, \uD83D\uDC80 " +
                    totalRecovered +
                    " mortes</code>";
        } catch (PortugueseReportParser.ParseFailureException e) {
            return "<b> \uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\n<code>Erro na leitura dos dados.</code>";
        }
    }

    public boolean run() throws IOException {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        //  Check if it's before 12:00. If so, return `true`.

        if (calendar.get(Calendar.HOUR_OF_DAY) < 12)
            return true;

        //  Check if we already have a report for today. If so, return `true`.

        if (databaseConnection.getLastReportName().equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
            return true;

        ArrayList<ReportMetadata> reports = reportLocator.getReports();

        //  Check if a report was published. If not, return `false`.

        if (reports.get(0).getName().equals(databaseConnection.getLastReportName()))
            return false;

        PDDocument todayDocument = PDDocument
                .load(reports.get(0).getURL().openStream());

        PDDocument yesterdayDocument = PDDocument
                .load(reports.get(1).getURL().openStream());

        PortugueseReportParser todayParser = new PortugueseReportParser(todayDocument);
        PortugueseReportParser yesterdayParser = new PortugueseReportParser(yesterdayDocument);

        String todayStr = "" + (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month);

        String message = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n" +
                "\n" + buildRegionString("Norte", PortugueseReportParser.continentalRegions[0], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Centro", PortugueseReportParser.continentalRegions[1], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Lisboa e Vale do Tejo", PortugueseReportParser.continentalRegions[2], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Alentejo", PortugueseReportParser.continentalRegions[3], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Algarve", PortugueseReportParser.continentalRegions[4], todayParser, yesterdayParser) +
                "\n" + buildRegionString("Madeira", PortugueseReportParser.madeiraRegion, todayParser, yesterdayParser) +
                "\n" + buildRegionString("Açores", PortugueseReportParser.azoresRegion, todayParser, yesterdayParser) +
                "\n" + buildCountryString(todayParser, yesterdayParser);

        telegramConnection.broadcast(message);

        databaseConnection.setCachedResponse(message);
        databaseConnection.setLastReportName(reports.get(0).getName());

        return true;
    }
}
