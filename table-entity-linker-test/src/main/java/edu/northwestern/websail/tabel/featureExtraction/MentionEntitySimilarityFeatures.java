package edu.northwestern.websail.tabel.featureExtraction;


import edu.northwestern.websail.datastructure.sketch.Sketch;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.wda.model.CorpusStat;
import edu.northwestern.websail.wda.model.TermMeasureStat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Mention-Entity Similarity Feature in the paper
 * rowTxtSimilarity: Cosine similarity of text between row of the mention,
 * and text of the context-representations of an entity in all tables.
 * colTxtSimilarity: same as rowTxtSimilarity, but for column
 * fullTxtSimilarity: same as rowTxtSimilarity, but for both row and col
 * rowEntitySimiarity: similar to TxtSimilarity,
 * but for calculating similarity for entities in same the row of mention
 * colTxtSimilarity: same as rowEntitySimiarity, but for column
 * fullTxtSimilarity: same as rowEntitySimiarity, but for both row and col
 */
public class MentionEntitySimilarityFeatures {
    MaxTFSketch candidateTextSketch;
    MaxTFSketch candidateEntitiesSketch;

    public MentionEntitySimilarityFeatures(
            Candidate candidate,
            SketchRAFSummaryManager textContextSkMgr,
            SketchRAFSummaryManager entitiesContextSkMgr
    ) throws IOException, ClassNotFoundException {
        candidateTextSketch = textContextSkMgr
                .loadContextSketch(GlobalConfig.txtContextLang,
                        candidate.wikiTitle.id);
        candidateEntitiesSketch = entitiesContextSkMgr
                .loadContextSketch(GlobalConfig.entityContextLang,
                        candidate.wikiTitle.id);
    }

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

    private static double computeSimilarity(
            List<String> termSet,
            MaxTFSketch sk1,
            CorpusStat stat1,
            MaxTFSketch sk2,
            CorpusStat stat2
    ) {
        double n = 0;

        double sum1 = 0;
        double sum2 = 0;
        for (String term : termSet) {
            Double w1 = getTermWeight(term, stat1);
            Double w2 = getTermWeight(term, stat2);
            if (w1 == null)
                w1 = 0.0;
            if (w2 == null)
                w2 = 0.0;
            double tfW1 = ((double) (sk1.sketch.query(term))) * w1;
            double tfW2 = ((double) (sk2.sketch.query(term))) * w2;
            sum1 += tfW1 * tfW1;
            sum2 += tfW2 * tfW2;
            n += tfW1 * tfW2;
        }
        double euDenorm = Math.sqrt(sum1) * Math.sqrt(sum2);
        if (euDenorm == 0)
            return 0.0;
        return n / euDenorm;
    }


    private static double sim(List<String> tokens1, MaxTFSketch sk1, CorpusStat stat1, MaxTFSketch sk2,
                       CorpusStat stat2) {
        return sk1 == null
                || sk2 == null
                || sk1 == null
                || sk2 == null ? -1.0
                : computeSimilarity(
                tokens1, sk1,
                stat1, sk2,
                stat2);
    }


    public double rowTextSimilarity(
            TableMentionContext context,
            CorpusStat textCorpusStat
    ) throws IOException, ClassNotFoundException {
        return sim(
                context.rowTextContextStrings,
                context.rowTextContextSketch,
                textCorpusStat,
                candidateTextSketch, textCorpusStat
        );
    }

    public double colTextSimilarity(TableMentionContext context, CorpusStat textCorpusStat) {
        List<String> colTextContextStrings = context.colTextContextStrings;
        MaxTFSketch colTextContextSketch = context.colTextContextSketch;
        return sim(
                context.colTextContextStrings,
                context.colTextContextSketch,
                textCorpusStat,
                candidateTextSketch,
                textCorpusStat
        );
    }

    public double fullTextSimilarity(TableMentionContext context, CorpusStat textCorpusStat) {
        return sim(
                context.fullTextContextStrings,
                context.fullTextContextSketch,
                textCorpusStat,
                candidateTextSketch,
                textCorpusStat
        );
    }

    public double rowEntitySimilarity(TableMentionContext context, CorpusStat entityCorpusStat) {
        return sim(
                context.rowEntityContextStrings,
                context.rowEntityContextSketch,
                entityCorpusStat,
                candidateTextSketch,
                entityCorpusStat
        );
    }

    public double colEntitySimilarity(TableMentionContext context, CorpusStat entityCorpusStat) {
        return sim(
                context.colEntityContextStrings,
                context.colEntityContextSketch,
                entityCorpusStat,
                candidateTextSketch,
                entityCorpusStat
        );
    }

    public double fullEntitySimilarity(TableMentionContext context, CorpusStat entityCorpusStat) {
        return sim(
                context.fullEntityContextStrings,
                context.fullEntityContextSketch,
                entityCorpusStat,
                candidateTextSketch, entityCorpusStat
        );
    }
}
