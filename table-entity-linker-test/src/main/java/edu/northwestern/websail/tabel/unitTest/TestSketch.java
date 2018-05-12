package edu.northwestern.websail.tabel.unitTest;

import com.google.gson.Gson;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.ResourceLoader;
import edu.northwestern.websail.tabel.model.MaxTFSketch;
import edu.northwestern.websail.tabel.model.SketchRAFSummaryManager;
import edu.northwestern.websail.wda.model.CorpusStat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// TODO: test when data on server is fixed
public class TestSketch {

    public static void main(String[] args) throws Exception {
        // load sketch data
        SketchRAFSummaryManager textContextSkMgr = ResourceLoader.loadTextContextSkMgr();
        SketchRAFSummaryManager entitiesContextSkMgr = ResourceLoader.loadEntityConextSkMgr();

        // get MaxTFSketch from loaded data
        MaxTFSketch candidateTextSketch;
        MaxTFSketch candidateEntitiesSketch;
        int testingCandidateId = 3611706; // with "title":"Northern_Ireland_general_election,_1929",

        candidateTextSketch = textContextSkMgr
                .loadContextSketch(GlobalConfig.txtContextLang,
                        testingCandidateId);
        candidateEntitiesSketch = entitiesContextSkMgr
                .loadContextSketch(GlobalConfig.entityContextLang,
                        testingCandidateId);

        System.out.println("text sketch term: " + candidateTextSketch.term);
        System.out.println("entity sketch term: " + candidateEntitiesSketch.term);
        System.out.println("text query 1929: " + candidateTextSketch.sketch.query("1929"));
        System.out.println("entity query 1929: " + candidateEntitiesSketch.sketch.query("1929"));

        System.out.println("text query election: " + candidateTextSketch.sketch.query("election"));
        System.out.println("entity query election: " + candidateEntitiesSketch.sketch.query("election"));
    }
}
