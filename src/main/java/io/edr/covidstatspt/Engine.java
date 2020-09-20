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
import io.edr.covidstatspt.exceptions.ParseFailureException;
import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Engine {

    private final DatabaseConnection databaseConnection;
    private final ReportLocator reportLocator;
    private final TelegramConnection telegramConnection;

    public Engine(DatabaseConnection databaseConnection, ReportLocator reportLocator, TelegramConnection telegramConnection) {
        this.databaseConnection = databaseConnection;
        this.reportLocator = reportLocator;
        this.telegramConnection = telegramConnection;
    }

    private static String buildRegionString(String regionName, RegionReport report) {
        return "<b> \uD83C\uDFD9️ " + regionName + "</b>\nNovos: <code>\uD83E\uDDA0 " +
                report.day.cases +
                " casos, \uD83D\uDC80 " +
                report.day.deaths +
                " mortes</code>\nCumulativo: <code>\uD83E\uDDA0 " +
                report.cumulative.cases +
                " casos, \uD83D\uDC80 " +
                report.cumulative.deaths +
                " mortes</code>\n";
    }

    private static String buildCountryString(CountryReport report) {
        return "<b> \uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\nNovos: <code>\uD83E\uDDA0 " +
                report.day.cases +
                " casos, \uD83D\uDFE2 " +
                report.day.recoveries +
                " recuperados, \uD83D\uDD34 " +
                report.day.active +
                " ativos, \uD83D\uDC80 " +
                report.day.deaths +
                " mortes</code>\nCumulativo: <code>\uD83E\uDDA0 " +
                report.cumulative.cases +
                " casos, \uD83D\uDFE2 " +
                report.cumulative.recoveries +
                " recuperados, \uD83D\uDD34 " +
                report.cumulative.active +
                " ativos, \uD83D\uDC80 " +
                report.cumulative.deaths +
                " mortes</code>";
    }

    public boolean run() throws MisconfigurationException, IOException {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        //  Check if it's before 12:00. If so, return `true`.

        if (hour < 12)
            return true;

        //  Check if we already have a report for today. If so, return `true`.

        if (databaseConnection.getLastReportName().equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
            return true;

        ReportMetadata report = reportLocator.getReport();

        //  Check if a report was published. If not, return `false`.

        if (report.getName().equals(databaseConnection.getLastReportName()))
            return false;

        PDDocument document = PDDocument.load(report.getURL().openStream());

        ReportParser parser = new PortugueseReportParser(document);

        String todayStr = "" + (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month);

        try {
            Map<String, RegionReport> regionReports = parser.getRegionReports();

            StringBuilder messageBuilder = new StringBuilder("\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n");

            for (String regionName: parser.getOrderedRegions()) {
                RegionReport r = regionReports.get(regionName);

                messageBuilder.append("\n").append(buildRegionString(regionName, r));
            }

            messageBuilder.append("\n").append(buildCountryString(parser.getCountryReport()));

            messageBuilder.append("\n\n").append("Report DGS: " + report.getURL().toString());

            String message = messageBuilder.toString();

            telegramConnection.broadcast(message);

            databaseConnection.setCachedResponse(message);
            databaseConnection.setLastReportName(report.getName());

            return true;
        } catch (ParseFailureException e) {
            telegramConnection.sendToAdmin("An error has occurred while parsing the report for " + report.getName(), false);

            StringBuilder messageBuilder = new StringBuilder("\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n");

            messageBuilder.append("\nOcorreu um erro na obtenção dos dados do report da DGS. No entanto, o mesmo já está disponível para consulta no seguinte link: " + report.getURL().toString());

            String message = messageBuilder.toString();

            telegramConnection.broadcast(messageBuilder.toString());

            databaseConnection.setCachedResponse(message);

            databaseConnection.setLastReportName(report.getName());

            return true;
        }
    }
}
