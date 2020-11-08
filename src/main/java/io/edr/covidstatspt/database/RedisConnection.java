/*
 *  RedisConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

import io.edr.covidstatspt.model.MaxValuesData;
import redis.clients.jedis.Jedis;

public class RedisConnection implements DatabaseConnection {

    private final Jedis jedis;

    public RedisConnection(String connectionString, Integer db) {
        this.jedis = new Jedis(connectionString);

        if (db != null)
            jedis.select(db);
    }

    @Override
    public String getCachedResponse() {
        try {
            return jedis.get("covid-stats-pt:cached_response");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getLastReportName() {
        try {
            return jedis.get("covid-stats-pt:last_report_name");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String[] getTelegramRecipients() {
        try {
            return jedis.get("covid-stats-pt:recipients").split(",");
        } catch (Exception e) {
            return new String[]{};
        }
    }

    @Override
    public MaxValuesData getMaxValuesData() {
        try {
            return MaxValuesData.deserialize(jedis.get("covid-stats-pt:max-values-data"));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean setCachedResponse(String cachedResponse) {
        try {
            jedis.set("covid-stats-pt:cached_response", cachedResponse);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setLastReportName(String newReportURL) {
        try {
            jedis.set("covid-stats-pt:last_report_name", newReportURL);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setTelegramRecipients(String[] recipients) {
        try {
            jedis.set("covid-stats-pt:recipients", String.join(",", recipients));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setMaxValuesData(MaxValuesData data) {
        try {
            jedis.set("covid-stats-pt:max-values-data", data.serialize());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
