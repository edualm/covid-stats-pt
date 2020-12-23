package io.edr.covidstatspt;

import io.edr.covidstatspt.model.*;

import java.util.Calendar;
import java.util.Map;

public class StringFactory {

    static String buildTodayDate(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        return "" + (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month);
    }

    static String buildRegionString(String regionName, RegionReport report) {
        return "<b>\uD83C\uDFD9️ " + regionName + "</b>\nNovos: <code>\uD83E\uDDA0 " +
                report.day.cases +
                " casos, \uD83D\uDC80 " +
                (report.day.deaths < report.day.cases ? report.day.deaths : "...") +
                " mortes</code>\nTotal: <code>\uD83E\uDDA0 " +
                report.cumulative.cases +
                " casos, \uD83D\uDC80 " +
                (report.cumulative.deaths < report.cumulative.cases ? report.cumulative.deaths : "...") +
                " mortes</code>\n";
    }

    static String buildCountryString(CountryReport report) {
        return "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\nNovos: <code>\uD83E\uDDA0 " +
                report.day.cases +
                " casos, \uD83D\uDFE2 " +
                report.day.recoveries +
                " recuperados, \uD83D\uDD34 " +
                report.day.active +
                " ativos, \uD83D\uDC80 " +
                report.day.deaths +
                " mortes</code>\nTotal: <code>\uD83E\uDDA0 " +
                report.cumulative.cases +
                " casos, \uD83D\uDFE2 " +
                report.cumulative.recoveries +
                " recuperados, \uD83D\uDD34 " +
                report.cumulative.active +
                " ativos, \uD83D\uDC80 " +
                report.cumulative.deaths +
                " mortes</code>";
    }

    static String buildMaxCasesString(String maxCasesDate, int newCases, MaxValuesData.DatedValue pastCases) {
        if (newCases > pastCases.value)
            return "<b>\u26A0\uFE0F Novo máximo de \uD83E\uDDA0 casos</b>: <code>" + newCases + " (+" + (newCases - pastCases.value) + ")</code>";
        else
            return "<b>Máximo de \uD83E\uDDA0 casos</b>: <code>" + pastCases.value + " (" + pastCases.date + ")</code>";
    }

    static String buildMaxDeathsString(String maxDeathsDate, int newDeaths, MaxValuesData.DatedValue pastDeaths) {
        if (newDeaths > pastDeaths.value)
            return "<b>\u26A0\uFE0F Novo máximo de \uD83D\uDC80 mortes</b>: <code>" + newDeaths + " (+" + (newDeaths - pastDeaths.value) + ")</code>";
        else
            return "<b>Máximo de \uD83D\uDC80 mortes</b>: <code>" + pastDeaths.value + " (" + pastDeaths.date + ")</code>";
    }

    static String buildMessage(String date,
                               ReportMetadata report,
                               CountryReport countryReport,
                               Map<String, RegionReport> regionReports,
                               MaxValuesData maxValues,
                               String[] orderedRegions) {

        StringBuilder messageBuilder = new StringBuilder("\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + date + "</b>\n");

        for (String regionName: orderedRegions) {
            RegionReport r = regionReports.get(regionName);

            messageBuilder.append("\n").append(StringFactory.buildRegionString(regionName, r));
        }

        messageBuilder.append("\n").append(StringFactory.buildCountryString(countryReport));

        if (maxValues != null) {
            messageBuilder.append("\n\n")
                    .append(StringFactory.buildMaxCasesString(date, countryReport.day.cases, maxValues.cases))
                    .append("\n")
                    .append(StringFactory.buildMaxDeathsString(date, countryReport.day.deaths, maxValues.deaths));
        }

        messageBuilder.append("\n\n").append("\uD83D\uDCDD <b>Report DGS</b>: ").append(report.getURL().toString());

        return messageBuilder.toString();
    }
}
