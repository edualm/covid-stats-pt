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
import io.edr.covidstatspt.model.MaxValuesData;
import io.edr.covidstatspt.model.RegionReport;
import io.edr.covidstatspt.model.ReportMetadata;

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

    private boolean[] checkForDailyMaximums(String date, int cases, int deaths) {
        boolean[] result = new boolean[]{false, false};

        MaxValuesData maxValues = databaseConnection.getMaxValuesData();

        if (maxValues == null) {
            maxValues = new MaxValuesData(
                    new MaxValuesData.DatedValue(date, cases),
                    new MaxValuesData.DatedValue(date, deaths)
            );

            databaseConnection.setMaxValuesData(maxValues);

            return result;
        }

        if (cases > maxValues.cases.value) {
            maxValues.cases = new MaxValuesData.DatedValue(date, cases);

            result[0] = true;
        }

        if (deaths > maxValues.deaths.value) {
            maxValues.deaths = new MaxValuesData.DatedValue(date, deaths);

            result[1] = true;
        }

        databaseConnection.setMaxValuesData(maxValues);

        return result;
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

        ReportMetadata report = null;

        try {
            report = reportLocator.getReport();
        } catch (ParseFailureException e) {
            return false;
        }

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

                messageBuilder.append("\n").append(StringFactory.buildRegionString(regionName, r));
            }

            CountryReport countryReport = parser.getCountryReport();

            messageBuilder.append("\n").append(StringFactory.buildCountryString(countryReport));

            boolean[] dailyMaximums = checkForDailyMaximums(todayStr, countryReport.day.cases, countryReport.day.deaths);

            MaxValuesData maxValues = databaseConnection.getMaxValuesData();

            if (dailyMaximums[0]) {
                messageBuilder.append("\n").append(
                        StringFactory.buildMaxCasesString(countryReport.day.cases, maxValues.cases.value)
                );
            }

            if (dailyMaximums[1]) {
                messageBuilder.append("\n").append(
                        StringFactory.buildMaxCasesString(countryReport.day.deaths, maxValues.deaths.value)
                );
            }

            messageBuilder.append("\n\n").append("\uD83D\uDCDD <b>Report DGS</b>: " + report.getURL().toString());

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
