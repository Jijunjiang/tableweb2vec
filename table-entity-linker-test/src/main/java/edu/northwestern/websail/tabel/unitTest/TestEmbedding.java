package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.Embeddings.Embeddings;

import java.util.HashMap;

/**
 * Created by apple on 15/05/2018.
 */


public class TestEmbedding {

    static public void main(String[] args) {
        Embeddings embObj = new Embeddings();
        HashMap<String, double[]> map = embObj.loadEmbedding();
        double[] d1 = map.get("d");
        double[] d2 = map.get("e");
        double res = 0.0;
        for (int i = 0; i < d1.length; i++) {
            res += d1[i] * d2[i];
        }
        double res2 = embObj.calculateSimilarity("d", "e");
        System.out.println(res == res2);
    }
}
