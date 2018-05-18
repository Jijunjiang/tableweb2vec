package edu.northwestern.websail.tabel.Embeddings;
import Jama.Matrix;
import edu.northwestern.websail.tabel.model.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.util.HashMap;

/**
 * Created by jijun on 15/05/2018.
 */
public class Embeddings {

    public HashMap<String, double[]> embeddingVectorsMap_c;
    public HashMap<String, double[]> embeddingVectorsMap_r;
    public void loadEmbedding() {
        try {
            String filePath = "/websail/jijun/data/embeddings/";
            System.out.println("loading embedding...");
            embeddingMapper vectorsMap_c = new ObjectMapper().readValue(new File(filePath + "embedding_c.json"), embeddingMapper.class);
            embeddingMapper vectorsMap_r = new ObjectMapper().readValue(new File(filePath + "embedding_r.json"), embeddingMapper.class);
            embeddingVectorsMap_c = vectorsMap_c.vectors;
            embeddingVectorsMap_r = vectorsMap_r.vectors;
            System.out.println("loading embedding done!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double calculateSimilarity_c(String id1, String id2) {
        Matrix matrix1 = new Matrix(new double[][]{embeddingVectorsMap_c.get(id1)});
        Matrix matrix2 = new Matrix(new double[][]{embeddingVectorsMap_c.get(id2)});
        // since the voctor already normalized
        return matrix1.times(matrix2.transpose()).get(0,0);
    }

    public double calculateSimilarity_r(String id1, String id2) {
        Matrix matrix1 = new Matrix(new double[][]{embeddingVectorsMap_r.get(id1)});
        Matrix matrix2 = new Matrix(new double[][]{embeddingVectorsMap_r.get(id2)});
        // since the voctor already normalized
        return matrix1.times(matrix2.transpose()).get(0,0);
    }

    public double cosineSimilarity(Matrix m1, Matrix m2) {
        return (m1.times(m2.transpose()).get(0, 0)) /
                (Math.sqrt(m1.times(m1.transpose()).get(0, 0)) *  Math.sqrt(m2.times(m2.transpose()).get(0, 0)));
    }

    public double AverageRowSimilarity(Mention mention, Candidate candidate, WtTable table) {
        int col = mention.cellCol;
        int row = mention.cellRow;
        double rowSimilarity = 0.0;
        int numOfEntities = 0;
        String candidateID= Integer.toString(candidate.wikiTitle.id);
        // col feature

        for (int j = 0; j < table.numCols; j++) {
            if (j != col) {
                WikiCell cell = table.tableData[row][j];
                for (WikiLink link : cell.surfaceLinks) {
                    if (link.target.id != -1) {
                        String key = Integer.toString(link.target.id);
                        double[] v = embeddingVectorsMap_c.get(key);
                        if (v == null) continue;
                        rowSimilarity += calculateSimilarity_c(candidateID, key);
                        numOfEntities++;
                    }
                }
            }
        }
        return rowSimilarity / numOfEntities;
    }

    public double AverageColSimilarity(Mention mention, Candidate candidate, WtTable table) {
        int col = mention.cellCol;
        int row = mention.cellRow;
        double colSimilarity = 0.0;
        int numOfEntities = 0;
        String candidateID= Integer.toString(candidate.wikiTitle.id);
        for (int i = 0; i < table.numDataRows; i++) {
            if (i != row) {
                WikiCell cell = table.tableData[i][col];
                for (WikiLink link : cell.surfaceLinks) {
                    if (link.target.id != -1) {
                        String key = Integer.toString(link.target.id);
                        double[] v = embeddingVectorsMap_c.get(key);
                        if (v == null) continue;
                        colSimilarity += calculateSimilarity_c(candidateID, key);
                        numOfEntities++;
                    }
                }
            }
        }
        return colSimilarity / numOfEntities;
    }

    private int getSubjectColumn(WtTable table, int curColumn) {
        int maxIndex = -1;
        int maxValue = 0;
        for (int j = 0; j < table.numCols; j++) {
            int count = 0;
            if (j == curColumn) continue;
            for (int i = 0; i < table.numDataRows; i++) {
                WikiCell cell = table.tableData[i][j];
                for (WikiLink link : cell.surfaceLinks) {
                    if (link.target.id != -1) {
                        count++;
                        break;
                    }
                }
            }
            if (count >= table.numDataRows) {
                return j;
            }
            if (count > maxValue) {
                maxIndex = j;
                maxValue = count;
            }
        }
        return maxIndex;
    }

    public double vectorFeatureOfSubjectColumn(Mention mention, Candidate candidate, WtTable table) {
        int col = mention.cellCol;
        int row = mention.cellRow;
        int subjectColumn = getSubjectColumn(table, mention.cellCol);
        if (subjectColumn == -1) return 0;
        // calculate similarity between cur_row(sub-column - candi-column) and other_row(sub-column - candi-column)

        double[] candiVector = embeddingVectorsMap_r.get(Integer.toString(candidate.wikiTitle.id));
        if (candiVector == null) return 0;
        Matrix v1 = new Matrix( new double[][]{candiVector});

        String key = Integer.toString(table.tableData[row][subjectColumn].surfaceLinks.get(0).target.id);
        double[] candi_sub_vector = embeddingVectorsMap_r.get(key);
        if (candi_sub_vector == null) return 0;
        Matrix v2 = new Matrix( new double[][] {candi_sub_vector});
        Matrix candidateVector = v2.minus(v1);

        double similarity = 0;
        int count = 0;
        for (int i = 0; i < table.numDataRows; i++) {
            if (i != row) {
                if (table.tableData[i][subjectColumn].surfaceLinks.size() == 0 ||
                        table.tableData[i][col].surfaceLinks.size() == 0) continue;
                String keyID1 = Integer.toString(table.tableData[i][subjectColumn].surfaceLinks.get(0).target.id);
                double[] ID1_m = embeddingVectorsMap_r.get(keyID1);
                if (ID1_m == null) continue;
                Matrix m_sub = new Matrix(new double[][]{ID1_m});

                String keyID2 = Integer.toString(table.tableData[i][col].surfaceLinks.get(0).target.id);
                double[] ID2_m = embeddingVectorsMap_r.get(keyID2);
                if (ID2_m == null) continue;
                Matrix m_canRow = new Matrix(new double[][]{ID2_m});

                Matrix m_reference = m_sub.minus(m_canRow);
                similarity += cosineSimilarity(candidateVector, m_reference);
                count++;
            }
        }
        return similarity / count;
    }


}
