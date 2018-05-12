package edu.northwestern.websail.tabel.train;

import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.tabel.utils.DataPrinter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MentionDocDataExtractor {
    MentionDocRAFManager mdMgr;
    TableRAFManager tbMgr;
    MentionExtractor mentionExtractor;
    ModelTrainingDataExtractor featureExtractor;

    MentionDocDataExtractor() throws Exception {
        mdMgr = new MentionDocRAFManager(GlobalConfig.modelDataMentionPos, GlobalConfig.modelDataMentionRaf);
        tbMgr = new TableRAFManager(GlobalConfig.modelDataTablesPos, GlobalConfig.modelDataTablesRaf);
        mentionExtractor = new MentionExtractor();
        featureExtractor = new ModelTrainingDataExtractor();
    }

    public ArrayList<MentionDoc> getMentionDocsFromRAF(String filename) throws Exception {
        ArrayList<String> mentionIds = TableDataReader.loadMentionIDs(filename);
        ArrayList<MentionDoc> mentionDocs = new ArrayList<MentionDoc>();
        for (int i=0; i<mentionIds.size(); i++) {
            String s = mentionIds.get(i);
            MentionDoc md = mdMgr.getMentionDocFromRAF(s);
            mentionDocs.add(md);
        }

        return mentionDocs;
    }

    public WtTable getTableFromDoc (MentionDoc md) throws IOException {
        int pgId = md.pgId;
        int tableId = md.tableId;
        String tableKey = Integer.toString(pgId) + "-" + Integer.toString(tableId);
        WtTable tbl = tbMgr.getTableFromRAF(tableKey);

        return tbl;
    }

    public ArrayList<HashMap<String, Double>> getDataFromOneDoc (MentionDoc md) throws Exception {
        ArrayList<Mention> mentions = mentionExtractor.extractMentionFromMetionDoc(md);
        ArrayList<HashMap<String, Double>> features = new ArrayList<HashMap<String, Double>>();
        WtTable table = getTableFromDoc(md);
        for (int i=0; i<mentions.size(); i++) {
            Mention m = mentions.get(i);
            features.addAll(featureExtractor.getFeatureForOneMention(m, table));
        }
        return features;
    }

    public ArrayList<HashMap<String, Double>> getAllData (ArrayList<MentionDoc> mentionDocs) throws Exception {
        ArrayList<HashMap<String, Double>> features = new ArrayList<HashMap<String, Double>>();
        for (int i = 0; i < mentionDocs.size(); i++) {
            MentionDoc md = mentionDocs.get(i);
            features.addAll(getDataFromOneDoc(md));
            if (i % 1000 == 0) {
                System.out.println("data: " + i/1000 + " k");
            }
        }
        return features;
    }

    public ArrayList<HashMap<String, Double>> getAllDataFromFile (String filename) throws Exception{
        ArrayList<MentionDoc> mentionDocs = getMentionDocsFromRAF(filename);
        //ArrayList<MentionDoc> mentionDocsTest = new ArrayList<MentionDoc>(mentionDocs.subList(0, 2));
        ArrayList<HashMap<String, Double>> features = getAllData(mentionDocs);
        return features;
    }

    public static void main(String[] args) throws Exception {
        MentionDocDataExtractor extractor = new MentionDocDataExtractor();
//        ArrayList<HashMap<String, Double>> features = extractor.getAllDataFromFile(GlobalConfig.trainingMentionDir);
//        DataPrinter p = new DataPrinter(GlobalConfig.trainingDataDir);
//        p.printFeatures(features);
//        p.close();

        ArrayList<HashMap<String, Double>> testingFeatures = extractor.getAllDataFromFile(GlobalConfig.testingMentionDir);
        DataPrinter p = new DataPrinter(GlobalConfig.testingDataDir);
        p.printFeatures(testingFeatures);
        p.close();

        extractor.tbMgr.close();
        extractor.mdMgr.close();
    }
}
