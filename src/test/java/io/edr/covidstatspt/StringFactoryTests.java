/*
 *  StringFactoryTests.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;
import io.edr.covidstatspt.model.ReportMetadata;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class StringFactoryTests {

    private static CountryReport countryReportNoCases;
    private static CountryReport countryReportNoDeaths;
    private static CountryReport countryReportNoCasesNorDeaths;
    private static Map<String, RegionReport> regionReports;

    private static String todayStr;

    @BeforeClass
    public static void setUp() {
        regionReports = new HashMap<>();

        regionReports.put("Norte", new RegionReport(
                new RegionReport.Report(273, 3),
                new RegionReport.Report(24795, 871)
        ));

        regionReports.put("Centro", new RegionReport(
                new RegionReport.Report(29, 0),
                new RegionReport.Report(5621, 256)
        ));

        regionReports.put("Lisboa e Vale do Tejo", new RegionReport(
                new RegionReport.Report(179, 10),
                new RegionReport.Report(35004, 728)
        ));

        regionReports.put("Alentejo", new RegionReport(
                new RegionReport.Report(35, 0),
                new RegionReport.Report(1318, 23)
        ));

        regionReports.put("Algarve", new RegionReport(
                new RegionReport.Report(33, 0),
                new RegionReport.Report(1392, 19)
        ));

        regionReports.put("Açores", new RegionReport(
                new RegionReport.Report(2, 0),
                new RegionReport.Report(243, 15)
        ));

        regionReports.put("Madeira", new RegionReport(
                new RegionReport.Report(1, 0),
                new RegionReport.Report(204, 0)
        ));

        countryReportNoCases = new CountryReport(
                new CountryReport.Report(0, 13, 347, 192),
                new CountryReport.Report(68577, 1912, 21069, 45596)
        );

        countryReportNoDeaths = new CountryReport(
                new CountryReport.Report(552, 0, 347, 192),
                new CountryReport.Report(68577, 1912, 21069, 45596)
        );

        countryReportNoCasesNorDeaths = new CountryReport(
                new CountryReport.Report(0, 0, 347, 192),
                new CountryReport.Report(68577, 1912, 21069, 45596)
        );

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 13);

        todayStr = StringFactory.buildTodayDate(calendar);
    }

    private String todayStr() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 13);

        return StringFactory.buildTodayDate(calendar);
    }

    @Test
    public void testNoCovidCases() throws MalformedURLException {
        String expectedMessage = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr() + "</b>\n" +
                "\n" +
                "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>\n" +
                "Novos: <code>\uD83E\uDDA0 0 casos, \uD83D\uDFE2 192 recuperados, \uD83D\uDD34 347 ativos, \uD83D\uDC80 13 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 68577 casos, \uD83D\uDFE2 45596 recuperados, \uD83D\uDD34 21069 ativos, \uD83D\uDC80 1912 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Norte</b>\n" +
                "Novos: <code>\uD83E\uDDA0 273 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 24795 casos, \uD83D\uDC80 871 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Centro</b>\n" +
                "Novos: <code>\uD83E\uDDA0 29 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 5621 casos, \uD83D\uDC80 256 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Lisboa e Vale do Tejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 179 casos, \uD83D\uDC80 10 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 35004 casos, \uD83D\uDC80 728 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Alentejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 35 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1318 casos, \uD83D\uDC80 23 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Algarve</b>\n" +
                "Novos: <code>\uD83E\uDDA0 33 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1392 casos, \uD83D\uDC80 19 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Madeira</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 204 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Açores</b>\n" +
                "Novos: <code>\uD83E\uDDA0 2 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 243 casos, \uD83D\uDC80 15 mortes</code>\n" +
                "\n" +
                "\uD83C\uDF89\uD83D\uDE45\uD83E\uDDA0 <b>Sem casos de COVID-19 hoje!</b> \uD83C\uDF89\uD83D\uDE45\uD83E\uDDA0\n" +
                "\n" +
                "\uD83D\uDCDD <b>Report DGS</b>: http://report-url/";

        String builtMessage = StringFactory.buildMessage(
                todayStr,
                new ReportMetadata("1/1", new URL("http://report-url/")),
                countryReportNoCases,
                regionReports,
                null,
                PortugueseReportParser.orderedRegions
        );

        assertEquals(expectedMessage, builtMessage);
    }

    @Test
    public void testNoCovidRelatedDeaths() throws MalformedURLException {
        String expectedMessage = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr() + "</b>\n" +
                "\n" +
                "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>\n" +
                "Novos: <code>\uD83E\uDDA0 552 casos, \uD83D\uDFE2 192 recuperados, \uD83D\uDD34 347 ativos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 68577 casos, \uD83D\uDFE2 45596 recuperados, \uD83D\uDD34 21069 ativos, \uD83D\uDC80 1912 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Norte</b>\n" +
                "Novos: <code>\uD83E\uDDA0 273 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 24795 casos, \uD83D\uDC80 871 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Centro</b>\n" +
                "Novos: <code>\uD83E\uDDA0 29 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 5621 casos, \uD83D\uDC80 256 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Lisboa e Vale do Tejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 179 casos, \uD83D\uDC80 10 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 35004 casos, \uD83D\uDC80 728 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Alentejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 35 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1318 casos, \uD83D\uDC80 23 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Algarve</b>\n" +
                "Novos: <code>\uD83E\uDDA0 33 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1392 casos, \uD83D\uDC80 19 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Madeira</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 204 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Açores</b>\n" +
                "Novos: <code>\uD83E\uDDA0 2 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 243 casos, \uD83D\uDC80 15 mortes</code>\n" +
                "\n" +
                "\uD83C\uDF89\uD83D\uDE45\uD83D\uDC80 <b>Sem mortes devido a COVID-19 hoje!</b> \uD83C\uDF89\uD83D\uDE45\uD83D\uDC80\n" +
                "\n" +
                "\uD83D\uDCDD <b>Report DGS</b>: http://report-url/";

        String builtMessage = StringFactory.buildMessage(
                todayStr,
                new ReportMetadata("1/1", new URL("http://report-url/")),
                countryReportNoDeaths,
                regionReports,
                null,
                PortugueseReportParser.orderedRegions
        );

        assertEquals(expectedMessage, builtMessage);
    }

    @Test
    public void testNoCovidCasesNorCovidRelatedDeaths() throws MalformedURLException {
        String expectedMessage = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr() + "</b>\n" +
                "\n" +
                "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>\n" +
                "Novos: <code>\uD83E\uDDA0 0 casos, \uD83D\uDFE2 192 recuperados, \uD83D\uDD34 347 ativos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 68577 casos, \uD83D\uDFE2 45596 recuperados, \uD83D\uDD34 21069 ativos, \uD83D\uDC80 1912 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Norte</b>\n" +
                "Novos: <code>\uD83E\uDDA0 273 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 24795 casos, \uD83D\uDC80 871 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Centro</b>\n" +
                "Novos: <code>\uD83E\uDDA0 29 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 5621 casos, \uD83D\uDC80 256 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Lisboa e Vale do Tejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 179 casos, \uD83D\uDC80 10 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 35004 casos, \uD83D\uDC80 728 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Alentejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 35 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1318 casos, \uD83D\uDC80 23 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Algarve</b>\n" +
                "Novos: <code>\uD83E\uDDA0 33 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1392 casos, \uD83D\uDC80 19 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Madeira</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 204 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Açores</b>\n" +
                "Novos: <code>\uD83E\uDDA0 2 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 243 casos, \uD83D\uDC80 15 mortes</code>\n" +
                "\n" +
                "\uD83C\uDF89\uD83D\uDE45\uD83E\uDDA0 <b>Sem casos de COVID-19 hoje!</b> \uD83C\uDF89\uD83D\uDE45\uD83E\uDDA0\n" +
                "\uD83C\uDF89\uD83D\uDE45\uD83D\uDC80 <b>Sem mortes devido a COVID-19 hoje!</b> \uD83C\uDF89\uD83D\uDE45\uD83D\uDC80\n" +
                "\n" +
                "\uD83D\uDCDD <b>Report DGS</b>: http://report-url/";

        String builtMessage = StringFactory.buildMessage(
                todayStr,
                new ReportMetadata("1/1", new URL("http://report-url/")),
                countryReportNoCasesNorDeaths,
                regionReports,
                null,
                PortugueseReportParser.orderedRegions
        );

        assertEquals(expectedMessage, builtMessage);
    }
}
