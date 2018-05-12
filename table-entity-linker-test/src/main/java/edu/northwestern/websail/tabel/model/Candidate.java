package edu.northwestern.websail.tabel.model;

import edu.northwestern.websail.tabel.featureExtraction.CandidateFeatureName;
import edu.northwestern.websail.tabel.featureExtraction.FeatureValue;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Candidate {
    public static Logger logger = Logger.getLogger(Candidate.class.getName());
    public Mention mention;
    public WikiTitle wikiTitle;
    public HashMap<String, HashMap<String, FeatureValue>> featureGroups;
    public String originalSurface;
    public Double label;

    /**
     * Reference everything in c
     * @param c
     */
    public Candidate(Candidate c){
        this.mention = c.mention;
        this.wikiTitle = c.wikiTitle;
        this.featureGroups = c.featureGroups;
        this.originalSurface = c.originalSurface;
        this.label = 0.0;
    }

    /**
     * Clone feature groups from c, reference everything else but mention which is pointed to m
     * @param c
     * @param m
     *
     */
    public Candidate(Candidate c, Mention m){
        this(c);
        this.mention = m;
        this.featureGroups= new HashMap<String, HashMap<String, FeatureValue>>();
        for(Map.Entry<String, HashMap<String,FeatureValue>> group:c.featureGroups.entrySet()){
            HashMap<String, FeatureValue> feature = new HashMap<String, FeatureValue>();
            this.featureGroups.put(group.getKey(), feature);
            for(Map.Entry<String, FeatureValue> templateFeature:group.getValue().entrySet()){
                feature.put(templateFeature.getKey(), templateFeature.getValue().copy());
            }
        }
    }

    public Candidate(Mention mention, String title, int titleId) {
        this(titleId, title);
        this.mention = mention;
    }

    public Candidate(int titleId, String title) {
        mention = null;
        this.wikiTitle = new WikiTitle(titleId, title);
        this.featureGroups = new HashMap<String, HashMap<String, FeatureValue>>();
    }

    // ========================================================================
    // Feature Value
    // ========================================================================

    public FeatureValue getFeatureValue(String featureName) {
        String[] parts = featureName.split("\\.");
        String group = parts[0];
        String key = parts[1];
        return this.getFeatureValue(featureName, group, key);
    }

    public FeatureValue getFeatureValue(String featureName, String group, String key) {

        if(!this.featureGroups.containsKey(group)){
            //logger.severe("Feature Group missing: " + group);
            return null;
        }

        if(!this.featureGroups.get(group).containsKey(key)){
            //logger.severe("Feature missing: " + featureName);
            return null;
        }
        FeatureValue value = this.featureGroups.get(group).get(key);
        if(featureName.equals(CandidateFeatureName.PRIOR_EXTERNAL_PROB_CASE) ||
                featureName.equals(CandidateFeatureName.PRIOR_INTERNAL_PROB_CASE)){
            Boolean mentionExact = this.getIsMentionExact();
            if(mentionExact!=null){
                int w = mentionExact?1:0;
                value.value = value.value * w;
            }
        }
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
        if(featureName.equals(CandidateFeatureName.PRIOR_EXTERNAL_PROB_CASE) ||
                featureName.equals(CandidateFeatureName.PRIOR_INTERNAL_PROB_CASE)){
            Boolean mentionExact = this.getIsMentionExact();
            if(mentionExact!=null){
                int w = mentionExact?1:0;
                value.value = value.value* w;
            }
        }
        this.featureGroups.get(group).put(key, value);
        if(featureName.equals(CandidateFeatureName.SURFACE_TRIE_SURFACE_MATCH)){
            FeatureValue v = this.getFeatureValue(CandidateFeatureName.PRIOR_INTERNAL_PROB_CASE);
            if(v!=null) {
                this.setFeatureValue(CandidateFeatureName.PRIOR_INTERNAL_PROB_CASE, v);
            }
            v = this.getFeatureValue(CandidateFeatureName.PRIOR_EXTERNAL_PROB_CASE);
            if(v!=null) {
                this.setFeatureValue(CandidateFeatureName.PRIOR_EXTERNAL_PROB_CASE, v);
            }
        }
        return old;
    }

    // ========================================================================
    // Utils
    // ========================================================================

    public boolean isCorrect() {
        if (this.mention == null){
            return false;
        }
        if (this.mention.goldAnnotation == null){
            return false;
        }
        return this.wikiTitle.id == this.mention
                .goldAnnotation.id;
    }

    @Override
    public String toString() {
        return this.wikiTitle.id + ":" + this.wikiTitle.title;
    }

    // ========================================================================
    // Old API Support
    // ========================================================================

    public Double[] getProbabilityFeature() {
        Double[] probs = new Double[6];
        probs[0] = getFeatureValue(CandidateFeatureName.PRIOR_INTERNAL_PROB_CASE)
                .value;
        probs[1] = getFeatureValue(CandidateFeatureName.PRIOR_EXTERNAL_PROB_CASE)
                .value;
        probs[2] = getFeatureValue(CandidateFeatureName.PRIOR_INTERNAL_PROB_NOCASE)
                .value;
        probs[3] = getFeatureValue(CandidateFeatureName.PRIOR_EXTERNAL_PROB_NOCASE)
                .value;
        probs[4] = getFeatureValue(CandidateFeatureName.PRIOR_AVERAGE_CASE).value;
        probs[5] = getFeatureValue(CandidateFeatureName.PRIOR_AVERAGE_NON_CASE)
                .value;
        return probs;
    }

    public void setProbabilityFeatures(Double[] probs) {
        try {
            setFeatureValue(CandidateFeatureName.PRIOR_INTERNAL_PROB_CASE,
                    new FeatureValue(probs[0]));
            setFeatureValue(CandidateFeatureName.PRIOR_EXTERNAL_PROB_CASE,
                    new FeatureValue(probs[1]));
            setFeatureValue(CandidateFeatureName.PRIOR_INTERNAL_PROB_NOCASE,
                    new FeatureValue(probs[2]));
            setFeatureValue(CandidateFeatureName.PRIOR_EXTERNAL_PROB_NOCASE,
                    new FeatureValue(probs[3]));
            setFeatureValue(CandidateFeatureName.PRIOR_AVERAGE_CASE, new FeatureValue(
                    probs[4]));
            setFeatureValue(CandidateFeatureName.PRIOR_AVERAGE_NON_CASE,
                    new FeatureValue(probs[5]));
        } catch (Exception e) {
            logger.severe("Unexpected Error, the probability should have 6 members.");
            e.printStackTrace();
        }
    }

    public void setLinkCountFeatures(Double[] counts) {
        try {
            setFeatureValue(CandidateFeatureName.LINK_COUNT_INTERNAL_MENTION_COUNT_CASE,
                    new FeatureValue(counts[0]));
            setFeatureValue(CandidateFeatureName.LINK_COUNT_EXTERNAL_MENTION_COUNT_CASE,
                    new FeatureValue(counts[1]));
            setFeatureValue(CandidateFeatureName.LINK_COUNT_INTERNAL_MENTION_COUNT_NOCASE,
                    new FeatureValue(counts[2]));
            setFeatureValue(CandidateFeatureName.LINK_COUNT_EXTERNAL_MENTION_COUNT_NOCASE,
                    new FeatureValue(counts[3]));
            setFeatureValue(CandidateFeatureName.LINK_COUNT_INTERNAL_SURFACE_COUNT_CASE,
                    new FeatureValue(counts[4]));
            setFeatureValue(CandidateFeatureName.LINK_COUNT_EXTERNAL_SURFACE_COUNT_CASE,
                    new FeatureValue(counts[5]));
            setFeatureValue(CandidateFeatureName.LINK_COUNT_INTERNAL_SURFACE_COUNT_NOCASE,
                    new FeatureValue(counts[6]));
            setFeatureValue(CandidateFeatureName.LINK_COUNT_EXTERNAL_SURFACE_COUNT_NOCASE,
                    new FeatureValue(counts[7]));
        } catch (Exception e) {
            logger.severe("Unexpected Error, the counts should have 8 members.");
            e.printStackTrace();
        }
    }

    public Boolean getIsTitle() {
        return getFeatureValue(CandidateFeatureName.SURFACE_TITLE_MATCH).value == 1.0;
    }

    public void setIsTitle(Boolean isTitle) {
        try {
            setFeatureValue(CandidateFeatureName.SURFACE_TITLE_MATCH, new FeatureValue(
                    isTitle ? 1.0 : 0.0));
        } catch (Exception e) {
            logger.severe("Unexpected Error when setting feature");
            e.printStackTrace();
        }
    }

    public Boolean getIsMentionExact() {
        FeatureValue value =  getFeatureValue(CandidateFeatureName.SURFACE_TRIE_SURFACE_MATCH);
        if(value == null) return null;
        else return
                value.value == 1.0;
    }

    public void setIsMentionExact(Boolean isMentionExact) {
        try {
            setFeatureValue(CandidateFeatureName.SURFACE_TRIE_SURFACE_MATCH,
                    new FeatureValue(isMentionExact ? 1.0 : 0.0));
        } catch (Exception e) {
            logger.severe("Unexpected Error when setting feature");
            e.printStackTrace();
        }
    }
}