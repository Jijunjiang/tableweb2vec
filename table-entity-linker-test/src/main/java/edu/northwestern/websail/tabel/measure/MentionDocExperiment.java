package edu.northwestern.websail.tabel.measure;

import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.InputFileManager;
import edu.northwestern.websail.tabel.train.ModelTraining;
import edu.northwestern.websail.tabel.utils.DataPrinter;

import java.util.*;

public class MentionDocExperiment {
    public ModelTraining model;
    public ArrayList<HashMap<String, Double>> trainingData;
    public ArrayList<HashMap<String, Double>> testingData;

    public MentionDocExperiment() {
    }

    public void loadData(String trainingPath, String testingPath, Set<String> features) {
        model = new ModelTraining();
        trainingData = loadModelData(trainingPath, features);
        testingData = loadModelData(testingPath, features);
    }



    public ArrayList<HashMap<String, Double>> loadModelData(String path, Set<String> features) {
        ArrayList<HashMap<String, Double>> data = new ArrayList<HashMap<String, Double>>();
        InputFileManager in = new InputFileManager(path);
        String line;
        while ((line = in.readLine()) != null) {
            String[] pairs = line.split("\t");
            HashMap<String, Double> f = new HashMap<String, Double>();
            for (int i=0; i<pairs.length; i++) {
                String[] p = pairs[i].split(" ");
                if (features.contains(p[0])) f.put(p[0], Double.parseDouble(p[1].equals("NaN") ? "0.0" : p[1]));
            }
            data.add(f);
        }
        return data;
    }

    public void runExperiment() throws Exception {
        model.train(trainingData);
        double[] res = model.predict(testingData);
        double[] act = new double[res.length];
        for (int i=0; i<res.length; i++) {
            double result = res[i];
            HashMap<String, Double> tmp = testingData.get(i);
            double tmpa = tmp.get("label");
            act[i] = tmpa;
        }
        System.out.println("accuracy: " + Measure.accuracy(res, act));
        System.out.println("f1: " + Measure.f1(res, act));
        System.out.println("f1 negative: " + Measure.f1Negative(res, act));

//        for (int i=0; i<res.length; i++) {
//            System.out.println("pred " + res[i] + "\t" + "act " + act[i]);
//        }
    }

    public static void main(String[] args) throws Exception {
//        String trainingData = "/Users/ruohongzhang/Desktop/websail/table-entity-linker/training.txt";
//        String testingData = "/Users/ruohongzhang/Desktop/websail/table-entity-linker/testing.txt";
        String[] array = new String[] {"label",
                "isMentionExact",
                "surfaceTitleMatch",
                "mentionColIdx",
                "surfaceAndCandidateTitleInContext",
                "isCandidatelinkedDiffSurface",
                "candidateTitleColumnTitleOverlap",
                "candidateTitleIsInColTitles",
                "candidatePageSr",
                "rowLinksAvgSr",
                "colLinksAvgSr",
                "fullLinksAvgSr",
                "rowEmbeddingSimilarity",
                "colEmbeddingSimilarity",
                "subjectColumnRelation"};

        List<String> basicGroup = Arrays.asList(array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7]);
        List<String> srGroup = Arrays.asList(array[8], array[9], array[10], array[11]);
        List<String> embedGroup = Arrays.asList(array[12], array[13], array[14]);

        String trainingData = "/websail/jijun/data/trainingData600.txt";
        String testingData = "/websail/jijun/data/testingData600.txt";


//        System.out.println("basic with  basic");
//        Set<String> features = new HashSet<String>();
//        features.addAll(basicGroup);
//        MentionDocExperiment exp = new MentionDocExperiment();
//        exp.loadData(trainingData, testingData, features);
//        exp.runExperiment();
//
//        System.out.println("basic with SR");
//        features.addAll(srGroup);
//        exp.loadData(trainingData, testingData, features);
//        exp.runExperiment();
//
//
//        System.out.println("basic with embedding");
//        features.removeAll(srGroup);
//        features.addAll(embedGroup);
//        exp.loadData(trainingData, testingData, features);
//        exp.runExperiment();
//
//        System.out.println("all in");
//        features.addAll(srGroup);
//        exp.loadData(trainingData, testingData, features);
//        exp.runExperiment();
//        features.addAll(srGroup);
//
//
//        features.removeAll(embedGroup);
//        System.out.println("add relational");
//        features.add(array[14]);
//        exp.loadData(trainingData, testingData, features);
//        exp.runExperiment();
//
//        System.out.println("add col");
//        features.remove(array[14]);
//        features.add(array[13]);
//        exp.loadData(trainingData, testingData, features);
//        exp.runExperiment();
//
//
//        System.out.println("add row");
//        features.remove(array[13]);
//        features.add(array[12]);
//        exp.loadData(trainingData, testingData, features);
//        exp.runExperiment();

        /*
        System.out.println(n);
        HashMap<String, Double> f = exp.trainingData.get(0);
        DataPrinter p = new DataPrinter("/Users/ruohongzhang/Desktop/websail/table-entity-linker/out.txt");
        p.printMap(f);
        */

        System.out.println("basic with  basic");
        Set<String> features = new HashSet<String>();
        features.add("label");
        features.addAll(embedGroup);
        MentionDocExperiment exp = new MentionDocExperiment();
        exp.loadData(trainingData, testingData, features);
        exp.runExperiment();
    }
}
