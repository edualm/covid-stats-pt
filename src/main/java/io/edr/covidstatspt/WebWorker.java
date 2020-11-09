/*
 *  WebWorker.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.DatabaseConnection;

import io.edr.covidstatspt.model.FullReport;
import io.edr.covidstatspt.model.MaxValuesData;
import io.edr.covidstatspt.model.ReportMetadata;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.*;

@SuppressWarnings("rawtypes")
public class WebWorker {

    private final DatabaseConnection databaseConnection;
    private final MessagingConnection telegramConnection;
    private final String webHookPath;

    public WebWorker(int webServerPort, String webHookPath, DatabaseConnection databaseConnection, MessagingConnection telegramConnection) {
        this.webHookPath = webHookPath;
        this.databaseConnection = databaseConnection;
        this.telegramConnection = telegramConnection;

        port(webServerPort);
    }

    public void start() {
        get("/api/mostRecent", (req, res) -> {
            FullReport report = databaseConnection.getLastReport();

            if (report == null)
                return "{}";

            return (new Serializer<>(FullReport.class).serialize(report));
        });

        get("/api/maxValues", (req, res) -> {
            MaxValuesData maxValues = databaseConnection.getMaxValuesData();

            if (maxValues == null)
                return "{}";

            return (new Serializer<>(MaxValuesData.class).serialize(maxValues));
        });

        post("/" + webHookPath, (req, res) -> {
            try {
                JSONObject obj = (JSONObject) (new JSONParser().parse(req.body()));

                Map message = (Map) obj.get("message");

                if (message == null)
                    return "No message provided!";

                Map chatProperties = (Map) message.get("chat");

                if (chatProperties == null)
                    return "Invalid chat properties.";

                String chatId = ((Long) chatProperties.get("id")).toString();
                String textMessage = (String) message.get("text");

                if (chatId.equals("") || textMessage == null)
                    return "Invalid chat id / text message!";

                switch (textMessage) {
                    case "/about": {
                        String response = "<b>COVID Stats \uD83C\uDDF5\uD83C\uDDF9</b> is a telegram bot that parses the official COVID-19 data for Portugal and sends it daily to you via Telegram." +
                                "\n\n" +
                                "Source code for this project is available at https://github.com/edualm/covid-stats-pt under a public domain license." +
                                "\n\n" +
                                "Created and hosted by Eduardo Almeida (https://eduardo.engineer).";

                        telegramConnection.send(chatId, response, true);

                        return "Done!";
                    }

                    case "/today": {
                        String response = null;

                        FullReport report = databaseConnection.getLastReport();
                        MaxValuesData maxValues = databaseConnection.getMaxValuesData();

                        if (report == null)
                            response = "No cached response available!";
                        else {
                            response = StringFactory.buildMessage(
                                    report.name,
                                    report.metadata,
                                    report.countryReport,
                                    report.regionReports,
                                    maxValues,
                                    PortugueseReportParser.orderedRegions
                            );
                        }

                        telegramConnection.send(chatId, response, true);

                        return "Done!";
                    }

                    case "/start":
                    case "/subscribe": {
                        String[] recipients = databaseConnection.getTelegramRecipients();

                        if (Arrays.asList(recipients).contains(chatId)) {
                            telegramConnection.send(chatId, "You are already subscribed!", false);

                            return "User is already subscribed!";
                        }

                        recipients = Arrays.copyOf(recipients, recipients.length + 1);
                        recipients[recipients.length - 1] = chatId;

                        List<String> newRecipientsAsList = Arrays.stream(recipients).filter(value -> !value.equals("")).collect(Collectors.toList());

                        String[] newRecipients = new String[newRecipientsAsList.size()];
                        newRecipients = newRecipientsAsList.toArray(newRecipients);

                        databaseConnection.setTelegramRecipients(newRecipients);

                        telegramConnection.send(chatId, "You have been successfully subscribed.", false);

                        return "Done!";
                    }

                    case "/unsubscribe": {
                        String[] recipients = databaseConnection.getTelegramRecipients();

                        if (Arrays.stream(recipients).noneMatch(chatId::equals)) {
                            telegramConnection.send(chatId, "You are not subscribed!", false);

                            return "User is not subscribed!";
                        }

                        List<String> newRecipientsAsList = Arrays.stream(recipients).filter(value -> !value.equals(chatId)).collect(Collectors.toList());

                        String[] newRecipients = new String[newRecipientsAsList.size()];
                        newRecipients = newRecipientsAsList.toArray(newRecipients);

                        databaseConnection.setTelegramRecipients(newRecipients);

                        telegramConnection.send(chatId, "You have been successfully unsubscribed.", false);

                        return "Done!";
                    }
                }

                telegramConnection.send(chatId, "I have no idea what to do with what you've just sent me.", false);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return "NOOP!";
        });
    }
}
