package edu.northwestern.websail.tabel.model;

import edu.northwestern.websail.tabel.text.Token;
import edu.northwestern.websail.tabel.text.Tokenizer;
import edu.northwestern.websail.wda.model.CorpusStat;
import edu.northwestern.websail.wda.model.TermMeasureStat;

import java.io.IOException;
import java.util.*;

public class TableMentionContext {
    public MaxTFSketch rowTextContextSketch;
    public List<String> rowTextContextStrings;
    public List<Token> rowTextContextTokens;

    public MaxTFSketch colTextContextSketch;
    public List<String> colTextContextStrings;

    public MaxTFSketch fullTextContextSketch;
    public List<String> fullTextContextStrings;

    public MaxTFSketch rowEntityContextSketch;
    public List<String> rowEntityContextStrings;

    public MaxTFSketch colEntityContextSketch;
    public List<String> colEntityContextStrings;

    public MaxTFSketch fullEntityContextSketch;
    public List<String> fullEntityContextStrings;


    public TableMentionContext() {

        rowTextContextStrings = new ArrayList<String>();
        colTextContextStrings = new ArrayList<String>();
        fullTextContextStrings = new ArrayList<String>();

        rowEntityContextStrings = new ArrayList<String>();
        colEntityContextStrings = new ArrayList<String>();
        fullEntityContextStrings = new ArrayList<String>();
    }

    public TableMentionContext(List<Token> rowTextContextTokens, MaxTFSketch rowTextContextSketch, List<String> rowTextContextStrings, MaxTFSketch colTextContextSketch, List<String> colTextContextStrings, MaxTFSketch fullTextContextSketch, List<String> fullTextContextStrings, MaxTFSketch rowEntityContextSketch, List<String> rowEntityContextStrings, MaxTFSketch colEntityContextSketch, List<String> colEntityContextStrings, MaxTFSketch fullEntityContextSketch, List<String> fullEntityContextStrings) {
        this.rowTextContextTokens = rowTextContextTokens;
        this.rowTextContextSketch = rowTextContextSketch;
        this.rowTextContextStrings = rowTextContextStrings;
        this.colTextContextSketch = colTextContextSketch;
        this.colTextContextStrings = colTextContextStrings;
        this.fullTextContextSketch = fullTextContextSketch;
        this.fullTextContextStrings = fullTextContextStrings;
        this.rowEntityContextSketch = rowEntityContextSketch;
        this.rowEntityContextStrings = rowEntityContextStrings;
        this.colEntityContextSketch = colEntityContextSketch;
        this.colEntityContextStrings = colEntityContextStrings;
        this.fullEntityContextSketch = fullEntityContextSketch;
        this.fullEntityContextStrings = fullEntityContextStrings;
    }

    public static List<Token> filterTokensText(ArrayList<Token> allTokens, HashMap<String, Integer> textTokens) {
        List<Token> filteredList = new ArrayList<Token>();
        for (Token t : allTokens) {
            if (!textTokens.containsKey(t.text) || textTokens.get(t.text) == 0) {
                filteredList.add(t);
            } else {
                int initCount = textTokens.get(t.text);
                textTokens.put(t.text, initCount - 1);
            }
        }
        return filteredList;
    }

    public static List<Token> getRowAllTokensExcept(Mention mention, WtTable table) throws IOException {
        int row = mention.cellRow;
        int col = mention.cellCol;
        List<String> tokens = new ArrayList<String>(table.tableData[row][col]
                .textTokens);

        return filterTokensText(table.getRowAllTokens(row), Tokenizer.convertTokensToCountMap(Tokenizer
                .convertStringListToTokens(tokens)));
    }

    public static Collection<? extends Token> convertWikiLinkIDsToTokens(Collection<WikiLink> values) {
        HashSet<Token> idTokens = new HashSet<Token>();
        for (WikiLink wl : values) {
            String idStr = wl.target.id + "";
            idTokens.add(new Token(idStr, 0, idStr.length(), 0));
        }

        return idTokens;
    }

    @SuppressWarnings("unchecked")
    public static List<Token> getRowLinkTokensExcept(Mention mention, WtTable table) {
        int row = mention.cellRow;
        int col = mention.cellCol;
        HashSet<Token> idTokens = (HashSet<Token>) convertWikiLinkIDsToTokens(table.tableData[row][col].surfaceLinks);
        List<Token> tokens = new ArrayList<Token>(idTokens);
        HashMap<String, Integer> idTokenCounts = Tokenizer.convertTokensToCountMap(tokens);
        return filterTokensText(table.getRowLinkTokens(row), idTokenCounts);
    }

    public static List<Token> getColAllTokensExcept(Mention mention, WtTable table) throws IOException {
        int row = mention.cellRow;
        int col = mention.cellCol;
        List<String> tokens = new ArrayList<String>(table.tableData[row][col].textTokens);
        HashMap<String, Integer> tokenCounts = Tokenizer.convertTokensToCountMap(
                Tokenizer.convertStringListToTokens(tokens));
        return filterTokensText(table.getColAllTokens(col), tokenCounts);
    }

    @SuppressWarnings("unchecked")
    public static List<Token>  getColLinkTokensExcept(Mention mention, WtTable table) {
        int row = mention.cellRow;
        int col = mention.cellCol;
        HashSet<Token> idTokens = (HashSet<Token>) convertWikiLinkIDsToTokens(table.tableData[row][col].surfaceLinks);
        List<Token> tokens = new ArrayList<Token>(idTokens);
        return filterTokensText(table.getColLinkTokens(col), Tokenizer.convertTokensToCountMap(tokens));
    }

    public static TableMentionContext getInstance(Mention mention,
                                                  WtTable table, SketchSummaryManager skMgr,
                                                  Tokenizer tokenizer) throws IOException {
        if (!table.isTextTokenized) {
            table.setPerColumnPerRowTokens(tokenizer);
        }

        List<Token> rowTextContextTokens = getRowAllTokensExcept(mention, table);
        List<Token> colTextContextTokens = getColAllTokensExcept(mention, table);
        List<Token> rowEntityTokens = getRowLinkTokensExcept(mention, table);
        List<Token> colEntityTokens = getColLinkTokensExcept(mention, table);

        return getInstance(rowTextContextTokens, colTextContextTokens, rowEntityTokens, colEntityTokens, skMgr);
    }

    private static TableMentionContext getInstance(List<Token> rowTextContextTokens, List<Token> colTextContextTokens,
                                                   List<Token> rowEntityTokens, List<Token> colEntityTokens, SketchSummaryManager skMgr) {

        MaxTFSketch rowTextContextSketch = skMgr.sketch(rowTextContextTokens);
        List<String> rowTextContextStrings = Tokenizer.convertTokensToStringList(rowTextContextTokens);

        MaxTFSketch colTextContextSketch = skMgr.sketch(colTextContextTokens);
        List<String> colTextContextStrings = Tokenizer.convertTokensToStringList(colTextContextTokens);

        List<Token> fullTextContextTokens = new ArrayList<Token>();
        fullTextContextTokens.addAll(rowTextContextTokens);
        fullTextContextTokens.addAll(colTextContextTokens);
        MaxTFSketch fullTextContextSketch = skMgr.sketch(fullTextContextTokens);
        List<String> fullTextContextStrings = Tokenizer.convertTokensToStringList(fullTextContextTokens);


        MaxTFSketch rowEntityContextSketch = skMgr.sketch(rowEntityTokens);
        List<String> rowEntityContextStrings = Tokenizer.convertTokensToStringList(rowEntityTokens);


        MaxTFSketch colEntityContextSketch = skMgr.sketch(colEntityTokens);
        List<String> colEntityContextStrings = Tokenizer.convertTokensToStringList(colEntityTokens);


        List<Token> entityTokens = new ArrayList<Token>();
        entityTokens.addAll(rowEntityTokens);
        entityTokens.addAll(colEntityTokens);
        MaxTFSketch fullEntityContextSketch = skMgr.sketch(entityTokens);
        List<String> fullEntityContextStrings = Tokenizer.convertTokensToStringList(entityTokens);


        return new TableMentionContext(rowTextContextTokens, rowTextContextSketch, rowTextContextStrings, colTextContextSketch,
                colTextContextStrings, fullTextContextSketch, fullTextContextStrings, rowEntityContextSketch,
                rowEntityContextStrings, colEntityContextSketch, colEntityContextStrings, fullEntityContextSketch,
                fullEntityContextStrings);
    }

    public List<String> getUniqueRowEntityStrings() {
        return new ArrayList<String>(new HashSet<String>(rowEntityContextStrings));
    }

    public List<String> getUniqueColEntityStrings() {
        return new ArrayList<String>(new HashSet<String>(colEntityContextStrings));
    }

    public List<String> getUniqueFullEntityStrings() {
        return new ArrayList<String>(new HashSet<String>(fullEntityContextStrings));
    }
}
