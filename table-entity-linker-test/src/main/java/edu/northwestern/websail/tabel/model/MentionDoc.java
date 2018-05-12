package edu.northwestern.websail.tabel.model;

import org.ietf.jgss.Oid;

import java.util.ArrayList;

public class MentionDoc {
    public ObjectId _id;
    public ArrayList<Candidate> candidates;
    public int cellRow;
    public int cellCol;
    public int startOffset;
    public int endOffset;
    public GoldAnnotation goldAnnotation;
    public boolean isTest;
    public double order;
    public int pgId;
    public String pgTitle;
    public String surfaceForm;
    public int tableId;
}

