package edu.northwestern.websail.tabel.config;

public class GlobalConfig {
    public static String rootDataDirectory = "/websail/common/wikification/data/";

    // text and entity language
    public static String txtContextLang = "enTables";
    public static String entityContextLang = "enTablesEntities";

    // ICA
    public static int defaultMaxIter = 50;

    // wda stat corpus config
    public static String wdaStatDirectory = rootDataDirectory + "system/features/wda_data/stat";
    public static String wdaStatFilename = "contexts.stat";
    public static String RAFTextCorpusStatDirectory = wdaStatDirectory
            + "/" + txtContextLang + "/raf/";
    public static String RAFEntityCorpusStatDirectory = wdaStatDirectory
            + "/" + entityContextLang + "/raf/";

    public static String textCorpusStatPath = wdaStatDirectory + "/"
            + "enTables" + "/" + "contexts.stat";
    public static String entityCorpusStatPath = wdaStatDirectory + "/"
            + "enTablesEntities" + "/" + "contexts.stat";

    // trie data config
    public static String serializedTrie = "/websail/common/wikification/data/en/trie/erd/trieSerialized-erd1Jun.dat";
    public static String trieDbhost = "downey-n2.cs.northwestern.edu";
    public static String trieDbname = "erd1Jun";
    public static String trieUser = "kgridUser";
    public static String triePassword = "kgridPwd";
    public static String SemanticRelatednessRootDir = "/websail/common/wikification/data/en/sr/srAllLinks/";
    public static int trieGlobalLimit = 50;
    public static int trieEachLimit = 40; // used as candidate each limit

    // id to title map
    public static String idToTitleMap = "/websail/common/wikification/data/en/en_id_title.map";


    // semantic data config
    public static String milneWittenFile = SemanticRelatednessRootDir + "mwsr.dat";
    public static String milneWittenQuantilesFile = SemanticRelatednessRootDir + "mwsrQuantiles.txt";
    public static int langId = 1;

    // sketch data config
    // TODO: check dir because /context_sketch doesn't exist on server now
    public static String contextSketchSummaryDirectory = rootDataDirectory + "system/features/context_sketch";
    public static String titleSketchSummaryDirectory = rootDataDirectory + "system/features/title_sketch";
    public static String contextSktechMetaDirectory = rootDataDirectory + "system/features/context_meta";
    public static String titleSketchMetaDirectory = rootDataDirectory + "system/features/title_meta";
    // use sketch RAF data on server
    public static String titleSketchSummaryRAFDirectory = rootDataDirectory + "system/features/sk_raf/title_sketch";
    public static String contextSketchSummaryRAFDirectory = rootDataDirectory + "system/features/sk_raf/context_sketch";
    public static String titleSketchMetaRAFDirectory = rootDataDirectory + "system/features/sk_raf/title_meta";
    public static String contextSktechMetaRAFDirectory = rootDataDirectory + "system/features/sk_raf/context_meta";
    public static int sketchDepth = 8; //2^3
    public static int sketchWidth = 524288; //2^14 --> 2^19

    // testing Mention
    public static String testMentionRootDirectory = "/websail/common/wikification/data/webTables/tabel_35k";
    public static String testingMentionDir = testMentionRootDirectory + "/35k_test.ids.txt";
    public static String trainingMentionDir = testMentionRootDirectory + "/model.ids.txt";

    // table and mention doc
    public static String testTablesRootDirectory = "/websail/common/wikification/data/webTables/tables";
    public static String tableMentionsDir = testTablesRootDirectory + "/tableMentions.json";
    public static String tableMentionPos = testTablesRootDirectory + "/tableMentions.pos";
    public static String tableMentionRAF = testTablesRootDirectory + "/tableMentions.raf";
    public static String tablesDir = testTablesRootDirectory + "/tables.json";
    public static String tablesPos = testTablesRootDirectory + "/tables.pos";
    public static String tablesRAF = testTablesRootDirectory + "/tables.raf";

    // experiment data
    public static String modelDataRootDirectory = "/websail/common/wikification/data/webTables/data";
    public static String trainingDataDir = modelDataRootDirectory + "/trainingData.txt";
    public static String testingDataDir = modelDataRootDirectory + "/testingData.txt";

    // subset of table and mention for model testing
    public static String modelDataMention = modelDataRootDirectory + "/modelTableMentions.json";
    public static String modelDataMentionPos = modelDataRootDirectory + "/modelTableMentions.pos";
    public static String modelDataMentionRaf = modelDataRootDirectory + "/modelTableMentions.raf";
    public static String modelDataTables = modelDataRootDirectory + "/modelTables.json";
    public static String modelDataTablesPos = modelDataRootDirectory + "/modelTables.pos";
    public static String modelDataTablesRaf = modelDataRootDirectory + "/modelTables.raf";
}