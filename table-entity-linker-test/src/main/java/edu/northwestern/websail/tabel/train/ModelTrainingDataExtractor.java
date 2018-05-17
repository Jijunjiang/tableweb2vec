package edu.northwestern.websail.tabel.train;

import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.W2CSQLTrie;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.W2CSQLTrieMaximalMatch;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.candidateComparators.W2CSQLCandidateExternalProbComparator;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.candidateComparators.W2CSQLTrieComparatorImpl;
import edu.northwestern.websail.tabel.Embeddings.Embeddings;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.featureExtraction.ExistingLinkFeatures;
import edu.northwestern.websail.tabel.featureExtraction.MentionEntitySimilarityFeatures;
import edu.northwestern.websail.tabel.featureExtraction.SemanticRelatednessFeatures;
import edu.northwestern.websail.tabel.featureExtraction.SurfaceFeatures;
import edu.northwestern.websail.tabel.io.ResourceLoader;
import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.tabel.text.StanfordNLPTokenizer;
import edu.northwestern.websail.tabel.text.Token;
import edu.northwestern.websail.tabel.text.Tokenizer;
import edu.northwestern.websail.tabel.utils.TrieResultConverter;
import edu.northwestern.websail.wda.model.CorpusStat;
import pulse.util.SemanticRelatedness;

import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelTrainingDataExtractor {
    public static W2CSQLTrieComparatorImpl comparator = new W2CSQLCandidateExternalProbComparator();
    public static Tokenizer tokenizer;
    public static W2CSQLTrie trie;
    public static CorpusStat textCorpusStat;
    public static CorpusStat entityCorpusStat;
    public static HashMap<Integer, String> idToTitleMap;
    public static SketchRAFSummaryManager textContextSkMgr;
    public static SketchRAFSummaryManager entitiesContextSkMgr;
    public static SketchSummaryManager skMgr;
    public static SemanticRelatedness sr;

    //Embedding
    public static Embeddings embeddings;

    public ModelTrainingDataExtractor() throws Exception {
        this.trie = ResourceLoader.loadTrie();
        this.tokenizer = new StanfordNLPTokenizer();
        this.idToTitleMap = ResourceLoader.loadIdToTitleMap();

        //Embedding
        this.embeddings = new Embeddings();
        embeddings.loadEmbedding();

        // TODO: fix sketch data
        /*
        // load corpus data
        this.textCorpusStat = ResourceLoader.loadTextCorpusStatRAF();
        this.entityCorpusStat = ResourceLoader.loadEntityCorpusStatRAF();

        // load sketch data
        this.textContextSkMgr = ResourceLoader.loadTextContextSkMgr();
        this.entitiesContextSkMgr = ResourceLoader.loadEntityConextSkMgr();*/

        this.skMgr = new SketchSummaryManager();


        // load semantic relatedness data
        this.sr = ResourceLoader.loadSRSource();
    }

    protected List<Token> tokenize(String text) throws IOException {
        tokenizer.initialize(text);
        List<Token> tokens = tokenizer.getAllTokens();
        return tokens;
    }

    private ArrayList<Mention> generateMentionsFromText(String text) throws SQLException, IOException {
        List<Token> tokens = tokenize(text);

        String[] words = Tokenizer.convertTokensToStringArray(tokens);
        if (words.length < 1) return new ArrayList<Mention>();
        ArrayList<W2CSQLTrieMaximalMatch> trieResult =
                trie.getMaximalMatchingCandidates(
                        words,
                        comparator,
                        GlobalConfig.trieEachLimit,
                        GlobalConfig.trieGlobalLimit
                );
        return TrieResultConverter.getMentions(
                trieResult,
                tokens,
                GlobalConfig.trieEachLimit
        );
    }

    private void getTitleForMentions(ArrayList<Mention> mentions) {
        for (int i=0; i<mentions.size(); i++) {
            Mention m = mentions.get(i);
            for (int j=0; j<m.candidates.size(); j++) {
                Candidate c = m.candidates.get(j);
                c.wikiTitle.title = idToTitleMap.get(c.wikiTitle.id);
            }
        }
    }

    private void labelOneCandidate(Mention mention, WikiLink surfaceLink) {
        for (int i=0; i<mention.candidates.size(); i++) {
            Candidate c = mention.candidates.get(i);
            if (c.wikiTitle.id == surfaceLink.target.id) {
                c.label = 1.0;
            } else {
                c.label = 0.0;
            }
        }
    }

    private void labelCandidates(ArrayList<Mention> mentions, ArrayList<WikiLink> surfaceLinks) {
        for (int i=0; i<surfaceLinks.size(); i++) {
            WikiLink link = surfaceLinks.get(i);
            String linkSurface = link.surface;
            for (int j = 0; j < mentions.size(); j++) {
                Mention m = mentions.get(j);
                if (m.surfaceForm.equals(linkSurface)) {
                    labelOneCandidate(m, link);
                }
            }
        }
    }

    public void setMentionsPos(ArrayList<Mention> mentions, int row, int col) {
        for (int i=0; i<mentions.size(); i++) {
            Mention m = mentions.get(i);
            m.cellRow = row;
            m.cellCol = col;
        }
    }

    public ArrayList<Mention> extractMentionsFromTable(WtTable table) throws Exception {
        ArrayList<Mention> mentions = new ArrayList<Mention>();
        int row = table.numDataRows;
        int col = table.numCols;
        for (int i=0; i<row; i++)
            for (int j=0; j<col; j++) {
                WikiCell c = table.tableData[i][j];
                if (c.text == null)
                    continue;

                if (c.surfaceLinks != null && c.surfaceLinks.size() > 0) {
                    ArrayList<Mention> newMentions = generateMentionsFromText(c.text);
                    setMentionsPos(newMentions, i, j);
                    labelCandidates(newMentions, c.surfaceLinks);
                    mentions.addAll(newMentions);
                }
            }
        getTitleForMentions(mentions);
        return mentions;
    }

    public ArrayList<HashMap<String, Double>> getFeaturesFromAllTables(
            ArrayList<WtTable> tables) throws Exception
    {
        ArrayList<HashMap<String, Double>> resultingFeatures = new ArrayList<HashMap<String, Double>>();
        for (int i=0; i<tables.size(); i++) {
            WtTable t = tables.get(i);
            resultingFeatures.addAll(getFeaturesFromOneTable(t));
        }
        return resultingFeatures;
    }

    public ArrayList<HashMap<String, Double>> getFeaturesFromOneTable(WtTable table) throws Exception {
        ArrayList<Mention> mentions = extractMentionsFromTable(table);
        ArrayList<HashMap<String, Double>> resultingFeatures = getAllFeatureValues(mentions, table);
        return resultingFeatures;
    }

    public ArrayList<HashMap<String, Double>> getFeatureForOneMention(
            Mention mention,
            WtTable table) throws Exception
    {
        ArrayList<HashMap<String, Double>> features = new ArrayList<HashMap<String, Double>>();
        TableMentionContext context = TableMentionContext.getInstance(mention, table, skMgr, tokenizer);
        for (int j=0; j<mention.candidates.size(); j++) {
            features.add(generateFeatures(mention, mention.candidates.get(j), table, context));
        }
        return features;
    }

    public ArrayList<HashMap<String, Double>> getAllFeatureValues(
            ArrayList<Mention> mentions,
            WtTable table) throws Exception {
        ArrayList<HashMap<String, Double>> allFeatures = new ArrayList<HashMap<String, Double>>();

        for (int i=0; i<mentions.size(); i++) {
            Mention m = mentions.get(i);
            allFeatures.addAll(getFeatureForOneMention(m, table));
        }
        return allFeatures;
    }

    public static HashMap<String, Double> generateFeatures(
            Mention mention,
            Candidate candidate,
            WtTable table,
            TableMentionContext context) throws IOException, ClassNotFoundException {
        HashMap<String, Double> features = new HashMap<String, Double>();

        // Surface Features
        Double isMentionExact = SurfaceFeatures.isMentionExact(candidate);
        Double surfaceTitleMatch = SurfaceFeatures.surfaceTitleMatch(mention, candidate);
        features.put("isMentionExact", isMentionExact);
        features.put("surfaceTitleMatch", surfaceTitleMatch);

        // Existing Link Features
        Double mentionColIdx = ExistingLinkFeatures.mentionColIdx(mention);
        Double surfaceAndCandidateTitleInContext =
                ExistingLinkFeatures.surfaceAndCandidateTitleInContext(
                        mention,
                        candidate,
                        table
                );
        Double isCandidatelinkedDiffSurface =
                ExistingLinkFeatures.isCandidatelinkedDiffSurface(
                        mention,
                        candidate,
                        table
                );
        Double candidateTitleColumnTitleOverlap =
                ExistingLinkFeatures.candidateTitleColumnTitleOverlap(
                        mention,
                        candidate,
                        table,
                        ModelTrainingDataExtractor.tokenizer
                );
        Double candidateTitleIsInColTitles =
                ExistingLinkFeatures.candidateTitleIsInColTitles(candidateTitleColumnTitleOverlap);
        features.put("mentionColIdx", mentionColIdx);
        features.put("surfaceAndCandidateTitleInContext", surfaceAndCandidateTitleInContext);
        features.put("isCandidatelinkedDiffSurface", isCandidatelinkedDiffSurface);
        features.put("candidateTitleColumnTitleOverlap", candidateTitleColumnTitleOverlap);
        features.put("candidateTitleIsInColTitles", candidateTitleIsInColTitles);

        // Mention Entity Similarity Features
/*        MentionEntitySimilarityFeatures simExtractor =
                new MentionEntitySimilarityFeatures(
                        candidate,
                        textContextSkMgr,
                        entitiesContextSkMgr
                );
        Double rowTextSimilarity = simExtractor.rowTextSimilarity(context, textCorpusStat);
        Double colTextSimilarity = simExtractor.colTextSimilarity(context, textCorpusStat);
        Double fullTextSimilarity = simExtractor.fullTextSimilarity(context, textCorpusStat);
        Double rowEntitySimilarity = simExtractor.rowEntitySimilarity(context, entityCorpusStat);
        Double colEntitySimilarity = simExtractor.colEntitySimilarity(context, entityCorpusStat);
        Double fullEntitySimilarity = simExtractor.fullEntitySimilarity(context, entityCorpusStat);
        features.put("rowTextSimilarity", rowTextSimilarity);
        features.put("colTextSimilarity", colTextSimilarity);
        features.put("fullTextSimilarity", fullTextSimilarity);
        features.put("rowEntitySimilarity", rowEntitySimilarity);
        features.put("colEntitySimilarity", colEntitySimilarity);
        features.put("fullEntitySimilarity", fullEntitySimilarity);*/

        // Semantic Relatedness Features
        SemanticRelatednessFeatures srExtractor = new SemanticRelatednessFeatures();
        Double candidatePageSr = srExtractor.candidatePageSr(candidate, table, sr);
        Double rowLinksAvgSr = srExtractor.rowLinksAvgSr(candidate, context, sr);
        Double colLinksAvgSr = srExtractor.colLinksAvgSr(candidate, context, sr);
        Double fullLinksAvgSr = srExtractor.fullLinksAvgSr(candidate, context, sr);
        features.put("candidatePageSr", candidatePageSr);
        features.put("rowLinksAvgSr", rowLinksAvgSr);
        features.put("colLinksAvgSr", colLinksAvgSr);
        features.put("fullLinksAvgSr", fullLinksAvgSr);

        // label
        features.put("label", candidate.label);

        // embedding features
        // embedding features are features of the average
        // 1. similarity of column embeddings between the current cell and the column context
        // 2. similarity of row embeddings between the current cell and the row context
        // 3. we find the first from left column that is not number and all the cell contains entities, if no such column
        // exist we set it as a constant 0
        features.put("rowEmbeddingSimilarity", embeddings.AverageRowSimilarity(mention, candidate, table));
        features.put("colEmbeddingSimilarity", embeddings.AverageColSimilarity(mention, candidate, table));
        features.put("subjectColumnRelation", embeddings.vectorFeatureOfSubjectColumn(mention,candidate, table));



        return features;
    }

}














