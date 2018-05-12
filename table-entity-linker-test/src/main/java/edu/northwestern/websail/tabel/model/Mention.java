package edu.northwestern.websail.tabel.model;

import edu.northwestern.websail.tabel.featureExtraction.FeatureValue;

import java.util.ArrayList;
import java.util.HashMap;

public class Mention {
    public Integer cellRow;
    public Integer cellCol;
    public String surfaceForm;
    public int startOffset;
    public int endOffset; // exclusion
    public WikiTitle goldAnnotation = null;
    public ArrayList<Candidate> candidates;
    public HashMap<String, HashMap<String, FeatureValue>> featureGroups;
    public Candidate entity = null;
    Boolean goldLinkLabel;
    // private ArrayList<Candidate> rankedCandidates;

    public Mention() {
        featureGroups = new HashMap<String, HashMap<String, FeatureValue>>();
    }

    public Mention(String surfaceForm, int startOffset, int endOffset) {
        this();
        this.endOffset = endOffset;
        this.startOffset = startOffset;
        this.surfaceForm = surfaceForm;
        candidates = new ArrayList<Candidate>();
    }

    public Mention(String surfaceForm, int startOffset, int endOffset,
                   WikiTitle goldAnnotation) {
        this(surfaceForm, startOffset, endOffset);
        this.goldAnnotation = goldAnnotation;
    }

    @Override
    public String toString() {
        return this.surfaceForm + " (" + this.startOffset + "-"
                + this.endOffset + ")" + candidates;
    }

    // ========================================================================
    // Feature Value
    // ========================================================================

    public FeatureValue getFeatureValue(String featureName) {
        String[] parts = featureName.split("\\.");
        String group = parts[0];
        String key = parts[1];
        if (!this.featureGroups.containsKey(group)) {
            return null;
        }

        if (!this.featureGroups.get(group).containsKey(key)) {
            return null;
        }
        FeatureValue value = this.featureGroups.get(group).get(key);
        return value;
    }

    public FeatureValue setFeatureValue(String featureName, FeatureValue value)
            throws Exception {
        String[] parts = featureName.split("\\.");
        String group = parts[0];
        String key = parts[1];
        if (parts.length != 2) {
            throw new Exception(
                    "Feature name should be in a format of <group>.<key>");
        }
        if (!this.featureGroups.containsKey(group)) {
            this.featureGroups.put(group, new HashMap<String, FeatureValue>());
        }
        FeatureValue old = this.featureGroups.get(group).get(key);
        this.featureGroups.get(group).put(key, value);
        return old;
    }
}
