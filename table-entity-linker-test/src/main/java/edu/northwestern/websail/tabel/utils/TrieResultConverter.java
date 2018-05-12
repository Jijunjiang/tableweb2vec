package edu.northwestern.websail.tabel.utils;

import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.W2CSQLCandidate;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.W2CSQLTrieMaximalMatch;
import edu.northwestern.websail.tabel.model.Candidate;
import edu.northwestern.websail.tabel.model.Mention;
import edu.northwestern.websail.tabel.text.Token;
import edu.northwestern.websail.tabel.text.Tokenizer;

import java.util.*;
import java.util.Map.Entry;

public class TrieResultConverter {

    public static ArrayList<Mention> getMentions(
            ArrayList<W2CSQLTrieMaximalMatch> sqlTMMs, List<Token> tokens,
            int candidateEachLimit) {
        ArrayList<Mention> mentions = new ArrayList<Mention>();
        // System.out.println("got "+sqlTMMs.size() + " matches");
        for (W2CSQLTrieMaximalMatch sqlTMM : sqlTMMs) {
            mentions.add(getMention(sqlTMM, tokens, candidateEachLimit));
        }
        return mentions;
    }

    public static Mention getMention(W2CSQLTrieMaximalMatch sqlTMM,
                                     List<Token> tokens, int candidateEachLimit) {
        Token token = getSurfaceFormToken(sqlTMM, tokens);
        Mention m = new Mention(token.text, token.startOffset,
                token.endOffset);
        // System.out.println(m.getSurfaceForm());
        if (candidateEachLimit == 0)
            return m;
        m.candidates = (getCandidates(sqlTMM.getAllCandidates(), m,
                m.surfaceForm, candidateEachLimit, false));
        return m;

    }

    /*
    public static ArrayList<TokenSpan> getTokenSpans(
            ArrayList<W2CSQLTrieMaximalMatch> sqlTMMs, List<Token> tokens) {
        ArrayList<TokenSpan> tsps = new ArrayList<TokenSpan>();
        int index = 0;
        for (W2CSQLTrieMaximalMatch sqlTMM : sqlTMMs) {
            tsps.add(getTokenSpan(sqlTMM, tokens, index++));
        }
        return tsps;
    }

    public static TokenSpan getTokenSpan(W2CSQLTrieMaximalMatch sqlTMM,
                                         List<Token> tokens, int index) {
        List<Token> subTokens = tokens.subList(sqlTMM.getStartIdx(),
                sqlTMM.getEndIdx());
        TokenSpan ts = new TokenSpan(subTokens.get(0).getStartOffset(),
                subTokens.get(subTokens.size() - 1).getEndOffset(), subTokens,
                index);
        return ts;
    }*/

    public static String getSurfaceForm(W2CSQLTrieMaximalMatch sqlTMM,
                                        String[] words) {
        String str = "";
        for (int i = sqlTMM.getStartIdx(); i < sqlTMM.getEndIdx(); i++) {
            str += words[i] + " ";
        }
        return str.substring(0, str.length() - 1);
    }

    public static String getSurfaceForm(W2CSQLTrieMaximalMatch sqlTMM,
                                        List<Token> tokens) {
        return Tokenizer.convertTokensToString(tokens.subList(
                sqlTMM.getStartIdx(), sqlTMM.getEndIdx()));
    }

    public static Token getSurfaceFormToken(W2CSQLTrieMaximalMatch sqlTMM,
                                            List<Token> tokens) {
        List<Token> subTokens = tokens.subList(sqlTMM.getStartIdx(),
                sqlTMM.getEndIdx());
        String surface = Tokenizer.convertTokensToString(subTokens);
        return new Token(surface, subTokens.get(0).startOffset, subTokens
                .get(subTokens.size() - 1).endOffset, subTokens.get(0)
                .index);
    }



    public static ArrayList<Candidate> getCandidates(
            HashMap<String, TreeSet<W2CSQLCandidate>> candidateSet, Mention m,
            String surfaceToken, int eLimit, boolean verbose) {
        ArrayList<Candidate> candidates = new ArrayList<Candidate>();
        if (eLimit < 1)
            return candidates;
        Iterator<Entry<String, TreeSet<W2CSQLCandidate>>> it = candidateSet
                .entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, TreeSet<W2CSQLCandidate>> pairs = it.next();
            boolean isExact = false;
            if (pairs.getKey().equals(surfaceToken)) {
                isExact = true;
            }
            int count = 0;
            if (verbose)
                System.out.print("\t\tset: " + pairs.getKey());
            if (verbose)
                System.out.print(" total: " + pairs.getValue().size());
            for (W2CSQLCandidate c : pairs.getValue()) {
                Candidate candidate = TrieResultConverter.parseW2CSQLCandidate(
                        c, m, isExact, "", pairs.getKey());
                if (candidate == null)
                    continue;
                // candidates.add(candidate);
                TrieResultConverter.addUniqueCandidate(candidate, candidates);
                if (count >= eLimit && eLimit != -1)
                    break;
                count++;
            }
            if (verbose)
                System.out.println(" parsable(max " + eLimit + "): " + count
                        + " candidates");
        }
        return candidates;
    }

    public static Candidate parseW2CSQLCandidate(W2CSQLCandidate c, Mention m,
                                                 boolean isExact, String title, String trieSurface) {
        Candidate candidate = new Candidate(m, title, c.getConceptId());
        candidate.originalSurface = trieSurface;
        candidate.setIsMentionExact(isExact);
        candidate.setIsTitle(c.getIsTitle());
        Double exMentionCount = (c.getProbExternal() * c.getDenomExternal());
        Double inMentionCount = (c.getProbInternal() * c.getDenomInternal());
        Double exMentionNCCount = (c.getProbExternalNonCase() * c
                .getDenomExternalNonCase());
        Double inMentionNCCount = (c.getProbInternalNonCase() * c
                .getDenomInternalNonCase());
        Double[] probs = new Double[6];
        probs[0] = c.getProbInternal();
        probs[1] = c.getProbExternal();
        probs[2] = c.getProbInternalNonCase();
        probs[3] = c.getProbExternalNonCase();
        probs[4] = c.getProbExternal() * exMentionCount + c.getProbInternal()
                * inMentionCount;
        probs[4] = probs[4] / (exMentionCount + inMentionCount);
        probs[5] = c.getProbExternalNonCase() * exMentionNCCount
                + c.getProbInternalNonCase() * inMentionNCCount;
        probs[5] = probs[5] / (exMentionNCCount + inMentionNCCount);
        candidate.setProbabilityFeatures(probs);

        Double[] counts = new Double[8];
        counts[0] = inMentionCount;
        counts[1] = exMentionCount;
        counts[2] = inMentionNCCount;
        counts[3] = exMentionNCCount;
        counts[4] = c.getDenomInternal() * 1.0;
        counts[5] = c.getDenomExternal() * 1.0;
        counts[6] = c.getDenomInternalNonCase() * 1.0;
        counts[7] = c.getDenomExternalNonCase() * 1.0;
        candidate.setLinkCountFeatures(counts);

        return candidate;
    }

    public static void addUniqueCandidate(Candidate c,
                                          List<Candidate> candidates) {
        Candidate cExist = null;
        for (Candidate iC : candidates) {
            if (iC.wikiTitle.id == c.wikiTitle.id) {
                cExist = iC;
            }
        }
        if (cExist == null) {
            candidates.add(c);
            return;
        }
        if (c.getIsMentionExact() && !cExist.getIsMentionExact()) {
            candidates.remove(cExist);
            candidates.add(c);
            return;
        }
        if (!c.getIsMentionExact() && !cExist.getIsMentionExact()) {
            if (c.getProbabilityFeature()[0] + c.getProbabilityFeature()[1] > cExist
                    .getProbabilityFeature()[0]
                    + cExist.getProbabilityFeature()[1]) {
                candidates.remove(cExist);
                candidates.add(c);
                return;
            }
        }
    }
}