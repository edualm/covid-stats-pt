/*
 *  LVTWorkaround.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;

import java.util.HashMap;
import java.util.Map;

public class LVTWorkaround {

    /*
     *  For some reason, the PDF is broken on the cumulative deaths count table row.
     *
     *  As I can't parse it, and that's the only data point that appears to be broken,
     *  I am calculating it using the sum of all the other cumulative death count and
     *  the value of total deaths.
     */

    private final CountryReport countryReport;
    private final Map<String, RegionReport> regionReports;

    private static String LVTRegionKey = "Lisboa e Vale do Tejo";

    public LVTWorkaround(CountryReport countryReport, Map<String, RegionReport> regionReports) {
        this.countryReport = countryReport;
        this.regionReports = regionReports;
    }

    public Map<String, RegionReport> generateFixedRegionReports() {
        HashMap<String, RegionReport> fixedRegionReports = new HashMap<>();

        int cumulativeDeathsWithoutLVT = 0;

        for (String regionName: regionReports.keySet()) {
            if (regionName.equals(LVTRegionKey))
                continue;

            cumulativeDeathsWithoutLVT += regionReports.get(regionName).cumulative.deaths;

            fixedRegionReports.put(regionName, regionReports.get(regionName));
        }

        RegionReport lvt = regionReports.get(LVTRegionKey);
        lvt.cumulative.deaths = (countryReport.cumulative.deaths - cumulativeDeathsWithoutLVT);

        fixedRegionReports.put(LVTRegionKey, lvt);

        return fixedRegionReports;
    }
}
