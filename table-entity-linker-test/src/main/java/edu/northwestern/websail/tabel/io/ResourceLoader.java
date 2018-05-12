package edu.northwestern.websail.tabel.io;

import com.google.gson.Gson;
import edu.northwestern.websail.datastructure.sketch.Sketch;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.W2CSQLTrie;
import edu.northwestern.websail.datastructure.trie.impl.w2cSQL.utils.W2CSQLTrieSerializer;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.model.SketchRAFSummaryManager;
import edu.northwestern.websail.tabel.model.SketchSummaryManager;
import edu.northwestern.websail.utils.MySQLQueryHandler;
import edu.northwestern.websail.wda.model.CorpusStat;
import edu.northwestern.websail.wda.model.CorpusStatLM;
import pulse.util.SemanticRelatedness;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import static edu.northwestern.websail.tabel.config.GlobalConfig.wdaStatDirectory;

public class ResourceLoader {

    public static HashMap<Integer, String> loadIdToTitleMap () throws IOException {
        String idToTitleMap = GlobalConfig.idToTitleMap;
        System.out.println("loading id title map");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(idToTitleMap), "UTF8"));
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        String line;
        while ((line = in.readLine()) != null) {
            String[] parts = line.split("\t");
            map.put(Integer.valueOf(parts[0]), parts[1]);
        }
        in.close();
        System.out.println("finish loading id title map");
        return map;
    }

    public static CorpusStat deserialize(String filename) throws IOException {
        Gson gson = new Gson();
        BufferedReader in= new BufferedReader(new FileReader(filename));
        CorpusStat stat = gson.fromJson(in, CorpusStat.class);
        in.close();
        return stat;
    }

    public static CorpusStatLM wdaCorpusStatDeserializeFromRAF(String dirName,
                                                               String statFileName) throws IOException {

        File dataFile = new File(dirName, statFileName);
        File positionFile = new File(dirName, statFileName + "-positions");
        File metaDataFile = new File(dirName, statFileName + "-metadata");

        BufferedReader in = new BufferedReader(new FileReader(metaDataFile));
        String line;

        Integer totalDocs = 0;
        Integer totalTerms = 0;
        Long totalTermFrequency = 0l;
        String name = "";
        while ((line = in.readLine()) != null) {

            String[] parts = line.split("\t");

            if (parts[0].equalsIgnoreCase("totalDocs")) {
                totalDocs = Integer.valueOf(parts[1]);
            } else if (parts[0].equalsIgnoreCase("totalTerms")) {
                totalTerms = Integer.valueOf(parts[1]);
            } else if (parts[0].equalsIgnoreCase("totalTermFrequency")) {
                totalTermFrequency = Long.valueOf(parts[1]);
            } else if (parts[0].equalsIgnoreCase("name")) {
                name = parts[1];
            }
        }

        CorpusStatLM statObj = new CorpusStatLM(name, totalDocs);
        statObj.setTotalTerms(totalTerms);
        statObj.setTotalTermFrequency(totalTermFrequency);

        in.close();

        in = new BufferedReader(new FileReader(positionFile));
        HashMap<String, Long> keyPositionsMap = new HashMap<String, Long>();
        while ((line = in.readLine()) != null) {
            String[] parts = line.split("\t");
            keyPositionsMap.put(parts[0], Long.valueOf(parts[1]));
        }
        in.close();

        statObj.setKeyPositionsMap(keyPositionsMap);
        statObj.setDataFileName(dataFile.getAbsolutePath());
        return statObj;
    }

    /**
     * load text corpus stats
     */
    public static CorpusStat loadTextCorpusStatRAF() throws Exception {
        CorpusStat stat;
        stat = wdaCorpusStatDeserializeFromRAF(GlobalConfig.RAFTextCorpusStatDirectory, GlobalConfig.wdaStatFilename);
        return stat;
    }

    public static CorpusStat loadTextCorpusStat() throws IOException {
        CorpusStat stat;
        stat = deserialize(GlobalConfig.textCorpusStatPath);
        return stat;
    }

    /**
     * load entity corpus stats
     */
    public static CorpusStat loadEntityCorpusStatRAF() throws Exception {
        CorpusStat stat;
        stat = wdaCorpusStatDeserializeFromRAF(GlobalConfig.RAFEntityCorpusStatDirectory, GlobalConfig.wdaStatFilename);
        return stat;
    }

    public static CorpusStat loadEntityCorpusStat() throws IOException {
        CorpusStat stat;
        stat = deserialize(GlobalConfig.entityCorpusStatPath);
        return stat;
    }

    /**
     * load text sketch data
     */
    public static SketchRAFSummaryManager loadTextContextSkMgr() throws IOException {
        SketchRAFSummaryManager skMgr = SketchRAFSummaryManager.create(GlobalConfig.txtContextLang);
        return skMgr;
    }

    /**
     * load entity sketch data
     */
    public static SketchRAFSummaryManager loadEntityConextSkMgr() throws IOException {
        SketchRAFSummaryManager skMgr = SketchRAFSummaryManager.create(GlobalConfig.entityContextLang);
        return skMgr;
    }

    public static SketchSummaryManager loadSketchSummaryManager()  {
        SketchSummaryManager skMgr = new SketchSummaryManager();
        return skMgr;
    }

    /**
     * load trie data
     */
    public static W2CSQLTrie loadTrie()
            throws IOException, SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        BufferedReader in = new BufferedReader(new FileReader(GlobalConfig.serializedTrie));
        Connection conn = connectTrieDB();
        System.out.println("Reading Trie");
        W2CSQLTrie trie = W2CSQLTrieSerializer.readFromFile(in, conn);
        System.out.println("Finish Reading Trie");
        in.close();
        return trie;
    }

    public static Connection connectTrieDB()
            throws FileNotFoundException, IOException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {

        MySQLQueryHandler handler = new MySQLQueryHandler(
                GlobalConfig.trieDbhost,
                GlobalConfig.trieDbname,
                GlobalConfig.trieUser,
                GlobalConfig.triePassword);
        return handler.getConn();
    }

    /**
     * load semantic relatedness data
     */
    public static SemanticRelatedness loadSRSource() throws Exception {
        SemanticRelatedness sr = new SemanticRelatedness(GlobalConfig.milneWittenFile, GlobalConfig.milneWittenQuantilesFile);
        return sr;
    }
}