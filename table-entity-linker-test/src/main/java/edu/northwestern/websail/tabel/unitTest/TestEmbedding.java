package edu.northwestern.websail.tabel.unitTest;

import Jama.Matrix;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.W2CSQLTrie;
import edu.northwestern.websail.tabel.Embeddings.Embeddings;
import edu.northwestern.websail.tabel.io.ResourceLoader;
import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.tabel.train.ModelTrainingDataExtractor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Created by apple on 15/05/2018.
 */


public class TestEmbedding {

    static public void main(String[] args) throws Exception{
        Embeddings embObj = new Embeddings();
        embObj.loadEmbedding();
        HashMap<String, double[]> map = embObj.embeddingVectorsMap_c;
        Iterator it = map.entrySet().iterator();
        Map.Entry mpe = (Map.Entry)it.next();
        String k1 = (String) mpe.getKey();
        double[] d1 = embObj.embeddingVectorsMap_c.get(k1);

        mpe = (Map.Entry)it.next();
        String k2 = (String) mpe.getKey();
        double[] d2 = embObj.embeddingVectorsMap_c.get(k2);
        double res = 0.0;
        for (int i = 0; i < d1.length; i++) {
            res += d1[i] * d2[i];
        }
        double res2 = embObj.calculateSimilarity_c(k1, k2);
        Matrix m1 = new Matrix(new double[][]{d1});
        Matrix m2 = new Matrix(new double[][]{d2});
        double res3 = embObj.cosineSimilarity(m1, m2);
        System.out.println(res == res2 &&  res == res3 ? "test1 pass" : "test1 fail");



        ArrayList<WtTable> tb = TableDataReader.loadTableFromResource("/test.json");
        for (WtTable t1 : tb) {
            ArrayList<Mention> mentions = extractMention(t1);
            //W2CSQLTrie trie = ResourceLoader.loadTrie();
            for (Mention mention : mentions) {
                System.out.println("entity:" + "   " + mention.entity.toString());
                for (Candidate c : mention.candidates) {
                    System.out.println("candidate:" + "  " + c.toString());
                    System.out.println("col similarity:" + "    " + Double.toString(embObj.AverageColSimilarity(mention, c, t1)));
                    System.out.println("row similarity:" + "    " + Double.toString(embObj.AverageRowSimilarity(mention, c, t1)));
                    System.out.println("vector similarity:" + "    " + Double.toString(embObj.vectorFeatureOfSubjectColumn(mention, c, t1)));
                }
            }
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
}
