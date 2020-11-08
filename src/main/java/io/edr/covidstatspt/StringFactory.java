package io.edr.covidstatspt;

import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;

public class StringFactory {

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

    static String buildMaxCasesString(int newCases, int pastCases) {
        return "<b>\u26A0\uFE0F Novo máximo de \uD83E\uDDA0 casos: <code>" + newCases + " (+ " + (newCases - pastCases) + ")</code>";
    }

    static String buildMaxDeathsString(int newDeaths, int pastDeaths) {
        return "<b>\u26A0\uFE0F Novo máximo de \uD83D\uDC80 mortes: <code>" + newDeaths + " (+ " + (newDeaths - pastDeaths) + ")</code>";
    }
}
