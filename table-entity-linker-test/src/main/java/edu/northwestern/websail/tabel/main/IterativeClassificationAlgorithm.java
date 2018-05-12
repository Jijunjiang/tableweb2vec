package edu.northwestern.websail.tabel.main;

import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.model.*;
import edu.northwestern.websail.tabel.train.ModelTraining;
import edu.northwestern.websail.tabel.train.ModelTrainingDataExtractor;
import weka.classifiers.Classifier;
import weka.gui.beans.InteractiveTableModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IterativeClassificationAlgorithm {
    public ModelTraining model;
    public ModelTrainingDataExtractor extractor;
    public int maxIter;

    IterativeClassificationAlgorithm(ModelTraining model, ModelTrainingDataExtractor extractor) {
        this.model = model;
        this.extractor = extractor;
        this.maxIter = GlobalConfig.defaultMaxIter;
    }

    IterativeClassificationAlgorithm(ModelTraining model, ModelTrainingDataExtractor extractor, int maxIter) {
        this.model = model;
        this.extractor = extractor;
        this.maxIter = maxIter;
    }

    public Candidate selectBestCandidateForMention (Mention mention, WtTable table) throws Exception {
        ArrayList<HashMap<String, Double>> features = extractor.getFeatureForOneMention(mention, table);
        Candidate bestCandidate = model.selectBestCandidate(features, mention.candidates);
        return bestCandidate;
    }

    public int fillInEntitiesForTable(WtTable table, ArrayList<Mention> mentions) throws Exception {
        int numEntityChanged = 0;
        // recalculate features and assign entity to mention
        for (int i=0; i<mentions.size(); i++) {
            Mention mention = mentions.get(i);
            Candidate bestCandidate = selectBestCandidateForMention(mention, table);
            if (mention.entity != bestCandidate) {
                numEntityChanged++;
                mention.entity = bestCandidate;
            }
        }

        // reinit table
        table.isTextTokenized = false;
        for (int i=0; i<mentions.size(); i++) {
            Mention m = mentions.get(i);
            int row = m.cellRow;
            int col = m.cellCol;
            table.tableData[row][col].surfaceLinks = new ArrayList<WikiLink>();
        }

        // fill in the table
        for (int i=0; i<mentions.size(); i++) {
            Mention m = mentions.get(i);
            Candidate candidate = m.entity;
            int row = m.cellRow;
            int col = m.cellCol;
            WikiLink link = new WikiLink(
                    m.startOffset,
                    m.endOffset,
                    WikiPageLocationType.MAIN_TABLE,
                    m.surfaceForm,
                    candidate.wikiTitle
            );
            table.tableData[row][col].surfaceLinks.add(link);
        }
        return numEntityChanged;
    }

    public WtTable annotateTable(WtTable table) throws Exception {
        ArrayList<Mention> mentions = extractor.extractMentionsFromTable(table);
        runICA(table, mentions);
        return table;
    }

    public ArrayList<WtTable> annotateTables(ArrayList<WtTable> tables) throws Exception {
        for (int i=0; i<tables.size(); i++) {
            annotateTable(tables.get(i));
        }
        return tables;
    }

    public WtTable runICA (WtTable table, ArrayList<Mention> mentions) throws Exception {
        for (int i=0; i<maxIter; i++) {
            int numMentionChanged = fillInEntitiesForTable(table, mentions);
            if (numMentionChanged == 0) {break;}
        }
        return table;
    }
}
