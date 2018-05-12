package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.Candidate;
import edu.northwestern.websail.tabel.model.Mention;
import edu.northwestern.websail.tabel.model.WtTable;
import edu.northwestern.websail.tabel.train.ModelTrainingDataExtractor;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * For model training
 * Test whether mention is extracted correctly from table.
 * Compare if the surface form, locations are correct. Also compare the candidate features
 */
public class MentionExtractionTest {

    @Test
    public void extractMentionFromTable() throws Exception {
        ArrayList<WtTable> tb = TableDataReader.loadTableFromResource("/test.json");
        WtTable t1 = tb.get(0);
        System.out.println(t1.numDataRows + " " + t1.numCols);
        ModelTrainingDataExtractor ex = new ModelTrainingDataExtractor();
        ArrayList<Mention> mentions = ex.extractMentionsFromTable(t1);
        System.out.println("number of mentions: " + mentions.size());
        for (int i = 0; i < mentions.size(); i++) {
            Mention m = mentions.get(i);
            System.out.println("surface: " + m.surfaceForm);
            System.out.println("row: " + m.cellRow);
            System.out.println("col: " + m.cellCol);
            for (int j = 0; j < m.candidates.size() && j < 5; j++) {
                System.out.println("\t title: " + m.candidates.get(j).wikiTitle.title);
                System.out.println("\t id: " + m.candidates.get(j).wikiTitle.id);
            }
        }

        Mention m = mentions.get(mentions.size() - 1);
        assertEquals("cell Row", 10, (int)m.cellRow);
        assertEquals("cell col", 3, (int)m.cellRow);
        Candidate c = m.candidates.get(0);
        assertEquals("titleId", 349110, c.wikiTitle.id);
        assertEquals("title", "Boundary_Commissions_(United_Kingdom)", c.wikiTitle.title);
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(MentionExtractionTest.class);
        if (result.getFailureCount() == 0) {
            System.out.println("all passed");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
}
