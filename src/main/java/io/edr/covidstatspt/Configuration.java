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

public class Configuration {

    public static Database getDatabaseConnection() throws MisconfigurationException {
        Database db = null;

        if (Secrets.KVdbURL != null) {
            db = new KVdbConnection(Secrets.KVdbURL);
        }

        if (Secrets.ThisDBAPIKey != null && Secrets.ThisDBBucketID != null) {
            if (db != null)
                throw new MisconfigurationException("More than one database type is configured!");

            db = new ThisDBConnection(Secrets.ThisDBAPIKey, Secrets.ThisDBBucketID);
        }

        if (Secrets.RedisConnectionString != null) {
            if (db != null)
                throw new MisconfigurationException("More than one database type is configured!");

            db = new RedisConnection(Secrets.RedisConnectionString, Secrets.RedisDatabaseID);
        }

        if (db == null)
            throw new MisconfigurationException("No database type is configured!");

        return db;
    }

    public static void checkConfiguration() throws MisconfigurationException {
        getDatabaseConnection();

        if (Secrets.TelegramBotKey == null) {
            throw new MisconfigurationException("The telegram bot configuration isn't correct!");
        }
    }
}
