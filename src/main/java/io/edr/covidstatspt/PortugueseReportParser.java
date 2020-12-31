/*
 *  PortugueseReportParser.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import io.edr.covidstatspt.model.CountryReport;
import io.edr.covidstatspt.model.RegionReport;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class PortugueseReportParser implements ReportParser {

    public static final Map<String, Rectangle[]> regionsToRect = new HashMap<String, Rectangle[]>() {{
        put("Norte", new Rectangle[]{
                new Rectangle((float) 194.595227394104, (float) 419.32853015899656, (float) 214.6872776412964, (float) 214.6872776412964),
                new Rectangle((float) 217.66387767791747, (float) 420.07268016815186, (float) 234.77932788848875, (float) 234.77932788848875)
        });

        put("Centro", new Rectangle[]{
                new Rectangle((float) 306.9618787765503, (float) 406.67798000335694, (float) 324.8214789962768, (float) 324.8214789962768),
                new Rectangle((float) 328.54222904205324, (float) 406.67798000335694, (float) 344.9135292434692, (float) 344.9135292434692)
        });

        put("Lisboa e Vale do Tejo", new Rectangle[]{
                new Rectangle((float) 416.35193012237545, (float) 343.42522922515866, (float) 433.46738033294673, (float) 433.46738033294673),
                new Rectangle((float) 437.18813037872314, (float) 344.16937923431396, (float) 452.8152805709839, (float) 452.8152805709839)
        });

        put("Alentejo", new Rectangle[]{
                new Rectangle((float) 503.41748119354247, (float) 393.283279838562, (float) 519.7887813949585, (float) 519.7887813949585),
                new Rectangle((float) 523.5095314407348, (float) 394.77157985687256, (float) 540.6249816513061, (float) 540.6249816513061)
        });

        put("Algarve", new Rectangle[]{
                new Rectangle((float) 588.9947322463989, (float) 382.1210297012329, (float) 605.3660324478149, (float) 605.3660324478149),
                new Rectangle((float) 609.8309325027466, (float) 382.8651797103882, (float) 627.6905327224731, (float) 627.6905327224731)
        });

        put("Madeira", new Rectangle[]{
                new Rectangle((float) 346.40182926177977, (float) 227.33782779693604, (float) 365.74972949981685, (float) 365.74972949981685),
                new Rectangle((float) 368.72632953643796, (float) 228.0819778060913, (float) 385.84177974700924, (float) 385.84177974700924)
        });

        put("Açores", new Rectangle[]{
                new Rectangle((float) 199.06012744903563, (float) 228.82612781524656, (float) 216.9197276687622, (float) 216.9197276687622),
                new Rectangle((float) 221.38462772369382, (float) 228.0819778060913, (float) 238.50007793426514, (float) 238.50007793426514)
        });
    }};

    public static final String[] orderedRegions = {
            "Norte", "Centro", "Lisboa e Vale do Tejo", "Alentejo", "Algarve", "Madeira", "Açores"
    };

    public static final Rectangle activeRect =
            new Rectangle((float) 200.5484274673462, (float) 21.952425270080564, (float) 231.80272785186767, (float) 161.85262699127196);
    public static final Rectangle recoveriesRect =
            new Rectangle((float) 268.26607830047607, (float) 21.20827526092529, (float) 301.0086787033081, (float) 161.10847698211668);
    public static final Rectangle deathsRect =
            new Rectangle((float) 338.96032917022706, (float) 24.184875297546387, (float) 370.2146295547485, (float) 163.34092700958251);
    public static final Rectangle casesRect =
            new Rectangle((float) 463.97753070831294, (float) 19.719975242614744, (float) 494.4876810836792, (float) 160.3643269729614);

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

        boolean isNegative = input.trim().charAt(0) == '-';

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
    public Map<String, RegionReport> getRegionReports() {
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
    public CountryReport getCountryReport() {
        ObjectExtractor oe = new ObjectExtractor(document);

        Page page = oe.extract(1);

        String[] activeColumns = splitTableData(rectangleToColumns(activeRect, page)[0]);
        String[] recoveriesColumns = splitTableData(rectangleToColumns(recoveriesRect, page)[0]);
        String[] deathsColumns = splitTableData(rectangleToColumns(deathsRect, page)[0]);
        String[] casesColumns = splitTableData(rectangleToColumns(casesRect, page)[0]);

        int activeCases = parsePossiblyNegativeIntWithoutExtraCharacters(activeColumns[1]);

        if (activeCases == 0) {
            activeCases = parsePossiblyNegativeIntWithoutExtraCharacters(casesColumns[1]) - parsePossiblyNegativeIntWithoutExtraCharacters(recoveriesColumns[1]);
        }

        CountryReport.Report dayReport = new CountryReport.Report(
                parsePossiblyNegativeIntWithoutExtraCharacters(casesColumns[1]),
                parsePossiblyNegativeIntWithoutExtraCharacters(deathsColumns[1]),
                activeCases,
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
