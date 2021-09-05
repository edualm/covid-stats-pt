/*
 *  EndToEndTests.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.MisconfigurationException;
import io.edr.covidstatspt.mocks.MockDatabaseConnection;
import io.edr.covidstatspt.mocks.MockReportLocator;
import io.edr.covidstatspt.mocks.SpyMessagingConnection;
import io.edr.covidstatspt.model.MaxValuesData;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class EndToEndTests {

    private Engine sut;

    private MockDatabaseConnection databaseConnection;
    private SpyMessagingConnection messagingConnection;

    @Before
    public void setUp() {
        databaseConnection = new MockDatabaseConnection();
        messagingConnection = new SpyMessagingConnection();

        sut = new Engine(
                databaseConnection,
                new MockReportLocator(),
                messagingConnection
        );
    }

    @Test
    public void testWithoutStoredMaximums() throws MisconfigurationException, IOException {
        //  Given
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 13);

        String todayStr = StringFactory.buildTodayDate(calendar);

        databaseConnection.setMaxValuesData(null);

        String expectedReport = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n" +
                "\n" +
                "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\n" +
                "Novos: <code>\uD83E\uDDA0 4602 casos, \uD83D\uDFE2 3621 recuperados, \uD83D\uDD34 892 ativos, \uD83D\uDC80 89 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 383258 casos, \uD83D\uDFE2 308446 recuperados, \uD83D\uDD34 1668469 ativos, \uD83D\uDC80 6343 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Norte</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1811 casos, \uD83D\uDC80 32 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 197768 casos, \uD83D\uDC80 2973 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Centro</b>\n" +
                "Novos: <code>\uD83E\uDDA0 791 casos, \uD83D\uDC80 14 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 42462 casos, \uD83D\uDC80 903 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Lisboa e Vale do Tejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1467 casos, \uD83D\uDC80 40 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 123541 casos, \uD83D\uDC80 2196 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Alentejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 322 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 9727 casos, \uD83D\uDC80 176 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Algarve</b>\n" +
                "Novos: <code>\uD83E\uDDA0 151 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 6812 casos, \uD83D\uDC80 64 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Madeira</b>\n" +
                "Novos: <code>\uD83E\uDDA0 38 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1332 casos, \uD83D\uDC80 10 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Açores</b>\n" +
                "Novos: <code>\uD83E\uDDA0 22 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1616 casos, \uD83D\uDC80 21 mortes</code>\n" +
                "\n" +
                "\uD83D\uDCDD <b>Report DGS</b>: https://covid19.min-saude.pt/wp-content/uploads/2020/12/296_DGS_boletim_20201223.pdf";

        //  When
        sut.run(calendar.getTime());

        //  Then
        assertEquals(1, messagingConnection.messages.size());
        assertEquals(expectedReport, messagingConnection.messages.get(0));
    }

    @Test
    public void testWithNewMaximums() throws MisconfigurationException, IOException {
        //  Given
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 13);

        String todayStr = StringFactory.buildTodayDate(calendar);

        String expectedReport = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n" +
                "\n" +
                "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\n" +
                "Novos: <code>\uD83E\uDDA0 4602 casos, \uD83D\uDFE2 3621 recuperados, \uD83D\uDD34 892 ativos, \uD83D\uDC80 89 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 383258 casos, \uD83D\uDFE2 308446 recuperados, \uD83D\uDD34 1668469 ativos, \uD83D\uDC80 6343 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Norte</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1811 casos, \uD83D\uDC80 32 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 197768 casos, \uD83D\uDC80 2973 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Centro</b>\n" +
                "Novos: <code>\uD83E\uDDA0 791 casos, \uD83D\uDC80 14 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 42462 casos, \uD83D\uDC80 903 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Lisboa e Vale do Tejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1467 casos, \uD83D\uDC80 40 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 123541 casos, \uD83D\uDC80 2196 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Alentejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 322 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 9727 casos, \uD83D\uDC80 176 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Algarve</b>\n" +
                "Novos: <code>\uD83E\uDDA0 151 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 6812 casos, \uD83D\uDC80 64 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Madeira</b>\n" +
                "Novos: <code>\uD83E\uDDA0 38 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1332 casos, \uD83D\uDC80 10 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Açores</b>\n" +
                "Novos: <code>\uD83E\uDDA0 22 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1616 casos, \uD83D\uDC80 21 mortes</code>\n" +
                "\n" +
                "<b>Máximo de \uD83E\uDDA0 casos</b>: <code>5000 (---)</code>\n" +
                "<b>⚠️ Novo máximo de \uD83D\uDC80 mortes</b>: <code>89 (+39)</code>\n" +
                "\n" +
                "\uD83D\uDCDD <b>Report DGS</b>: https://covid19.min-saude.pt/wp-content/uploads/2020/12/296_DGS_boletim_20201223.pdf";

        //  When
        sut.run(calendar.getTime());

        //  Then
        assertEquals(1, messagingConnection.messages.size());
        assertEquals(expectedReport, messagingConnection.messages.get(0));
    }

    @Test
    public void testWithoutNewMaximums() throws MisconfigurationException, IOException {
        //  Given
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 13);

        String todayStr = StringFactory.buildTodayDate(calendar);

        databaseConnection.setMaxValuesData(new MaxValuesData(
                new MaxValuesData.DatedValue("---", 10000),
                new MaxValuesData.DatedValue("---", 10000))
        );

        String expectedReport = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n" +
                "\n" +
                "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\n" +
                "Novos: <code>\uD83E\uDDA0 4602 casos, \uD83D\uDFE2 3621 recuperados, \uD83D\uDD34 892 ativos, \uD83D\uDC80 89 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 383258 casos, \uD83D\uDFE2 308446 recuperados, \uD83D\uDD34 1668469 ativos, \uD83D\uDC80 6343 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Norte</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1811 casos, \uD83D\uDC80 32 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 197768 casos, \uD83D\uDC80 2973 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Centro</b>\n" +
                "Novos: <code>\uD83E\uDDA0 791 casos, \uD83D\uDC80 14 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 42462 casos, \uD83D\uDC80 903 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Lisboa e Vale do Tejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1467 casos, \uD83D\uDC80 40 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 123541 casos, \uD83D\uDC80 2196 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Alentejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 322 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 9727 casos, \uD83D\uDC80 176 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Algarve</b>\n" +
                "Novos: <code>\uD83E\uDDA0 151 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 6812 casos, \uD83D\uDC80 64 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Madeira</b>\n" +
                "Novos: <code>\uD83E\uDDA0 38 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1332 casos, \uD83D\uDC80 10 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Açores</b>\n" +
                "Novos: <code>\uD83E\uDDA0 22 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Total: <code>\uD83E\uDDA0 1616 casos, \uD83D\uDC80 21 mortes</code>\n" +
                "\n" +
                "<b>Máximo de \uD83E\uDDA0 casos</b>: <code>10000 (---)</code>\n" +
                "<b>Máximo de \uD83D\uDC80 mortes</b>: <code>10000 (---)</code>\n" +
                "\n" +
                "\uD83D\uDCDD <b>Report DGS</b>: https://covid19.min-saude.pt/wp-content/uploads/2020/12/296_DGS_boletim_20201223.pdf";

        //  When
        sut.run(calendar.getTime());

        //  Then
        assertEquals(1, messagingConnection.messages.size());
        assertEquals(expectedReport, messagingConnection.messages.get(0));
    }
}
