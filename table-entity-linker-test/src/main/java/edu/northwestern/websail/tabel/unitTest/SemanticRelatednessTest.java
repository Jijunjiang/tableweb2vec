package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.featureExtraction.SemanticRelatednessFeatures;
import edu.northwestern.websail.tabel.io.ResourceLoader;
import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.WikiCell;
import edu.northwestern.websail.tabel.model.WikiLink;
import edu.northwestern.websail.tabel.model.WtTable;
import org.junit.Test;
import pulse.util.SemanticRelatedness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class SemanticRelatednessTest {
    public static double eps = 1e-6;

    public static void SRUnitTest(SemanticRelatedness sr, int titleId1, int titleId2) throws IOException {
        System.out.println("titleId1: " + titleId1 + "\t titleId2: " + titleId2);
        SemanticRelatednessFeatures srExtractor = new SemanticRelatednessFeatures();
        HashMap<Integer, Double> srMap = srExtractor.getSRs(sr, titleId1);
        double srValue = srMap.get(titleId2);
        System.out.println("sr val: " + srValue);

        if (titleId1 == 3611706 && titleId2 == 70525) {
            assertEquals("sr value", 0.7837975295917979, srValue, eps);
        }

        if (titleId1 == 27678607 && titleId2 == 70525) {
            assertEquals("sr value", 0.6610972622567769, srValue, eps);
        }
    }

    @Test
    public static void testSRValue() throws Exception {
        SemanticRelatedness sr = ResourceLoader.loadSRSource();
        ArrayList<WtTable> tb = TableDataReader.loadTableFromResource("/test.json");
        WtTable t1 = tb.get(0);

        ArrayList<Integer> titleIds = new ArrayList<Integer>();
        int row = t1.numDataRows;
        int col = t1.numCols;
        for (int i=0; i<row; i++)
            for (int j=0; j<col; j++) {
                WikiCell c = t1.tableData[i][j];
                for (WikiLink link : c.surfaceLinks) {
                    titleIds.add(link.target.id);
                }
            }
        for (int i=0; i<titleIds.size() && i < 20; i++)
            for (int j=0; j<titleIds.size() && j < 20; j++) {
                if (i == j)
                    continue;
                if (titleIds.get(i) == -1 || titleIds.get(j) == -1)
                    continue;
                SRUnitTest(sr, titleIds.get(i), titleIds.get(j));
            }
    }


    public static void main(String[] args) throws Exception {
        testSRValue();
        /*
        Result result = JUnitCore.runClasses(SemanticRelatedness.class);
        if (result.getFailureCount() == 0) {
            System.out.println("all passed");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }*/
    }
}
