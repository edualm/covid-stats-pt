/*
 *  RedisConnection.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.database;

import io.edr.covidstatspt.Serializer;
import io.edr.covidstatspt.model.FullReport;
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
    public FullReport getLastReport() {
        try {
            return (new Serializer<>(FullReport.class))
                    .deserialize(jedis.get("covid-stats-pt:last_report"));
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
            return new Serializer<>(MaxValuesData.class).deserialize(jedis.get("covid-stats-pt:max_values_data"));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean setLastReport(FullReport report) {
        try {
            jedis.set("covid-stats-pt:last_report", (new Serializer<>(FullReport.class)).serialize(report));

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
            jedis.set("covid-stats-pt:max_values_data", (new Serializer<>(MaxValuesData.class)).serialize(data));

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
