package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.io.ResourceLoader;
import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.tabel.text.StanfordNLPTokenizer;
import edu.northwestern.websail.tabel.text.Tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Model Training test
 *
 */
public class TableMentionContextTest {
    public static SketchSummaryManager skMgr;
    public static Tokenizer tokenizer;
    public static HashMap<Integer, String> idToTitleMap;

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

    public static void contextUnitTest(ArrayList<Mention> mentions, int mId, WtTable t1) throws IOException {
        // For context, only the location of mention is used.
        Mention mention = mentions.get(mId);
        System.out.println("row: " + mention.cellRow + "\tcol: " + mention.cellCol);
        System.out.println("surface: " + mention.surfaceForm);


        TableMentionContext context = TableMentionContext.getInstance(mention, t1, skMgr, tokenizer);
        List<String> rowUniqueEntityStrings = context.getUniqueRowEntityStrings();
        for (String s : rowUniqueEntityStrings) {
            Integer id = Integer.valueOf(s);
            if (id == -1)
                continue;

            String title = idToTitleMap.get(id);
            System.out.println("id: " + id + "\t title: " + title);
        }
        System.out.println("");

        if (mId == 1) {
            int id1 = Integer.valueOf(rowUniqueEntityStrings.get(0));
            assertEquals("id1", 70525, id1);
            assertEquals("title1", "Ulster_Unionist_Party", idToTitleMap.get(id1));
            int id2 = Integer.valueOf(rowUniqueEntityStrings.get(1));
            assertEquals("id2", 3611706, id2);
            assertEquals("title2", "Northern_Ireland_general_election,_1929", idToTitleMap.get(id2));
        }
    }

    public static void main(String[] args) throws Exception {
        ArrayList<WtTable> tb = TableDataReader.loadTableFromResource("/test.json");
        WtTable t1 = tb.get(0);
        ArrayList<Mention> mentions = extractMention(t1);
        tokenizer = new StanfordNLPTokenizer();
        skMgr = new SketchSummaryManager();
        idToTitleMap = ResourceLoader.loadIdToTitleMap();

        for (int i=0; i<mentions.size(); i++) {
            contextUnitTest(mentions, i, t1);
        }
    }
}
