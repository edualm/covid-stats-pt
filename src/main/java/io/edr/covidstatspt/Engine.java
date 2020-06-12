/*
 *  Engine.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.DatabaseConnection;
import io.edr.covidstatspt.exceptions.MisconfigurationException;
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

    private static String buildCountryString(PortugueseReportParser todayParser, PortugueseReportParser yesterdayParser) {
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
