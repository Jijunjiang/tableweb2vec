package edu.northwestern.websail.tabel.featureExtraction;

/**
 * enum type of all feature names
 */
public enum FeatureName {
    isMentionExact("isMentionExact"),
    surfaceTitleMatch("surfaceTitleMatch"),
    mentionColIdx("mentionColIdx"),
    surfaceAndCandidateTitleInContext("surfaceAndCandidateTitleInContext"),
    isCandidatelinkedDiffSurface("isCandidatelinkedDiffSurface"),
    candidateTitleColumnTitleOverlap("candidateTitleColumnTitleOverlap"),
    candidateTitleIsInColTitles("candidateTitleIsInColTitles"),
/*    rowTextSimilarity("rowTextSimilarity"),
    colTextSimilarity("colTextSimilarity"),
    fullTextSimilarity("fullTextSimilarity"),
    rowEntitySimilarity("rowEntitySimilarity"),
    colEntitySimilarity(("colEntitySimilarity")),
    fullEntitySimilarity("fullEntitySimilarity"),*/
    candidatePageSr("candidatePageSr"),
    rowLinksAvgSr("rowLinksAvgSr"),
    colLinksAvgSr("colLinksAvgSr"),
    fullLinksAvgSr("fullLinksAvgSr"),
    rowEmbeddingSimilarity("rowEmbeddingSimilarity"),
    colEmbeddingSimilarity("colEmbeddingSimilarity"),
    subjectColumnRelation("subjectColumnRelation"),


    label("label");

    private String name;

    FeatureName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}