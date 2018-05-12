package edu.northwestern.websail.tabel.featureExtraction;


public class CandidateFeatureName {

    /**
     * Surface Features
     */
    public static final String isMentionExact = "isMentionExact";
    public static final String surfaceTitleMatch = "surfaceTitleMatch";

    /**
     * Existing Link Features
     */
    public static final String mentionColIdx = "mentionColIdx";
    public static final String surfaceAndCandidateTitleInContext = "surfaceAndCandidateTitleInContext";
    public static final String isCandidatelinkedDiffSurface = "isCandidatelinkedDiffSurface";
    public static final String candidateTitleColumnTitleOverlap = "candidateTitleColumnTitleOverlap";
    public static final String candidateTitleIsInColTitles = "candidateTitleIsInColTitles";

    /**
     * Mention Entity Similarity Features
     */
    public static final String rowTextSimilarity = "rowTextSimilarity";
    public static final String colTextSimilarity = "colTextSimilarity";
    public static final String fullTextSimilarity = "fullTextSimilarity";
    public static final String rowEntitySimilarity = "rowEntitySimilarity";
    public static final String colEntitySimilarity = "colEntitySimilarity";
    public static final String fullEntitySimilarity = "fullEntitySimilarity";

    /**
     * Semantic Relatedness Features
     */
    public static final String candidatePageSr = "candidatePageSr";
    public static final String rowLinksAvgSr = "rowLinksAvgSr";
    public static final String colLinksAvgSr = "colLinksAvgSr";
    public static final String fullLinksAvgSr = "fullLinksAvgSr";


    // TODO: remove the unused features
    /**
     * GROUP = "PRIOR"
     */
    public static final String PRIOR_GROUP = "prior";
    public static final String PRIOR_INTERNAL_PROB_CASE = PRIOR_GROUP + ".internal_case";
    public static final String PRIOR_EXTERNAL_PROB_CASE = PRIOR_GROUP + ".external_case";
    public static final String PRIOR_INTERNAL_PROB_NOCASE = PRIOR_GROUP + ".internal_none_case";
    public static final String PRIOR_EXTERNAL_PROB_NOCASE = PRIOR_GROUP + ".external_none_case";
    public static final String PRIOR_AVERAGE_CASE = PRIOR_GROUP + ".average_case";
    public static final String PRIOR_AVERAGE_NON_CASE = PRIOR_GROUP + ".average_none_case";
    public static final String PRIOR_PROB_RANK = PRIOR_GROUP + ".probability_rank";

    /**
     * GROUP = "LOG_PRIOR"
     */
    public static final String LOG_PRIOR_GROUP = "log_prior";
    public static final String LOG_PRIOR_INTERNAL_PROB_CASE = LOG_PRIOR_GROUP + ".internal_case";
    public static final String LOG_PRIOR_EXTERNAL_PROB_CASE = LOG_PRIOR_GROUP + ".external_case";
    public static final String LOG_PRIOR_INTERNAL_PROB_NOCASE = LOG_PRIOR_GROUP + ".internal_none_case";
    public static final String LOG_PRIOR_EXTERNAL_PROB_NOCASE = LOG_PRIOR_GROUP + ".external_none_case";
    public static final String LOG_PRIOR_AVERAGE_CASE = LOG_PRIOR_GROUP + ".average_case";
    public static final String LOG_PRIOR_AVERAGE_NON_CASE = LOG_PRIOR_GROUP + ".average_none_case";

    /**
     * GROUP = "LINK COUNT"
     */

    public static final String LINK_COUNT_GROUP = "link_count";
    public static final String LINK_COUNT_INTERNAL_MENTION_COUNT_CASE = LINK_COUNT_GROUP + ".internal_mention_count_case";
    public static final String LINK_COUNT_EXTERNAL_MENTION_COUNT_CASE = LINK_COUNT_GROUP + ".external_mention_count_case";
    public static final String LINK_COUNT_INTERNAL_MENTION_COUNT_NOCASE = LINK_COUNT_GROUP + ".internal_mention_count_none_case";
    public static final String LINK_COUNT_EXTERNAL_MENTION_COUNT_NOCASE = LINK_COUNT_GROUP + ".external_mention_count_none_case";
    public static final String LINK_COUNT_INTERNAL_SURFACE_COUNT_CASE = LINK_COUNT_GROUP + ".internal_surface_count_case";
    public static final String LINK_COUNT_EXTERNAL_SURFACE_COUNT_CASE = LINK_COUNT_GROUP + ".external_surface_count_case";
    public static final String LINK_COUNT_INTERNAL_SURFACE_COUNT_NOCASE = LINK_COUNT_GROUP + ".internal_surface_count_none_case";
    public static final String LINK_COUNT_EXTERNAL_SURFACE_COUNT_NOCASE = LINK_COUNT_GROUP + ".external_surface_count_none_case";

    /**
     * GROUP = "SURFACE"
     */

    public static final String SURFACE_GROUP = "surface";
    public static final String SURFACE_TITLE_MATCH = SURFACE_GROUP + ".title_match";
    public static final String SURFACE_TRIE_SURFACE_MATCH = SURFACE_GROUP + ".trie_surface_match";
    public static final String SURFACE_TITLE_EDIT_DISTANCE = SURFACE_GROUP + ".editDistance";
    public static final String SURFACE_TITLE_JACCARD_COEFF = SURFACE_GROUP + ".jc";

    /**
     * GROUP = "NGRAM"
     */

    public static final String NGRAM_GROUP = "ngram";
    public static final String NGRAM_NORMALIZED_PROB = NGRAM_GROUP + ".normalized_prob";
    public static final String NGRAM_PROB = NGRAM_GROUP + ".prob";
    public static final String NGRAM_CONCEPT_PROB = NGRAM_GROUP + ".concept_prob";

    public static final String NGRAM_BACKOFF_GROUP = "ngram_backoff";
    public static final String NGRAM_BACKOFF_NORMALIZED_PROB = NGRAM_BACKOFF_GROUP + ".normalized_prob";
    public static final String NGRAM_BACKOFF_PROB = NGRAM_BACKOFF_GROUP + ".prob";
    public static final String NGRAM_BACKOFF_CONCEPT_PROB = NGRAM_BACKOFF_GROUP + ".concept_prob";


    /**
     * GROUP = "SKETCH"
     */
    public static final String SK_GROUP = "sk";
    public static final String SK_T2T = SK_GROUP + ".text2text";
    public static final String SK_T2C = SK_GROUP + ".text2context";
    public static final String SK_C2T = SK_GROUP + ".context2text";
    public static final String SK_C2C = SK_GROUP + ".context2context";
    public static final String SK_TITLE_TOTAL = SK_GROUP + ".totalTitleTermCount";
    public static final String SK_CONTEXT_TOTAL = SK_GROUP + ".totalContextTermCount";

    public static final String SK_TOP_T2T = SK_GROUP + ".topword_text2text";
    public static final String SK_TOP_T2C = SK_GROUP + ".topword_text2context";
    public static final String SK_TOP_C2T = SK_GROUP + ".topword_context2text";
    public static final String SK_TOP_C2C = SK_GROUP + ".topword_context2context";

    public static final String SK_BACKOFF_GROUP = "sk_backoff";
    public static final String SK_BACKOFF_T2T = SK_BACKOFF_GROUP + ".text2text";
    public static final String SK_BACKOFF_T2C = SK_BACKOFF_GROUP + ".text2context";
    public static final String SK_BACKOFF_C2T = SK_BACKOFF_GROUP + ".context2text";
    public static final String SK_BACKOFF_C2C = SK_BACKOFF_GROUP + ".context2context";
    public static final String SK_BACKOFF_TITLE_TOTAL = SK_BACKOFF_GROUP + ".totalTitleTermCount";
    public static final String SK_BACKOFF_CONTEXT_TOTAL = SK_BACKOFF_GROUP + ".totalContextTermCount";

    /**
     * GROUP = "EXISTING_LINK"
     */

    public static final String EXISTING_LINK_GROUP = "existing_link";
    public static final String EXISTING_LINK_ANY = EXISTING_LINK_GROUP + ".any";
    public static final String EXISTING_LINK_BEFORE = EXISTING_LINK_GROUP + ".before";
    public static final String EXISTING_LINK_AFTER = EXISTING_LINK_GROUP + ".after";
    public static final String EXISTING_LINK_DISTANCE_ANY = EXISTING_LINK_GROUP + ".distance_any";
    public static final String EXISTING_LINK_DISTANCE_BEFORE = EXISTING_LINK_GROUP + ".distance_before";
    public static final String EXISTING_LINK_DISTANCE_AFTER = EXISTING_LINK_GROUP + ".distance_after";
    public static final String EXISTING_LINK_PAGE_HAS_LINK = EXISTING_LINK_GROUP + ".pageContainsMatch";
    public static final String EXISTING_LINK_IS_MMUL = EXISTING_LINK_GROUP + ".isMmul";
    public static final String EXISTING_LINK_IS_CONCEPT_EXISTED = EXISTING_LINK_GROUP + ".isConceptExisted";
    public static final String EXISTING_LINK_CANDIDATE_LINKS_BACK = EXISTING_LINK_GROUP + ".isCandidatelinkBack";
    public static final String EXISTING_LINK_CANDIDATE_WITH_DIFFERENT_SURFACE = EXISTING_LINK_GROUP + "" +
            ".isCandidatelinkedDiffSurface";
    public static final String CANDIDATE_TITLE_COLUMN_TITLE_OVERLAP = EXISTING_LINK_GROUP + "" +
            ".candidateTitleColTitleOverlap";
    public static final String CANDIDATE_TITLE_TOKEN_IS_IN_COLUMN_TOKENS = EXISTING_LINK_GROUP + "" +
            ".candidateTitleIsInColTitles";

    /**
     * GROUP = "TABLE CONTEXT"
     */

    public static final String TABLE_GROUP = "tableContext";
    public static final String TABLE_TEXT_CONTEXT_SIMILARITY = TABLE_GROUP + ".txtSim";
    public static final String TABLE_ENTITY_CONTEXT_SIMILARITY = TABLE_GROUP + ".entSim";
    public static final String TABLE_TEXT_COL_CONTEXT_SIMILARITY = TABLE_GROUP + ".colTxtSim";
    public static final String TABLE_ENTITY_COL_CONTEXT_SIMILARITY = TABLE_GROUP + ".colEntSim";
    public static final String TABLE_TEXT_ROW_CONTEXT_SIMILARITY = TABLE_GROUP + ".rowTxtSim";
    public static final String TABLE_TEXT_TITLE_SIMILARITY = TABLE_GROUP + ".rowTxtArticleSim";
    public static final String TABLE_ENTITY_ROW_CONTEXT_SIMILARITY = TABLE_GROUP + ".rowEntSim";
    public static final String TABLE_COLUMN_IDX = TABLE_GROUP + ".mentionColIdx";
    public static final String TABLE_TEXT_CONTEXT_TIME_HIST_SIMILARITY = TABLE_GROUP + ".ctxYearSim";
    public static final String TABLE_TEXT_CONTEXT_YEAR_IN_CANDIDATE_TIME_HIST = TABLE_GROUP + ".ctxYearInArticle";
    public static final String TABLE_TEXT_CONTEXT_CONTAINS_YEAR = TABLE_GROUP + ".ctxYearExists";

    /**
     * GROUP = "SR"
     */
    public static final String SR_GROUP = "sr";
    public static final String CANDIDATE_PAGE_SR = SR_GROUP + ".candidate2page";
    public static final String CANDIDATE_PAGE_SR_AVG = SR_GROUP + ".candidate2pageAvg";
    public static final String CANDIDATE_PAGE_SR_MIN = SR_GROUP + ".candidate2pageMin";
    public static final String CANDIDATE_PAGE_SR_MAX = SR_GROUP + ".candidate2pageMax";
    public static final String CANDIDATE_TABLE_LINKS_SR_AVG = SR_GROUP + ".tableLinksSrAvg";
    public static final String CANDIDATE_TABLE_ROW_LINKS_SR_AVG = SR_GROUP + ".tableRowLinksSrAvg";
    public static final String CANDIDATE_TABLE_COL_LINKS_SR_AVG = SR_GROUP + ".tableColLinksSrAvg";

    /**
     * GROUP = "ONTOLOGY"
     */
    public static final String ONTOLOGY_GROUP = "onto";
    public static final String ONTOLOGY_NODE_PRIOR_INTERNAL_CASE = ONTOLOGY_GROUP + ".internal_case";
    public static final String ONTOLOGY_NODE_PRIOR_INTERNAL_NOCASE = ONTOLOGY_GROUP + ".internal_non_case";
    public static final String ONTOLOGY_NODE_PRIOR_EXTERNAL_CASE = ONTOLOGY_GROUP + ".external_case";
    public static final String ONTOLOGY_NODE_PRIOR_EXTERNAL_NOCASE = ONTOLOGY_GROUP + ".external_non_case";
    public static final String ONTOLOGY_NODE_PRIOR_AVERAGEL_CASE = ONTOLOGY_GROUP + ".avg_case";
    public static final String ONTOLOGY_NODE_PRIOR_AVERAGE_NOCASE = ONTOLOGY_GROUP + ".avg_non_case";
    public static final String ONTOLOGY_MODEL_SCORE = ONTOLOGY_GROUP + ".modelScore";
    public static final String ONTOLOGY_PATH_PROB = ONTOLOGY_GROUP + ".pathProb";
    public static final String ONTOLOGY_PATH_IS_UNKNOWN = ONTOLOGY_GROUP + ".isUnkown";
    public static final String ONTOLOGY_PATH_UNK_SCORE = ONTOLOGY_GROUP + ".unkScore";
    public static final String ONTOLOGY_PATH_ENT_SIM = ONTOLOGY_GROUP + ".entitySim";
    public static final String ONTOLOGY_PATH_TXT_SIM = ONTOLOGY_GROUP + ".textSim";
    public static final String ONTOLOGY_PATH_LENGTH = ONTOLOGY_GROUP + ".length";
    public static final String ONTOLOGY_PATH_LENGTH_SCORE = ONTOLOGY_GROUP + ".lengthScore";

    /**
     * GROUP = "ONTOLOGY_CONTEXT"
     */

    public static final String ONTOLOGY_CONTEXT_GROUP = "ontoContext";
    public static final String ONTOLOGY_CONTEXT_TXT_SIMILARITY = ONTOLOGY_CONTEXT_GROUP + ".txtSim";
    public static final String ONTOLOGY_CONTEXT_ENT_SIMILARITY = ONTOLOGY_CONTEXT_GROUP + ".entSim";


    /**
     * GROUP = "CC"
     */

    public static final String CC_GROUP = "cc";
    public static final String CC_SR_AVG_CONCEPT = CC_GROUP + ".avgSR_conceptExisted";

    /**
     * GROUP = "MEF"
     */
    public static final String MEF_GROUP = "mef";
    public static final String MEF_REFERENCE_MENTION_SOURCE = MEF_GROUP + ".referenceMentionSource";


    /**
     * GROUP = "ie"
     */
    public static final String IE_GROUP = "ie";
    public static final String IE_RANK = IE_GROUP + ".rank";
    public static final String IE_TOP_RANK = IE_GROUP + ".topRank";
    public static final String IE_SCORE = IE_GROUP + ".score";
    public static final String IE_WINNING_SCORE = IE_GROUP + ".winningScore";
}


