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
import io.edr.covidstatspt.model.*;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Engine {

    private final DatabaseConnection databaseConnection;
    private final ReportLocator reportLocator;
    private final MessagingConnection messagingConnection;

    public Engine(DatabaseConnection databaseConnection, ReportLocator reportLocator, MessagingConnection messagingConnection) {
        this.databaseConnection = databaseConnection;
        this.reportLocator = reportLocator;
        this.messagingConnection = messagingConnection;
    }

    private void checkForDailyMaximums(String date, int cases, int deaths) {
        MaxValuesData maxValues = databaseConnection.getMaxValuesData();

        if (maxValues == null) {
            maxValues = new MaxValuesData(
                    new MaxValuesData.DatedValue(date, cases),
                    new MaxValuesData.DatedValue(date, deaths)
            );

            databaseConnection.setMaxValuesData(maxValues);

            return;
        }

        if (cases > maxValues.cases.value)
            maxValues.cases = new MaxValuesData.DatedValue(date, cases);

        if (deaths > maxValues.deaths.value)
            maxValues.deaths = new MaxValuesData.DatedValue(date, deaths);

        databaseConnection.setMaxValuesData(maxValues);
    }

    public boolean run() throws MisconfigurationException, IOException {
        return run(new Date());
    }

    public boolean run(Date currentDate) throws MisconfigurationException, IOException {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(currentDate);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        //  Check if it's before 12:00. If so, return `true`.

        if (hour < 12)
            return true;

        //  Check if we already have a report for today. If so, return `true`.

        FullReport lastReport = databaseConnection.getLastReport();
        String lastReportName = (lastReport != null ? lastReport.name : "");

        if (lastReportName.equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
            return true;

        ReportMetadata report;

        try {
            report = reportLocator.getReport();
        } catch (ParseFailureException e) {
            return false;
        }

        //  Check if a report was published. If not, return `false`.

        if (report.getName().equals(lastReportName))
            return false;

        PDDocument document = PDDocument.load(report.getURL().openStream());

        ReportParser parser = new PortugueseReportParser(document);

        String todayStr = StringFactory.buildTodayDate(calendar);

        try {
            Map<String, RegionReport> regionReports = parser.getRegionReports();

            CountryReport countryReport = parser.getCountryReport();

            MaxValuesData maxValues = databaseConnection.getMaxValuesData();

            checkForDailyMaximums(todayStr, countryReport.day.cases, countryReport.day.deaths);

            String message = StringFactory.buildMessage(
                    todayStr,
                    report,
                    countryReport,
                    regionReports,
                    maxValues,
                    parser.getOrderedRegions()
            );

            messagingConnection.broadcast(message);

            databaseConnection.setLastReport(new FullReport(todayStr, report, countryReport, regionReports));

            return true;
        } catch (Exception e) {
            messagingConnection.sendToAdmin("An error has occurred while parsing the report for " + report.getName(), false);

            StringBuilder messageBuilder = new StringBuilder("\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n");

            messageBuilder.append("\nOcorreu um erro na obtenção dos dados do report da DGS. No entanto, o mesmo já está disponível para consulta no seguinte link: ").append(report.getURL().toString());

            String message = messageBuilder.toString();

            messagingConnection.broadcast(messageBuilder.toString());

            return true;
        }
    }
}
