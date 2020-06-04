package io.edr.covidstatspt;

import java.io.IOException;

public interface Database {

    public String getLastReportURL() throws IOException;
    public String[] getTelegramRecipients() throws IOException;

    public void updateLastReportURL(String newReportURL) throws IOException;
}
