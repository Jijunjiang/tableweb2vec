package edu.northwestern.websail.tabel.unitTest;

import Jama.Matrix;
import edu.northwestern.websail.tabel.Embeddings.Embeddings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by apple on 15/05/2018.
 */


public class TestEmbedding {

    static public void main(String[] args) {
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

    }
}
