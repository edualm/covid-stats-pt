/*
 *  Parser.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.io.IOException;
import java.util.List;

public class Parser {

    public static Rectangle[] continentalRegions = {
            new Rectangle((float)266.616,(float)437.667,(float)294.877,(float)513.525),
            new Rectangle((float)357.348,(float)426.512,(float)385.608,(float)500.882),
            new Rectangle((float)453.285,(float)365.529,(float)477.827,(float)438.411),
            new Rectangle((float)517.243,(float)415.356,(float)542.529,(float)486.008),
            new Rectangle((float)600.538,(float)412.382,(float)630.286,(float)482.289)
    };

    public static Rectangle azoresRegion = new Rectangle((float)301.57,(float)285.209,(float)327.6,(float)356.604);
    public static Rectangle madeiraRegion = new Rectangle((float)381.146,(float)288.184,(float)411.638,(float)355.86);

    public static Rectangle tableRegion = new Rectangle((float)412.382,(float)215.301,(float)800,(float)309.751);

    private PDDocument document = null;

    Parser(PDDocument document) {
        this.document = document;
    }

    private static String[][] tableToArrayOfRows(Table table) {
        List<List<RectangularTextContainer>> tableRows = table.getRows();

        int maxColCount = 0;

        for (int i = 0; i < tableRows.size(); i++) {
            List<RectangularTextContainer> row = tableRows.get(i);

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

    public int[] getCasesAndDeaths(Rectangle regionRect) throws IOException {
        ObjectExtractor oe = new ObjectExtractor(document);

        Page page = oe.extract(1);

        Page area = page.getArea(regionRect);

        BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();

        Table table = bea.extract(area).get(0);

        String[] rows = tableToArrayOfRows(table)[0];

        int[] result = {0, 0};

        for (String row: rows) {
            if (row.length() == 0)
                continue;

            if (row.contains(" ")) {
                String[] values = row.split(" ");

                if (values.length != 2)
                    continue;

                return new int[]{ Integer.parseInt(values[0]), Integer.parseInt(values[1]) };
            }

            if (result[0] == 0)
                result[0] = Integer.parseInt(row);
            else
                result[1] = Integer.parseInt(row);
        }

        return result;
    }

    public int[] getTableData() throws IOException {
        ObjectExtractor oe = new ObjectExtractor(this.document);

        Page page = oe.extract(1);

        Page area = page.getArea(tableRegion);

        BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();

        Table table = bea.extract(area).get(0);

        String[][] arrayRows = tableToArrayOfRows(table);

        int[] result = new int[]{
                Integer.parseInt(arrayRows[0][0]),
                Integer.parseInt(arrayRows[2][0]),
                Integer.parseInt(arrayRows[3][0]),
                Integer.parseInt(arrayRows[4][0]),
                Integer.parseInt(arrayRows[6][0]),
                Integer.parseInt(arrayRows[9][0])
        };

        return result;
    }
}
