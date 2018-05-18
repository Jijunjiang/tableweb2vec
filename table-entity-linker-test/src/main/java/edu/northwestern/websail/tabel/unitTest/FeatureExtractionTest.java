package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.tabel.train.ModelTrainingDataExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static junit.framework.Assert.assertEquals;

/**
 * Model Training tests
 * Use cells with a link to generate mention and run feature extractor
 */
public class FeatureExtractionTest {
    static double eps = 1e-6;

    public static void printMap(HashMap mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            System.out.print(pair.getKey() + "=" + pair.getValue() + "\t");
        }
    }

    public static ArrayList<Mention> extractMention(WtTable table) {
        ArrayList<Mention> mentions = new ArrayList<Mention>();
        int row = table.numDataRows;
        int col = table.numCols;
        for (int i=0; i<row; i++)
            for (int j=0; j<col; j++) {
                WikiCell c = table.tableData[i][j];
                if (c.text == null)
                    continue;

                if (c.surfaceLinks != null && c.surfaceLinks.size() > 0) {
                    Mention m = new Mention(c.text, 1, 1);
                    m.cellRow = i;
                    m.cellCol = j;
                    WikiLink link = c.surfaceLinks.get(0);
                    Candidate candidate = new Candidate(m, link.target.title, link.target.id);
                    candidate.setIsMentionExact(true);
                    m.candidates.add(candidate);
                    mentions.add(m);
                }
            }
        return mentions;
    }

    public static void main(String[] args) throws Exception {

        ArrayList<WtTable> tb = TableDataReader.loadTableFromResource("/test.json");
        WtTable t1 = tb.get(0);
        ArrayList<Mention> mentions = extractMention(t1);
        ModelTrainingDataExtractor ex = new ModelTrainingDataExtractor();

        ArrayList<HashMap<String, Double>> features = ex.getFeatureForOneMention(mentions.get(0), t1);
        for (int i = 0; i < features.size(); i++) {
            HashMap<String, Double> f = features.get(i);
            printMap(f);
        }
        HashMap<String, Double> f = features.get(0);

        assertEquals("candidateTitleColumnTitleOverlap", 0.7230769230769231, f.get("candidateTitleColumnTitleOverlap"), eps);
        assertEquals("mentionColIdx", 2.0, f.get("mentionColIdx"), eps);
        assertEquals("surfaceAndCandidateTitleInContext", 0.0, f.get("surfaceAndCandidateTitleInContext"), eps);
        assertEquals("fullLinksAvgSr", 0.7803534859583198, f.get("fullLinksAvgSr"), eps);
        System.out.println(f.get("rowEmbeddingSimilarity"));
        System.out.println(f.get("colEmbeddingSimilarity"));
        System.out.println(f.get("subjectColumnRelation"));
    }
}
