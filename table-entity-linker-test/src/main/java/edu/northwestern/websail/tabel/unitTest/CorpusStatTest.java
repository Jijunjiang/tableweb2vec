package edu.northwestern.websail.tabel.unitTest;

import com.sun.net.httpserver.Authenticator;
import edu.northwestern.websail.tabel.io.ResourceLoader;
import edu.northwestern.websail.wda.model.CorpusStat;
import edu.northwestern.websail.wda.model.TermMeasureStat;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * For model training, similarity features
 * Corpus has the IDF information for each term, which is stored on server
 * Test if corpus is loaded correctly
 */
public class CorpusStatTest {
    public static double eps = 1e-6;
    private static Double getTermWeight(String term, CorpusStat stat) {
        TermMeasureStat t = null;
        try {
            t = stat.getTermMeasureStat(term.toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (t == null)
            return 0.0;
        else
            return t.getrIDF();
    }

    @Test
    public void loadCorpusAndGetTermWeight() throws Exception {
        CorpusStat textCorpusStat;
        CorpusStat entityCorpusStat;
        textCorpusStat = ResourceLoader.loadTextCorpusStatRAF();
        entityCorpusStat = ResourceLoader.loadEntityCorpusStatRAF();

        double w1 = getTermWeight("election", textCorpusStat);
        double w2 = getTermWeight("chicago", textCorpusStat);

        assertEquals("text idf for electio", 1.5897261330766232, w1, eps);
        assertEquals("text idf for chicago", 1.818053701512199, w2, eps);

        double e1 = getTermWeight("3611706", entityCorpusStat);
        double e2 = getTermWeight("70525", entityCorpusStat);
        assertEquals("entity idf for id 3611706", 0.11547451763116001, e1, eps);
        assertEquals("entity idf for id 70525", 2.1534100309877875, e2, eps);
    }


    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(CorpusStatTest.class);
        if (result.getFailureCount() == 0) {
            System.out.println("all passed");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
}
