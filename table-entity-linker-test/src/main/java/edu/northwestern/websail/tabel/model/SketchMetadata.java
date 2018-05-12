package edu.northwestern.websail.tabel.model;

import java.io.Serializable;
import java.util.HashSet;

public class SketchMetadata implements Serializable {

    public static final long serialVersionUID = 528002264826289773L;
    public int titleId;
    public HashSet<String> topWords;
    public double l2Norm;
    public double l1Norm;
    public int totalDocuments;

    public SketchMetadata(){
        l2Norm = 1.0;
        l1Norm = 1.0;
        topWords = new HashSet<String>();
        totalDocuments = 0;
    }

    public SketchMetadata(int titleId) {
        l2Norm = 1.0;
        l1Norm = 1.0;
        topWords = new HashSet<String>();
        totalDocuments = 0;
        this.titleId = titleId;
    }
}
