package io.edr.covidstatspt;

import io.edr.covidstatspt.database.DatabaseConnection;
import io.edr.covidstatspt.exceptions.MisconfigurationException;
import io.edr.covidstatspt.exceptions.ParseFailureException;
import io.edr.covidstatspt.model.MaxValuesData;
import io.edr.covidstatspt.model.ReportMetadata;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class EndToEndTest {

    private static class MockDatabaseConnection implements DatabaseConnection {

        private MaxValuesData maxValuesData;

        MockDatabaseConnection() {
            maxValuesData = new MaxValuesData(
                    new MaxValuesData.DatedValue("---", 5000),
                    new MaxValuesData.DatedValue("---", 50)
            );
        }

        @Override
        public String getCachedResponse() {
            return "";
        }

        @Override
        public String getLastReportName() {
            return "";
        }

        @Override
        public String[] getTelegramRecipients() {
            return new String[0];
        }

        @Override
        public MaxValuesData getMaxValuesData() {
            return new MaxValuesData(
                    new MaxValuesData.DatedValue(maxValuesData.cases.date, maxValuesData.cases.value),
                    new MaxValuesData.DatedValue(maxValuesData.deaths.date, maxValuesData.deaths.value)
            );
        }

        @Override
        public boolean setCachedResponse(String cachedResponse) {
            return false;
        }

        @Override
        public boolean setLastReportName(String newReportName) {
            return false;
        }

        @Override
        public boolean setTelegramRecipients(String[] recipients) {
            return false;
        }

        @Override
        public boolean setMaxValuesData(MaxValuesData data) {
            maxValuesData = data;

            return true;
        }
    }

    private static class SpyMessagingConnection implements MessagingConnection {

        public ArrayList<String> messages;

        public SpyMessagingConnection() {
            messages = new ArrayList<>();
        }

        @Override
        public void broadcast(String message) throws IOException {
            messages.add(message);
        }

        @Override
        public void send(String recipient, String message, boolean html) throws IOException {}

        @Override
        public void sendToAdmin(String message, boolean html) throws MisconfigurationException, IOException {}
    }

    private static class MockReportLocator implements ReportLocator {

        @Override
        public ReportMetadata getReport() throws IOException, ParseFailureException {
            return new ReportMetadata(
                    "07/11/2020",
                    new URL("https://covid19.min-saude.pt/wp-content/uploads/2020/11/250_DGS_boletim_20201107.pdf")
            );
        }
    }

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
    public void testWithNewMaximums() throws MisconfigurationException, IOException {
        //  Given
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());

        String todayStr = StringFactory.buildTodayDate(calendar);

        String expectedReport = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Norte</b>\n" +
                "Novos: <code>\uD83E\uDDA0 3900 casos, \uD83D\uDC80 31 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 82361 casos, \uD83D\uDC80 1279 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Centro</b>\n" +
                "Novos: <code>\uD83E\uDDA0 712 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 5757 casos, \uD83D\uDC80 352 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Lisboa e Vale do Tejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1856 casos, \uD83D\uDC80 19 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 67725 casos, \uD83D\uDC80 1109 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Alentejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 49 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 3361 casos, \uD83D\uDC80 63 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Algarve</b>\n" +
                "Novos: <code>\uD83E\uDDA0 82 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 3367 casos, \uD83D\uDC80 29 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Madeira</b>\n" +
                "Novos: <code>\uD83E\uDDA0 19 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 534 casos, \uD83D\uDC80 1 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Açores</b>\n" +
                "Novos: <code>\uD83E\uDDA0 22 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 435 casos, \uD83D\uDC80 15 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\n" +
                "Novos: <code>\uD83E\uDDA0 6640 casos, \uD83D\uDFE2 3993 recuperados, \uD83D\uDD34 2591 ativos, \uD83D\uDC80 56 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 173540 casos, \uD83D\uDFE2 97747 recuperados, \uD83D\uDD34 72945 ativos, \uD83D\uDC80 2848 mortes</code>\n" +
                "\n" +
                "<b>⚠️ Novo máximo de \uD83E\uDDA0 casos: <code>6640 (+ 1640)</code>\n" +
                "<b>⚠️ Novo máximo de \uD83D\uDC80 mortes: <code>56 (+ 6)</code>\n" +
                "\n" +
                "\uD83D\uDCDD <b>Report DGS</b>: https://covid19.min-saude.pt/wp-content/uploads/2020/11/250_DGS_boletim_20201107.pdf";

        //  When
        sut.run();

        //  Then
        assertEquals(1, messagingConnection.messages.size());
        assertEquals(expectedReport, messagingConnection.messages.get(0));
    }

    @Test
    public void testWithoutNewMaximums() throws MisconfigurationException, IOException {
        //  Given
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());

        String todayStr = StringFactory.buildTodayDate(calendar);

        databaseConnection.setMaxValuesData(new MaxValuesData(
                new MaxValuesData.DatedValue("---", 10000),
                new MaxValuesData.DatedValue("---", 10000))
        );

        String expectedReport = "\uD83C\uDDF5\uD83C\uDDF9 <b>[COVID-19] Evolução a " + todayStr + "</b>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Norte</b>\n" +
                "Novos: <code>\uD83E\uDDA0 3900 casos, \uD83D\uDC80 31 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 82361 casos, \uD83D\uDC80 1279 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Centro</b>\n" +
                "Novos: <code>\uD83E\uDDA0 712 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 5757 casos, \uD83D\uDC80 352 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Lisboa e Vale do Tejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 1856 casos, \uD83D\uDC80 19 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 67725 casos, \uD83D\uDC80 1109 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Alentejo</b>\n" +
                "Novos: <code>\uD83E\uDDA0 49 casos, \uD83D\uDC80 3 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 3361 casos, \uD83D\uDC80 63 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Algarve</b>\n" +
                "Novos: <code>\uD83E\uDDA0 82 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 3367 casos, \uD83D\uDC80 29 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Madeira</b>\n" +
                "Novos: <code>\uD83E\uDDA0 19 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 534 casos, \uD83D\uDC80 1 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDFD9️ Açores</b>\n" +
                "Novos: <code>\uD83E\uDDA0 22 casos, \uD83D\uDC80 0 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 435 casos, \uD83D\uDC80 15 mortes</code>\n" +
                "\n" +
                "<b>\uD83C\uDDF5\uD83C\uDDF9 Portugal</b>:\n" +
                "Novos: <code>\uD83E\uDDA0 6640 casos, \uD83D\uDFE2 3993 recuperados, \uD83D\uDD34 2591 ativos, \uD83D\uDC80 56 mortes</code>\n" +
                "Cumulativo: <code>\uD83E\uDDA0 173540 casos, \uD83D\uDFE2 97747 recuperados, \uD83D\uDD34 72945 ativos, \uD83D\uDC80 2848 mortes</code>\n" +
                "\n" +
                "<b>Máximo de \uD83E\uDDA0 casos: <code>10000 (08/11)</code>\n" +
                "<b>Máximo de \uD83D\uDC80 mortes: <code>10000 (08/11)</code>\n" +
                "\n" +
                "\uD83D\uDCDD <b>Report DGS</b>: https://covid19.min-saude.pt/wp-content/uploads/2020/11/250_DGS_boletim_20201107.pdf";

        //  When
        sut.run();

        //  Then
        assertEquals(1, messagingConnection.messages.size());
        assertEquals(expectedReport, messagingConnection.messages.get(0));
    }
}
