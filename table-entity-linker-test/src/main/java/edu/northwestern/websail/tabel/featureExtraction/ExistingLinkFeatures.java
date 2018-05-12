package edu.northwestern.websail.tabel.featureExtraction;

import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.tabel.text.StanfordNLPTokenizer;
import edu.northwestern.websail.tabel.text.Token;
import edu.northwestern.websail.tabel.text.Tokenizer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This is the Existing Linking Feature in the paper.
 * mentionColIdx: the column of mention, 1 base
 * surfaceAndCandidateTitleInContext: if (same surface, candidate.title) occurs in the context(row and col)
 * isCandidatelinkedDiffSurface: if (different surface, candidate.title) in table
 * candidateTitleColumnTitleOverlap: Jaccard coef of column title and candidate title
 * candidateTitleIsInColTitles: if candidateTitleColumnTitleOverlap > 0
 */
public class ExistingLinkFeatures {

    public static double mentionColIdx(Mention mention) {
        return (double)(mention.cellCol + 1);
    }

    public static Double surfaceAndCandidateTitleInContext(
            Mention mention,
            Candidate candidate,
            WtTable table) {
        int numRow = table.numDataRows;
        int numCol = table.numCols;

        int mentionCol = mention.cellCol;
        int mentionRow = mention.cellRow;
        for (int i=0; i<numRow; i++) {
            if (i == mentionRow) {continue;}

            WikiCell cell = table.tableData[i][mentionCol];
            ArrayList<WikiLink> cellLinks = cell.surfaceLinks;
            for (WikiLink link : cellLinks) {
                // the same surface occurs in table
                if (!(link.surface.equalsIgnoreCase(mention.surfaceForm))) {
                    continue;
                }

                // the same surface uses the same entity
                if (link.target.id == candidate.wikiTitle.id) {
                    return 1.0;
                }
            }
        }

        for (int i=0; i<numCol; i++) {
            if (i == mentionCol) {continue;}

            WikiCell cell = table.tableData[mentionRow][i];
            ArrayList<WikiLink> cellLinks = cell.surfaceLinks;
            for (WikiLink link : cellLinks) {

                // the same surface occurs in table
                if (!(link.surface.equalsIgnoreCase(mention.surfaceForm))) {
                    continue;
                }

                // the same surface uses the same entity
                if (link.target.id == candidate.wikiTitle.id) {
                    return 1.0;
                }
            }
        }

        return 0.0;
    }

    public static Double isCandidatelinkedDiffSurface(Mention mention, Candidate candidate, WtTable table) {
        int numRow = table.numDataRows;
        int numCol = table.numCols;
        for (int i=0; i<numRow; i++)
            for (int j=0; j<numCol; j++) {
                if (i == mention.cellRow && j == mention.cellCol) {
                    continue;
                }

                WikiCell cell = table.tableData[i][j];
                ArrayList<WikiLink> cellLinks = cell.surfaceLinks;
                for (WikiLink link : cellLinks) {

                    // a cell links to the same entity
                    if (link.target.id != candidate.wikiTitle.id) {
                        continue;
                    }

                    // the same entity is linked by different surfaces in table
                    if (!link.surface.equalsIgnoreCase(mention.surfaceForm)) {
                        return 1.0;
                    }
                }
            }
        return 0.0;
    }

    private static List<Token> addAllLinkTitleTokens(ArrayList<WikiLink> links, Tokenizer tokenizer)
            throws IOException {
        List<Token> colLinkTitleTokens = new ArrayList<Token>();
        for (WikiLink link : links) {
            String linkTitle = cleanTitle(link.target.title);
            tokenizer.initialize(linkTitle);
            colLinkTitleTokens.addAll(tokenizer.getAllTokens());
            tokenizer.clear();
        }
        return colLinkTitleTokens;
    }

    private static List<Token> getColumnLinksTitleTokens(Mention mention, Tokenizer tokenizer, WtTable table)
            throws IOException {

        ArrayList<WikiLink> allLinks = new ArrayList<WikiLink>();

        int colIdx = mention.cellCol;
        int rowIdx = mention.cellRow;
        int numHeaderRows = table.numHeaderRows;
        int numRow = table.numDataRows;
        for (int i = 0; i < numHeaderRows; i++) {
            WikiCell cell = table.tableHeaders[i][colIdx];
            allLinks.addAll(cell.surfaceLinks);
        }

        for (int i = 0; i < numRow; i++) {
            if (i == rowIdx)
                continue;
            WikiCell cell = table.tableData[i][colIdx];
            allLinks.addAll(cell.surfaceLinks);
        }

        List<Token> colLinkTitleTokens = addAllLinkTitleTokens(allLinks, tokenizer);
        return colLinkTitleTokens;
    }

    private static HashMap<String, Integer> getTermCount(List<Token> tokens,
                                                        boolean lowercase) {
        HashMap<String, Integer> set = new HashMap<String, Integer>();
        for (Token t : tokens) {
            String key = t.text;
            if(lowercase) key = key.toLowerCase();
            Integer count = set.get(key);
            if (count == null)
                count = 0;
            set.put(key, count + 1);
        }
        return set;
    }

    /**
     * the candidate title is already obtained from the id to title map
     */
    public static double candidateTitleColumnTitleOverlap(
            Mention mention,
            Candidate candidate,
            WtTable table,
            Tokenizer tokenizer) throws IOException {

        String pgTitle = candidate.wikiTitle.title;
        String cleanPgTitle = cleanTitle(pgTitle);
        tokenizer.clear();
        tokenizer.initialize(cleanPgTitle);
        List<Token> titleTokens = tokenizer.getAllTokens();

        List<Token> colTitleTokens = getColumnLinksTitleTokens(mention, tokenizer, table);
        HashMap<String, Integer> colTitleTokenCounts = getTermCount(colTitleTokens, true);
        int totalTokens = colTitleTokens.size();

        int matchCount = 0;
        for (Token t : titleTokens) {
            matchCount += colTitleTokenCounts.containsKey(t.text.toLowerCase()) ?
                    colTitleTokenCounts.get(t.text.toLowerCase()) :
                    0;
        }
        return (double)matchCount / (double)(totalTokens + titleTokens.size());
    }

    public static double candidateTitleIsInColTitles(double similarity) {
        return similarity > 0.0 ? 1.0 : 0.0;
    }


    private static String cleanTitle(String linkTitle) {
        return linkTitle.toLowerCase().replaceAll("_|\\(|\\)", " ");
    }
}