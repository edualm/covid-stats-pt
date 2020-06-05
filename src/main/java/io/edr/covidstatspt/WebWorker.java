/*
 *  WebWorker.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.Database;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.post;
import static spark.Spark.port;

public class WebWorker {

    private Database database = null;
    private Telegram telegram = null;
    private String webHookPath = null;

    public WebWorker(int webServerPort, String webHookPath, Database database, Telegram telegram) {
        this.webHookPath = webHookPath;
        this.database = database;
        this.telegram = telegram;

        port(webServerPort);
    }

    public void start() {
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

                if (chatId == null || textMessage == null)
                    return "Invalid chat id / text message!";

                if (textMessage.equals("/today")) {
                    String response = database.getCachedResponse();

                    if (response == null)
                        response = "No cached response available!";

                    telegram.send(chatId, response, true);

                    return "Done!";
                } else if (textMessage.equals("/start") || textMessage.equals("/subscribe")) {
                    String[] recipients = database.getTelegramRecipients();

                    if (Arrays.stream(recipients).anyMatch(chatId::equals)) {
                        telegram.send(chatId, "You are already subscribed!", false);

                        return "User is already subscribed!";
                    }

                    recipients = Arrays.copyOf(recipients, recipients.length + 1);
                    recipients[recipients.length - 1] = chatId;

                    List<String> newRecipientsAsList = Arrays.stream(recipients).filter(value -> !value.equals("")).collect(Collectors.toList());

                    String[] newRecipients = new String[newRecipientsAsList.size()];
                    newRecipients = newRecipientsAsList.toArray(newRecipients);

                    database.setTelegramRecipients(newRecipients);

                    telegram.send(chatId, "You have been successfully subscribed.", false);

                    return "Done!";
                } else if (textMessage.equals("/unsubscribe")) {
                    String[] recipients = database.getTelegramRecipients();

                    if (!Arrays.stream(recipients).anyMatch(chatId::equals)) {
                        telegram.send(chatId, "You are not subscribed!", false);

                        return "User is not subscribed!";
                    }

                    List<String> newRecipientsAsList = Arrays.stream(recipients).filter(value -> !value.equals(chatId)).collect(Collectors.toList());

                    String[] newRecipients = new String[newRecipientsAsList.size()];
                    newRecipients = newRecipientsAsList.toArray(newRecipients);

                    database.setTelegramRecipients(newRecipients);

                    telegram.send(chatId, "You have been successfully unsubscribed.", false);

                    return "Done!";
                }

                telegram.send(chatId, "I have no idea what to do with what you've just sent me.", false);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return "NOOP!";
        });
    }
}
