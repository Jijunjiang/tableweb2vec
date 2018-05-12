package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.Mention;
import edu.northwestern.websail.tabel.model.MentionDoc;
import edu.northwestern.websail.tabel.model.MentionDocRAFManager;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class MentionDocTest {
    @Test
    public void testLoadingMentionID() throws Exception {
        ArrayList<String> trainingMentionIDs = TableDataReader.loadMentionIDs(GlobalConfig.testingMentionDir);
        assertEquals("Training Mention 1", "5358b063a3103b9806426e08", trainingMentionIDs.get(0));
        assertEquals("Traning Mention 2", "53589687a3103b9804f164f3", trainingMentionIDs.get(1));
        assertEquals("Training Mention last",
                "5358af63a3103b98063552f5",
                trainingMentionIDs.get(trainingMentionIDs.size()-1)
        );
    }

    @Test
    public void testMentionLoader() throws Exception {
        String filename = "/link.json";
        ArrayList<MentionDoc> mentionDocs = TableDataReader.loadMentionDocsFromResource(filename);
        MentionDoc m1 = mentionDocs.get(0);

        assertEquals("row", 0, m1.cellRow);
        assertEquals("col", 4, m1.cellCol);
        assertEquals("tilte", "Arabic alphabet", m1.pgTitle);
        assertEquals("oid", "535891dca3103b9804b3bd01", m1._id.$oid);
    }

    @Test
    public void testMentionDocRAF() throws Exception {
        String posFile = GlobalConfig.tableMentionPos;
        String rafFile = GlobalConfig.tableMentionRAF;
        //posFile = "/Users/ruohongzhang/Desktop/websail/table-entity-linker/testTableMentions.pos";
        //rafFile = "/Users/ruohongzhang/Desktop/websail/table-entity-linker/testTableMentions.raf";
        MentionDocRAFManager mentionMgr = new MentionDocRAFManager(posFile, rafFile);
        MentionDoc m1 = mentionMgr.getMentionDocFromRAF("535891dca3103b9804b3bd01");

        assertEquals("row", 0, m1.cellRow);
        assertEquals("col", 4, m1.cellCol);
        assertEquals("tilte", "Arabic alphabet", m1.pgTitle);
        assertEquals("oid", "535891dca3103b9804b3bd01", m1._id.$oid);

        MentionDoc m2 = mentionMgr.getMentionDocFromRAF("535891dca3103b9804b3bd1c");
        assertEquals("tilte", "John West, 1st Earl De La Warr", m2.pgTitle);
        mentionMgr.close();
    }

    public static void main(String[] args) throws Exception {
        Result result = JUnitCore.runClasses(MentionDocTest.class);
        if (result.getFailureCount() == 0) {
            System.out.println("all passed");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
}
