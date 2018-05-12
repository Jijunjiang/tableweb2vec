package edu.northwestern.websail.tabel.model;

import edu.northwestern.websail.tabel.text.Token;
import edu.northwestern.websail.tabel.text.Tokenizer;

import java.io.IOException;
import java.util.*;

public class WtTable {
    public String _id;
    public Integer numCols;
    public Integer numDataRows;
    public Integer numHeaderRows;
    HashSet<Integer> numericColumns = new HashSet<Integer>();
    public double order;
    public Integer pgId;
    public String pgTitle;
    public String sectionTitle;
    public String tableCaption;

    public WikiCell[][] tableData;
    public WikiCell[][] tableHeaders;

    public Integer tableId;


    public boolean isTextTokenized;


    ArrayList<Token>[] columnTokens;
    ArrayList<Token>[] columnLinkTokens;
    ArrayList<Token>[] rowTokens;
    ArrayList<Token>[] rowLinkTokens;
    ArrayList<String>[] columnStrings;
    ArrayList<String>[] rowStrings;

    public WtTable() {
        this.isTextTokenized = false;
    }

    public WtTable(Integer pgId, Integer tableId, int numHeaders,
                   int numDataRows, int numCols) {
        super();
        this.pgId = pgId;
        this.tableId = tableId;
        this._id = this.pgId + "-" + this.tableId;
        this.tableData = new WikiCell[numDataRows][numCols];
        this.tableHeaders = new WikiCell[numHeaders][numCols];
        this.numHeaderRows = numHeaders;
        this.numDataRows = numDataRows;
        this.numCols = numCols;
        this.isTextTokenized = false;
    }

    public void initTableData() {
        this.columnTokens = new ArrayList[numCols];
        this.columnLinkTokens = new ArrayList[numCols];
        this.columnStrings = new ArrayList[numCols];

        this.rowTokens = new ArrayList[numDataRows];
        this.rowLinkTokens = new ArrayList[numDataRows];
        this.rowStrings = new ArrayList[numDataRows];
    }

    public static Collection<? extends Token> convertWikiLinkIDsToTokens(Collection<WikiLink> values) {
        HashSet<Token> idTokens = new HashSet<Token>();
        for (WikiLink wl : values) {
            String idStr = wl.target.id + "";
            idTokens.add(new Token(idStr, 0, idStr.length(), 0));
        }

        return idTokens;
    }

    public void setPerColumnPerRowTokens(Tokenizer tokenizer) throws IOException {


        HashMap<Integer, ArrayList<Token>> rowWiseStringTokens = new HashMap<Integer, ArrayList<Token>>();
        HashMap<Integer, ArrayList<String>> rowWiseStrings = new HashMap<Integer, ArrayList<String>>();
        HashMap<Integer, ArrayList<Token>> rowWiseLinkTokens = new HashMap<Integer, ArrayList<Token>>();
        initTableData();

        for (int i = 0; i < numCols; i++) {
            ArrayList<Token> ct = new ArrayList<Token>();
            ArrayList<Token> clt = new ArrayList<Token>();
            ArrayList<String> ctStrings = new ArrayList<String>();
            for (int j = 0; j < numDataRows; j++) {

                String cellText = tableData[j][i].text;
                tokenizer.initialize(cellText.toLowerCase());
                List<Token> cellTokens = tokenizer.getAllTokens();
                HashSet<Token> linkTokens = (HashSet<Token>) convertWikiLinkIDsToTokens(tableData[j][i].surfaceLinks);

                ct.addAll(cellTokens);
                clt.addAll(linkTokens);
                ctStrings.add(cellText);

                if (!rowWiseStringTokens.containsKey(j))
                    rowWiseStringTokens.put(j, new ArrayList<Token>());
                if (!rowWiseStrings.containsKey(j))
                    rowWiseStrings.put(j, new ArrayList<String>());
                if (!rowWiseLinkTokens.containsKey(j))
                    rowWiseLinkTokens.put(j, new ArrayList<Token>());

                rowWiseStringTokens.get(j).addAll(cellTokens);
                rowWiseLinkTokens.get(j).addAll(linkTokens);
                rowWiseStrings.get(j).add(cellText);

                tokenizer.clear();
            }
            for (int j = 0; j < numHeaderRows; j++) {
                ct.add(new Token(tableHeaders[j][i].text, 0, 1, 0));
                clt.addAll(convertWikiLinkIDsToTokens(tableHeaders[j][i].surfaceLinks));
                ctStrings.add(tableHeaders[j][i].text);
            }
            columnTokens[i] = ct;
            columnLinkTokens[i] = clt;
            columnStrings[i] = ctStrings;
        }

        for (int i = 0; i < numDataRows; i++) {
            rowTokens[i] = rowWiseStringTokens.get(i);
            rowLinkTokens[i] = rowWiseLinkTokens.get(i);
            rowStrings[i] = rowWiseStrings.get(i);
        }

        isTextTokenized = true;
    }

    public ArrayList<Token> getRowAllTokens(int rowIdx) {
        return rowTokens[rowIdx];
    }
    public ArrayList<Token> getRowLinkTokens(int rowIdx) {
        return rowLinkTokens[rowIdx];
    }
    public ArrayList<Token> getColAllTokens(int colIdx) {
        return columnTokens[colIdx];
    }
    public ArrayList<Token> getColLinkTokens(int colIdx) {
        return columnLinkTokens[colIdx];
    }

    public String toString() {
        String retStr = "";
        for (int i = 0; i < tableData.length; i++) {
            for (int j = 0; j < tableData[i].length; j++) {

                if (!(tableData[i][j] == null))
                    retStr += tableData[i][j].toString() + "\n";
            }
            retStr = retStr.substring(0, retStr.length());
            retStr += "\n";
        }
        return retStr;
    }
}
