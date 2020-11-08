package io.edr.covidstatspt;

import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;

import java.util.Calendar;

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
                report.day.deaths +
                " mortes</code>\nCumulativo: <code>\uD83E\uDDA0 " +
                report.cumulative.cases +
                " casos, \uD83D\uDC80 " +
                report.cumulative.deaths +
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

    static String buildMaxCasesString(String maxCasesDate, int newCases, int pastCases) {
        if (newCases > pastCases)
            return "<b>\u26A0\uFE0F Novo máximo de \uD83E\uDDA0 casos: <code>" + newCases + " (+ " + (newCases - pastCases) + ")</code>";
        else
            return "<b>Máximo de \uD83E\uDDA0 casos: <code>" + pastCases + " (" + maxCasesDate + ")</code>";
    }

    static String buildMaxDeathsString(String maxDeathsDate, int newDeaths, int pastDeaths) {
        if (newDeaths > pastDeaths)
            return "<b>\u26A0\uFE0F Novo máximo de \uD83D\uDC80 mortes: <code>" + newDeaths + " (+ " + (newDeaths - pastDeaths) + ")</code>";
        else
            return "<b>Máximo de \uD83D\uDC80 mortes: <code>" + pastDeaths + " (" + maxDeathsDate + ")</code>";
    }
}
