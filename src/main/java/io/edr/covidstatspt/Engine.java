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
import io.edr.covidstatspt.model.*;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Engine {

    private final DatabaseConnection databaseConnection;
    private final ReportLocator reportLocator;
    private final MessagingConnection messagingConnection;

    public Engine(DatabaseConnection databaseConnection,
                  ReportLocator reportLocator,
                  MessagingConnection messagingConnection) {
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

        String todayFullDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        if (lastReport != null && lastReport.metadata.getName().equals(todayFullDate))
            return true;

        ReportMetadata report;

        try {
            report = reportLocator.getReport();
        } catch (Exception e) {
            messagingConnection.sendToAdmin(
                    "An error has occurred while acquiring the report for today." +
                            "\n\n" +
                            e.getMessage() +
                            "\n\n" +
                            Arrays.toString(e.getStackTrace()),
                    false,
                    true);

            return false;
        }

        //  Check if a report was published. If not, return `false`.

        if (lastReport != null && report.getName().equals(lastReport.metadata.getName()))
            return false;

        PDDocument document = PDDocument.load(report.getURL().openStream());

        ReportParser parser = new PortugueseReportParser(document);

        String todayStr = StringFactory.buildTodayDate(calendar, false);
        String todayStrWithYear = StringFactory.buildTodayDate(calendar, true);

        try {
            Map<String, RegionReport> regionReports = parser.getRegionReports();

            CountryReport countryReport = parser.getCountryReport();

            regionReports = (new LVTWorkaround(countryReport, regionReports)).generateFixedRegionReports();

            MaxValuesData maxValues = databaseConnection.getMaxValuesData();

            checkForDailyMaximums(todayStrWithYear, countryReport.day.cases, countryReport.day.deaths);

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
            messagingConnection.sendToAdmin(
                    "An error has occurred while parsing the report for " + report.getName() + "." +
                            "\n\n" +
                            e.getMessage() +
                            "\n\n" +
                            Arrays.toString(e.getStackTrace()),
                    false,
                    true);

            messagingConnection.broadcast(
                    "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>" +
                            "\n" +
                            "\n" +
                            "Ocorreu um erro na obtenção dos dados do report da DGS. No entanto, o mesmo já " +
                            "está disponível para consulta no seguinte link: " +
                    report.getURL()
            );

            return true;
        }
    }
}
