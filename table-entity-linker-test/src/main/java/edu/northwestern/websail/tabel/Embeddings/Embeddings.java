package edu.northwestern.websail.tabel.Embeddings;
import Jama.Matrix;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.File;
import java.util.HashMap;

/**
 * Created by jijun on 15/05/2018.
 */
public class Embeddings {
    HashMap<String, double[]> embeddingVectorsMap;
    public HashMap<String, double[]> loadEmbedding() {
        try {
            String filePath = "/Users/apple/PycharmProjects/tableweb2vec/table-entity-linker-test/src/main/resources/embeddingTest.json";
            embeddingMapper vectorsMap = new ObjectMapper().readValue(new File(filePath), embeddingMapper.class);
            embeddingVectorsMap = vectorsMap.vectors;
            System.out.println("loading embedding done!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return embeddingVectorsMap;
    }

    public double calculateSimilarity(int id1, int id2) {
        Matrix matrix1 = new Matrix(new double[][]{embeddingVectorsMap.get(Integer.toString(id1))});
        Matrix matrix2 = new Matrix(new double[][]{embeddingVectorsMap.get(Integer.toString(id2))});
        // since the voctor already normalized
        return matrix1.times(matrix2.transpose()).get(0,0);
    }

}
