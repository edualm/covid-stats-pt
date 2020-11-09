package io.edr.covidstatspt.model;

import java.util.Map;

public class FullReport {

    public String name;
    public ReportMetadata metadata;
    public CountryReport countryReport;
    public Map<String, RegionReport> regionReports;

    public FullReport() {
        this.name = null;
        this.metadata = null;
        this.countryReport = null;
        this.regionReports = null;
    }

    public FullReport(String date, ReportMetadata metadata, CountryReport countryReport, Map<String, RegionReport> regionReports) {
        this.name = date;
        this.metadata = metadata;
        this.countryReport = countryReport;
        this.regionReports = regionReports;
    }
}
