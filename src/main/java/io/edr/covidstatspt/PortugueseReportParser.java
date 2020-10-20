/*
 *  PortugueseReportParser.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.exceptions.ParseFailureException;
import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class PortugueseReportParser implements ReportParser {

    public static final Map<String, Rectangle[]> regionsToRect = new HashMap<String, Rectangle[]>() {{
        put("Norte", new Rectangle[]{
                new Rectangle((float) 210.095, (float) 422.793, (float) 226.457, (float) 503.857),
                new Rectangle((float) 229.431, (float) 422.793, (float) 248.024, (float) 503.113)
        });

        put("Centro", new Rectangle[]{
                new Rectangle((float) 292.646,(float)423.537,(float)310.495,(float)497.163),
                new Rectangle((float) 318.675,(float)426.512,(float)332.062,(float)495.676)
        });

        put("Lisboa e Vale do Tejo", new Rectangle[]{
                new Rectangle((float) 421.306,(float) 343.961,(float) 437.667,(float) 422.05),
                new Rectangle((float) 440.642,(float) 342.474,(float) 459.978,(float) 422.793)
        });

        put("Alentejo", new Rectangle[]{
                new Rectangle((float) 500.882,(float) 401.97,(float) 517.987,(float) 480.802),
                new Rectangle((float) 520.962,(float) 401.226,(float) 539.554,(float) 481.546)
        });

        put("Algarve", new Rectangle[]{
                new Rectangle((float) 592.357,(float) 399.739,(float) 609.462,(float) 480.802),
                new Rectangle((float) 611.693,(float) 398.995,(float) 631.773,(float) 480.058)
        });

        put("Madeira", new Rectangle[]{
                new Rectangle((float) 343.218, (float) 245.049, (float) 361.066, (float) 324.625),
                new Rectangle((float) 364.785, (float) 245.793, (float) 384.121, (float) 324.625)
        });

        put("Açores", new Rectangle[]{
                new Rectangle((float) 204.889, (float) 244.305, (float) 221.994, (float) 325.369),
                new Rectangle((float) 225.713, (float) 247.28,  (float) 242.818, (float) 326.112)
        });
    }};

    public static final String[] orderedRegions = {
            "Norte", "Centro", "Lisboa e Vale do Tejo", "Alentejo", "Algarve", "Madeira", "Açores"
    };

    public static final Rectangle activeRect =
            new Rectangle((float) 203.402, (float) 30.12,   (float) 150, (float) 172.167);
    public static final Rectangle recoveriesRect =
            new Rectangle((float) 274.053, (float) 30.12,   (float) 150, (float) 172.167);
    public static final Rectangle deathsRect =
            new Rectangle((float) 343.961, (float) 30.864,  (float) 150,  (float) 172.167);

    public static final Rectangle casesRect =
            new Rectangle((float) 479.315, (float) 25,  (float) 155, (float) 172.91);

    private final PDDocument document;

    PortugueseReportParser(PDDocument document) {
        this.document = document;
    }

    private static int parseIntWithoutExtraCharacters(String input) {
        try {
            return Integer.parseInt(input.replaceAll("[\\D]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int parsePossiblyNegativeIntWithoutExtraCharacters(String input) {
        if (input.length() <= 1)
            return 0;

        boolean isNegative = input.contains("- ");

        int value = parseIntWithoutExtraCharacters(input);

        if (isNegative)
            value *= -1;

        return value;
    }

    private static String[][] tableToArrayOfColumns(Table table) {
        List<List<RectangularTextContainer>> tableRows = table.getRows();

        int maxColCount = 0;

        for (List<RectangularTextContainer> row : tableRows) {
            if (maxColCount < row.size())
                maxColCount = row.size();
        }

        String[][] rv = new String[tableRows.size()][maxColCount];

        for (int i = 0; i < tableRows.size(); i++) {
            List<RectangularTextContainer> row = tableRows.get(i);

            for (int j = 0; j < row.size(); j++)
                rv[i][j] = table.getCell(i, j).getText();
        }

        return rv;
    }

    @Override
    public String[] getOrderedRegions() {
        return orderedRegions;
    }

    private String[] rectangleToColumns(Rectangle rectangle, Page page) {
        Page area = page.getArea(rectangle);

        BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();

        Table table = bea.extract(area).get(0);

        String[][] arrayOfArrayOfColumns = tableToArrayOfColumns(table);

        int indexToParse = 0;

        while (indexToParse < arrayOfArrayOfColumns.length) {
            boolean isSaneColumn = false;

            for (String s: arrayOfArrayOfColumns[indexToParse]) {
                if (s.contains("-") || s.contains("+")) {
                    isSaneColumn = true;

                    break;
                }
            }

            if (isSaneColumn)
                break;

            indexToParse++;
        }

        return Arrays.stream(tableToArrayOfColumns(table)[indexToParse])
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public Map<String, RegionReport> getRegionReports() throws IOException, ParseFailureException {
        HashMap<String, RegionReport> regionReports = new HashMap<>();

        ObjectExtractor oe = new ObjectExtractor(document);

        Page page = oe.extract(1);

        for (String regionName: orderedRegions) {
            RegionReport.Report dayReport = new RegionReport.Report(0, 0);
            RegionReport.Report cumulativeReport = new RegionReport.Report(0, 0);

            Rectangle[] rectangles = regionsToRect.get(regionName);

            for (int i = 0; i < rectangles.length; i++) {
                String[] columns = rectangleToColumns(rectangles[i], page);

                if (columns.length == 1) {
                    columns = splitTableData(columns[0]);
                }

                if (i == 0) {
                    cumulativeReport.cases = parseIntWithoutExtraCharacters(columns[0]);
                    dayReport.cases = parsePossiblyNegativeIntWithoutExtraCharacters(columns[1]);
                } else {
                    cumulativeReport.deaths = parseIntWithoutExtraCharacters(columns[0]);
                    dayReport.deaths = parsePossiblyNegativeIntWithoutExtraCharacters(columns[1]);
                }
            }

            //  Hacky solution for when values appear reversed, for some reason.

            if (dayReport.cases > cumulativeReport.cases) {
                int cumulative = cumulativeReport.cases;

                cumulativeReport.cases = dayReport.cases;
                dayReport.cases = cumulative;
            }

            if (dayReport.deaths > cumulativeReport.deaths) {
                int cumulative = cumulativeReport.deaths;

                cumulativeReport.deaths = dayReport.deaths;
                dayReport.deaths = cumulative;
            }

            regionReports.put(regionName, new RegionReport(dayReport, cumulativeReport));
        }

        return regionReports;
    }

    private static String[] splitTableData(String input) {
        if (input.split("-").length == 2) {
            String[] spl = input.split("-");

            return new String[]{spl[0], "-" + spl[1]};
        } else if (input.split("\\+").length == 2) {
            String[] spl = input.split("\\+");

            return new String[]{spl[0], "+" + spl[1]};
        } else {
            return new String[]{input, "0"};
        }
    }

    @Override
    public CountryReport getCountryReport() throws IOException, ParseFailureException {
        ObjectExtractor oe = new ObjectExtractor(document);

        Page page = oe.extract(1);

        String[] activeColumns = splitTableData(rectangleToColumns(activeRect, page)[0]);
        String[] recoveriesColumns = splitTableData(rectangleToColumns(recoveriesRect, page)[0]);
        String[] deathsColumns = splitTableData(rectangleToColumns(deathsRect, page)[0]);
        String[] casesColumns = splitTableData(rectangleToColumns(casesRect, page)[0]);

        CountryReport.Report dayReport = new CountryReport.Report(
                parsePossiblyNegativeIntWithoutExtraCharacters(casesColumns[1]),
                parsePossiblyNegativeIntWithoutExtraCharacters(deathsColumns[1]),
                parsePossiblyNegativeIntWithoutExtraCharacters(activeColumns[1]),
                parsePossiblyNegativeIntWithoutExtraCharacters(recoveriesColumns[1])
        );

        CountryReport.Report cumulativeReport = new CountryReport.Report(
                parseIntWithoutExtraCharacters(casesColumns[0]),
                parseIntWithoutExtraCharacters(deathsColumns[0]),
                parseIntWithoutExtraCharacters(activeColumns[0]),
                parseIntWithoutExtraCharacters(recoveriesColumns[0])
        );

        return new CountryReport(dayReport, cumulativeReport);
    }
}
