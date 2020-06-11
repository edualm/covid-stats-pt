/*
 *  Main.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.Database;
import io.edr.covidstatspt.exceptions.MisconfigurationException;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

public class Main {

    private static void waitLoop(Engine engine) throws MisconfigurationException {
        try {
            while (!engine.run()) {
                System.out.println("Sleeping for 1 minute...");

                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException ignored) {
                    //  Do nothing.
                }
            }
        } catch (IOException e) {
            System.out.println("An IOException has occurred. We'll try again in a minute.");

            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException ignored) {
                //  Do nothing.
            }

            return;
        }

        SleepTime st = new SleepTime();

        int minutesToSleep = st.calculate();

        System.out.println("Sleeping for " + minutesToSleep + " minutes...");

        try {
            TimeUnit.HOURS.sleep(minutesToSleep);
        } catch (InterruptedException ignored) {
            //  Do nothing.
        }
    }

    public static void main(String[] args) {
        System.out.println("Initializing Covid Stats PT...");

        try {
            System.out.println("Checking for configuration sanity...");

            Configuration.checkConfiguration();

            Database dbConnection = Configuration.getDatabaseConnection();
            Telegram tgConnection = new Telegram(dbConnection, Configuration.getTelegramBotKey());

            System.out.println("Initializing web routes...");

            WebWorker webWorker = new WebWorker(
                    Configuration.getWebPort(),
                    Configuration.getWebhookPath(),
                    dbConnection,
                    tgConnection
            );

            webWorker.start();

            Engine engine = new Engine(dbConnection, tgConnection);

            System.out.println("\nEntering main loop...\n");

            while (true)
                waitLoop(engine);
        } catch (MisconfigurationException e) {
            System.out.println(e.getMessage());

            System.exit(1);
        }
    }
}
