/*
 *  MockReportLocator.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.mocks;

import io.edr.covidstatspt.ReportLocator;
import io.edr.covidstatspt.exceptions.ParseFailureException;
import io.edr.covidstatspt.model.ReportMetadata;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MockReportLocator implements ReportLocator {

    @Override
    public String getExpectedTodayReportNameComponent() {
        return "20201223";
    }

    @Override
    public ReportMetadata getReport() throws IOException {
        return new ReportMetadata(
                "23/12/2020",
                new URL("http://arm.robotlike.cloud/covid-test-data/296_DGS_boletim_20201223.pdf")
        );
    }
}
