package edu.northwestern.websail.tabel.model;

import edu.northwestern.websail.tabel.featureExtraction.FeatureName;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static edu.northwestern.websail.tabel.featureExtraction.CandidateFeatureName.candidatePageSr;

public class Feature {
    /*
    public boolean isSRFeatureEnabled;
    public boolean isSimilarityFeatureEnabled;
    public boolean isSurfaceFeatureEnable;
    public boolean isLinkFeatureEnable;

    public HashSet<String> featureNames;

    public Feature(
            boolean isSRFeatureEnabled,
            boolean isSimilarityFeatureEnabled,
            boolean isSurfaceFeatureEnable,
            boolean isLinkFeatureEnable) {
        this.isSRFeatureEnabled = isSRFeatureEnabled;
        this.isSimilarityFeatureEnabled = isSimilarityFeatureEnabled;
        this.isSurfaceFeatureEnable = isSurfaceFeatureEnable;
        this.isLinkFeatureEnable = isLinkFeatureEnable;

        featureNames = new HashSet<String>();
        if (isSRFeatureEnabled) {
            featureNames.add(FeatureName.candidatePageSr.getName());
            featureNames.add(FeatureName.rowLinksAvgSr.getName());
            featureNames.add(FeatureName.colLinksAvgSr.getName());
            featureNames.add(FeatureName.fullLinksAvgSr.getName());
        }
        if (isSurfaceFeatureEnable) {
            featureNames.add(FeatureName.isMentionExact.getName());
            featureNames.add(FeatureName.surfaceTitleMatch.getName());
        }
        if (isLinkFeatureEnable) {
            featureNames.add(FeatureName.mentionColIdx.getName());
            featureNames.add(FeatureName.surfaceAndCandidateTitleInContext.getName());
            featureNames.add(FeatureName.isCandidatelinkedDiffSurface.getName());
            featureNames.add(FeatureName.candidateTitleColumnTitleOverlap.getName());
            featureNames.add(FeatureName.candidateTitleIsInColTitles.getName());
        }
        if (isSimilarityFeatureEnabled) {
            featureNames.add(FeatureName.rowTextSimilarity.getName());
            featureNames.add(FeatureName.colTextSimilarity.getName());
            featureNames.add(FeatureName.fullTextSimilarity.getName());
            featureNames.add(FeatureName.rowEntitySimilarity.getName());
            featureNames.add(FeatureName.colEntitySimilarity.getName());
            featureNames.add(FeatureName.fullEntitySimilarity.getName());
        }
    }

    HashMap<String, Double> features;
    public static ArrayList<Attribute> featureTypes;

    Feature() {
        featureTypes = getFeatureTypes();
    }

    public ArrayList<Attribute> getFeatureTypes() {
        ArrayList<Attribute> allFeatures = new ArrayList<Attribute>();
        for (FeatureName feature : FeatureName.values()) {
            Attribute f = new Attribute(feature.getName());
            allFeatures.add(f);
        }

        return allFeatures;
    }
    */
}
