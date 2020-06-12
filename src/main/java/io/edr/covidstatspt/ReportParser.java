/*
 *  ReportParser.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import technology.tabula.Rectangle;

import java.io.IOException;

public interface ReportParser {

    int[] getCasesAndDeaths(Rectangle regionRect) throws IOException;
    int[] getTableData() throws IOException;
}
