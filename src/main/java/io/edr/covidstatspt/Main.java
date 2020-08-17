/*
 *  Main.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.DatabaseConnection;
import io.edr.covidstatspt.exceptions.MisconfigurationException;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

public class Main {

    private static void waitLoop(Engine engine) {
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
        } catch (MisconfigurationException e) {
            //  Do nothing here, as this only means that an admin message couldn't be sent.
        }

        SleepTimeCalculator st = new SleepTimeCalculator();

        int minutesToSleep = st.calculate();

        System.out.println("Sleeping for " + minutesToSleep + " minutes...");

        try {
            TimeUnit.MINUTES.sleep(minutesToSleep);
        } catch (InterruptedException ignored) {
            //  Do nothing.
        }
    }

    public static void main(String[] args) {
        System.out.println("Initializing Covid Stats PT...");

        try {
            System.out.println("Checking for configuration sanity...");

            Configuration.checkConfiguration();

            DatabaseConnection dbConnection = Configuration.getDatabaseConnection();
            TelegramConnection tgConnection = new TelegramConnection(dbConnection, Configuration.getTelegramBotKey());

            System.out.println("Initializing web routes...");

            WebWorker webWorker = new WebWorker(
                    Configuration.getWebPort(),
                    Configuration.getWebhookPath(),
                    dbConnection,
                    tgConnection
            );

            webWorker.start();

            ReportLocator reportLocator = new PortugueseReportLocator();

            Engine engine = new Engine(dbConnection, reportLocator, tgConnection);

            System.out.println("\nEntering main loop...\n");

            while (true)
                waitLoop(engine);
        } catch (MisconfigurationException e) {
            System.out.println(e.getMessage());

            System.exit(1);
        }
    }
}
