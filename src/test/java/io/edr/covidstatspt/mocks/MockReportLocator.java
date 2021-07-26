/*
 *  MockReportLocator.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt.mocks;

import io.edr.covidstatspt.ReportLocator;
import io.edr.covidstatspt.model.ReportMetadata;

import java.io.IOException;
import java.net.URL;

public class MockReportLocator implements ReportLocator {

    @Override
    public ReportMetadata getReport() throws IOException {
        return new ReportMetadata(
                "23/12/2020",
                new URL("https://covid19.min-saude.pt/wp-content/uploads/2020/12/296_DGS_boletim_20201223.pdf")
        );
    }
}
