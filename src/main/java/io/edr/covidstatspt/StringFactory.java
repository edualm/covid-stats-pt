/*
 *  StringFactory.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.model.*;

import java.util.Calendar;
import java.util.Map;

public class StringFactory {

    static String buildTodayDate(Calendar calendar, boolean includeYear) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR) % 100;

        String dateAsString = "" + (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month);

        if (includeYear)
            dateAsString += "" + (year < 10 ? "0" + year : year);

        return dateAsString;
    }

    static String buildRegionString(String regionName, RegionReport report) {
        return "<b>\uD83C\uDFD9️ " + regionName + "</b>\nNovos: <code>\uD83E\uDDA0 " +
                (report.day.cases >= 0 ? report.day.cases : "...") +
                " casos, \uD83D\uDC80 " +
                ((report.day.deaths < report.day.cases && report.day.deaths >= 0) ? report.day.deaths : "...") +
                " mortes</code>\nTotal: <code>\uD83E\uDDA0 " +
                (report.cumulative.cases >= 0 ? report.cumulative.cases : "...") +
                " casos, \uD83D\uDC80 " +
                ((report.cumulative.deaths < report.cumulative.cases && report.cumulative.deaths >= 0) ? report.cumulative.deaths : "...") +
                " mortes</code>\n";
    }

    static String buildCountryString(CountryReport report) {
        return "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>\nNovos: <code>\uD83E\uDDA0 " +
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
            return "<b>\u26A0\uFE0F Novo máximo de \uD83E\uDDA0 casos</b>: <code>" +
                    newCases +
                    " (+" + (newCases - pastCases.value) + ")</code>";
        else
            return "<b>Máximo de \uD83E\uDDA0 casos</b>: <code>" +
                    pastCases.value +
                    " (" + pastCases.date + ")</code>";
    }

    static String buildMaxDeathsString(String maxDeathsDate, int newDeaths, MaxValuesData.DatedValue pastDeaths) {
        if (newDeaths > pastDeaths.value)
            return "<b>\u26A0\uFE0F Novo máximo de \uD83D\uDC80 mortes</b>: <code>" +
                    newDeaths +
                    " (+" + (newDeaths - pastDeaths.value) + ")</code>";
        else
            return "<b>Máximo de \uD83D\uDC80 mortes</b>: <code>" +
                    pastDeaths.value +
                    " (" + pastDeaths.date + ")</code>";
    }

    static String buildMessage(String date,
                               ReportMetadata report,
                               CountryReport countryReport,
                               Map<String, RegionReport> regionReports,
                               MaxValuesData maxValues,
                               String[] orderedRegions) {

        StringBuilder messageBuilder =
                new StringBuilder("\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + date + "</b>\n\n");

        messageBuilder.append(StringFactory.buildCountryString(countryReport)).append("\n");

        for (String regionName: orderedRegions) {
            RegionReport r = regionReports.get(regionName);

            messageBuilder.append("\n").append(StringFactory.buildRegionString(regionName, r));
        }

        if (maxValues != null) {
            messageBuilder.append("\n")
                    .append(StringFactory.buildMaxCasesString(date, countryReport.day.cases, maxValues.cases))
                    .append("\n")
                    .append(StringFactory.buildMaxDeathsString(date, countryReport.day.deaths, maxValues.deaths))
                    .append("\n");
        }

        if (countryReport.day.cases == 0) {
            messageBuilder.append("\n")
                    .append("\uD83C\uDF89\uD83D\uDE45\uD83E\uDDA0 <b>Sem casos de COVID-19 hoje!</b> \uD83C\uDF89\uD83D\uDE45\uD83E\uDDA0");
        }

        if (countryReport.day.deaths == 0) {
            messageBuilder.append("\n")
                    .append("\uD83C\uDF89\uD83D\uDE45\uD83D\uDC80 <b>Sem mortes devido a COVID-19 hoje!</b> \uD83C\uDF89\uD83D\uDE45\uD83D\uDC80");
        }

        if (countryReport.day.cases == 0 || countryReport.day.deaths == 0) {
            messageBuilder.append("\n");
        }

        messageBuilder.append("\n").append("\uD83D\uDCDD <b>Report DGS</b>: ").append(report.getURL().toString());

        return messageBuilder.toString();
    }
}
