package edu.northwestern.websail.tabel.train;

import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.W2CSQLTrie;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.W2CSQLTrieMaximalMatch;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.candidateComparators.W2CSQLCandidateExternalProbComparator;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.candidateComparators.W2CSQLTrieComparatorImpl;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.ResourceLoader;
import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.tabel.text.StanfordNLPTokenizer;
import edu.northwestern.websail.tabel.text.Token;
import edu.northwestern.websail.tabel.text.Tokenizer;
import edu.northwestern.websail.tabel.utils.TrieResultConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MentionExtractor {
    public static W2CSQLTrieComparatorImpl comparator = new W2CSQLCandidateExternalProbComparator();
    public static Tokenizer tokenizer;
    public static W2CSQLTrie trie;
    public static boolean isDataLoaded = false;
    public static HashMap<Integer, String> idToTitleMap;

    public MentionExtractor() throws Exception {
        if (isDataLoaded)
            return;
        this.isDataLoaded = true;
        this.trie = ResourceLoader.loadTrie();
        this.tokenizer = new StanfordNLPTokenizer();
        this.idToTitleMap = ResourceLoader.loadIdToTitleMap();
    }

    protected List<Token> tokenize(String text) throws IOException {
        tokenizer.initialize(text);
        List<Token> tokens = tokenizer.getAllTokens();
        return tokens;
    }

    public ArrayList<Mention> generateMentionsFromText(String text) throws SQLException, IOException {
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

    public void getTitleForMention (Mention m) {
        for (int i=0; i<m.candidates.size(); i++) {
            Candidate c = m.candidates.get(i);
            c.wikiTitle.title = idToTitleMap.get(c.wikiTitle.id);
        }
    }

    public void getLabelForCandidates (Mention m, int goldId) {
        for (int i=0; i<m.candidates.size(); i++) {
            Candidate c = m.candidates.get(i);
            if (c.wikiTitle.id == goldId) {
                c.label = 1.0;
            } else {
                c.label = 0.0;
            }
        }
    }

    /**
     * Given mention doc, extract mention using trie.
     * set cellRow, col; candidate title, candidate label
     */
    public ArrayList<Mention> extractMentionFromMetionDoc (MentionDoc md) throws Exception {
        String surface = md.surfaceForm;
        int goldId = md.goldAnnotation.titleId;
        ArrayList<Mention> mentions = generateMentionsFromText(surface);
        for (int i=0; i<mentions.size(); i++) {
            Mention m = mentions.get(i);
            m.cellRow = md.cellRow;
            m.cellCol = md.cellCol;
            getTitleForMention(m);
            getLabelForCandidates(m, goldId);
        }

        return mentions;
    }

}
