package edu.northwestern.websail.tabel.model;

import edu.northwestern.websail.tabel.utils.CellTextTokenizer;
import edu.northwestern.websail.tabel.utils.TrieTextNormalizer;

import java.util.ArrayList;
import java.util.HashSet;

public class WikiCell {
    public Integer cellID;
    public HashSet<String> textTokens;
    public String text = "";
    public String tdHtmlString = "";
    public ArrayList<WikiLink> surfaceLinks = new ArrayList<WikiLink>();
    public Integer subtableID = -1;
    public Boolean isNumeric = false;
    public Integer wikiReferenceID;
    public ArrayList<String> path;

    //    HashMap<String, WikiLink> predictedSurfaceLinks = new HashMap<String, WikiLink>();

    public WikiCell() {
    }

    public WikiCell(String text) {
        this(-1, text);
    }

    public WikiCell(Integer cellID, String text) {
        super();
        this.cellID = cellID;
        this.text = TrieTextNormalizer.getCleanStr(text);
        this.textTokens = CellTextTokenizer.normalizedText(this.text);
    }

    public String toString() {
        String retStr = "";
        retStr += cellID + "- Candidates=" + surfaceLinks;
        return retStr;
    }
}
