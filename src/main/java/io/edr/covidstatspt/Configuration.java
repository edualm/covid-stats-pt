/*
 *  Configuration.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.database.Database;
import io.edr.covidstatspt.database.KVdbConnection;
import io.edr.covidstatspt.database.RedisConnection;
import io.edr.covidstatspt.database.ThisDBConnection;
import io.edr.covidstatspt.exceptions.MisconfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static String getPropertyValue(String property) throws MisconfigurationException {
        Properties prop = new Properties();
        String propFileName = "config.properties";

        InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            try {
                prop.load(inputStream);
            } catch (IOException e) {
                throw new MisconfigurationException("Unable to open '" + propFileName + "'.");
            }
        } else
            throw new MisconfigurationException("Property file '" + propFileName + "' not found in the classpath.");

        String value = prop.getProperty(property);

        if (value == null || value.equals(""))
            throw new MisconfigurationException("Value not found for key " + property + ".");

        return prop.getProperty(property);
    }

    public static Database getDatabaseConnection() throws MisconfigurationException {
        switch (getPropertyValue("database")) {
            case "kvdb":
                return new KVdbConnection(getPropertyValue("database.kvdb.url"));

            case "redis":
                Integer dbNumber = null;

                try {
                    dbNumber = Integer.parseInt(getPropertyValue("database.redis.db"));
                } catch (MisconfigurationException e) {
                    //  Ignore, as this value is optional.
                }

                return new RedisConnection(getPropertyValue("database.redis.server"), dbNumber);

            case "thisdb":
                return new ThisDBConnection(
                        getPropertyValue("database.thisdb.apikey"),
                        getPropertyValue("database.thisdb.bucketid")
                );

            default:
                throw new MisconfigurationException("The database configuration isn't properly set up!");
        }
    }

    public static String getTelegramBotKey() throws MisconfigurationException {
        return getPropertyValue("telegram.botkey");
    }

    public static int getWebPort() throws MisconfigurationException {
        return Integer.parseInt(getPropertyValue("web.port"));
    }

    public static String getWebhookPath() throws MisconfigurationException {
        return getPropertyValue("web.webhookpath");
    }

    public static void checkConfiguration() throws MisconfigurationException {
        getDatabaseConnection();
        getTelegramBotKey();
        getWebPort();
        getWebhookPath();
    }
}
