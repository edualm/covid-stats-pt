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
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    private static int calculateSleepTime() {
        Date date = new Date();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay < 12) {
            int minutes = calendar.get(Calendar.MINUTE);

            return (12 - hourOfDay - 1) * 60 + (60 - minutes);
        }

        if (hourOfDay > 13) {
            int minutes = calendar.get(Calendar.MINUTE);

            return (12 + (24 - hourOfDay) - 1) * 60 + (60 - minutes);
        }

        return 0;
    }

    private static void waitLoop(Engine engine) throws MisconfigurationException {
        int minutesToSleep = calculateSleepTime();

        if (minutesToSleep > 0) {
            System.out.println("Sleeping for " + minutesToSleep + " minutes...");

            try {
                TimeUnit.MINUTES.sleep(minutesToSleep);
            } catch (InterruptedException ignored) {
                //  Do nothing.
            }

            return;
        }

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

        System.out.println("Sleeping for 2 hours...");

        try {
            TimeUnit.HOURS.sleep(2);
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
            Telegram tgConnection = new Telegram(dbConnection, Secrets.TelegramBotKey);

            System.out.println("Initializing web routes...");

            WebWorker webWorker = new WebWorker(Secrets.WebServerPort, dbConnection, tgConnection);

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
