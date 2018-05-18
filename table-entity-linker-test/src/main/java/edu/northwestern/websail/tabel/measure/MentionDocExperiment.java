package edu.northwestern.websail.tabel.measure;

import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.InputFileManager;
import edu.northwestern.websail.tabel.train.ModelTraining;
import edu.northwestern.websail.tabel.utils.DataPrinter;

import java.util.ArrayList;
import java.util.HashMap;

public class MentionDocExperiment {
    public ModelTraining model;
    public ArrayList<HashMap<String, Double>> trainingData;
    public ArrayList<HashMap<String, Double>> testingData;

    public MentionDocExperiment(String trainingPath, String testingPath) {
        model = new ModelTraining();
        trainingData = loadModelData(trainingPath);
        testingData = loadModelData(testingPath);
    }

    public ArrayList<HashMap<String, Double>> loadModelData(String path) {
        ArrayList<HashMap<String, Double>> data = new ArrayList<HashMap<String, Double>>();
        InputFileManager in = new InputFileManager(path);
        String line;
        while ((line = in.readLine()) != null) {
            String[] pairs = line.split("\t");
            HashMap<String, Double> f = new HashMap<String, Double>();
            for (int i=0; i<pairs.length; i++) {
                String[] p = pairs[i].split(" ");
                f.put(p[0], Double.parseDouble(p[1].equals("NaN") ? "0.0" : p[1]));
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

        for (int i=0; i<res.length; i++) {
            System.out.println("pred " + res[i] + "\t" + "act " + act[i]);
        }
    }

    public static void main(String[] args) throws Exception {
//        String trainingData = "/Users/ruohongzhang/Desktop/websail/table-entity-linker/training.txt";
//        String testingData = "/Users/ruohongzhang/Desktop/websail/table-entity-linker/testing.txt";
        String trainingData = "/websail/jijun/data/trainingData.txt";
        String testingData = "/websail/jijun/data/testingData.txt";
        MentionDocExperiment exp = new MentionDocExperiment(trainingData, testingData);
        exp.runExperiment();

        /*
        System.out.println(n);
        HashMap<String, Double> f = exp.trainingData.get(0);
        DataPrinter p = new DataPrinter("/Users/ruohongzhang/Desktop/websail/table-entity-linker/out.txt");
        p.printMap(f);
        */
    }
}
