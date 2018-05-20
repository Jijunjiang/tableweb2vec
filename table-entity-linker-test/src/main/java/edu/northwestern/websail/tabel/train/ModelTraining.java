package edu.northwestern.websail.tabel.train;

import edu.northwestern.websail.tabel.featureExtraction.FeatureName;
import edu.northwestern.websail.tabel.model.Candidate;
import edu.northwestern.websail.tabel.model.WtTable;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.Logistic;
import weka.core.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelTraining {
    public static ArrayList<Attribute> featureTypes;
    public static Classifier localClassifierModel;

    public ModelTraining() {
        featureTypes = getFeatureTypes();
        localClassifierModel = new Logistic();
    }

    public ArrayList<Attribute> getFeatureTypes() {
        ArrayList<Attribute> allFeatures = new ArrayList<Attribute>();
        for (FeatureName feature : FeatureName.values()) {
            if (feature.getName() == "label")
                continue;
            Attribute f = new Attribute(feature.getName());
            allFeatures.add(f);
        }
        ArrayList targetValues = new ArrayList();
        targetValues.add("true");
        targetValues.add("false");
        Attribute target = new Attribute("label", targetValues);
        allFeatures.add(target);

        return allFeatures;
    }

    public Classifier getModel(Instances dataSet) throws Exception {
        Classifier cModel = new Logistic();
        cModel.buildClassifier(dataSet);
        return cModel;
    }

    public void testModel(Classifier cModel, Instances dataSet, Instances testSet) throws Exception {
        Evaluation eTest = new Evaluation(dataSet);
        eTest.evaluateModel(cModel, testSet);
        String strSummary = eTest.toSummaryString();
        System.out.println(strSummary);
    }

    /**
     * creating a weka instance from the feature values
     * used by function buildDataSet(), which is a set of instance
     */
    public Instance setFeatureForSingleInstance(HashMap<String, Double> feature, boolean isTraining) {
        Instance example = new DenseInstance(this.featureTypes.size());
        for (int i = 0; i < this.featureTypes.size()-1; i++) {
            Attribute att_i = this.featureTypes.get(i);
            Double value = feature.get(att_i.name());
            if (value != null) example.setValue(att_i, value);
        }

        Attribute label = featureTypes.get(this.featureTypes.size()-1);

        if (true) {
            if (feature.get("label") == 1.0) {
                example.setValue(label, "true");
            } else {
                example.setValue(label, "false");
            }
        } else {
            example.setValue(label, "false");
        }
        return example;
    }

    public Instances buildDataSet(ArrayList<HashMap<String, Double>> features, boolean isTraining) {
        int dataCnt = features.size();

        Instances dataSet = new Instances("training", featureTypes, dataCnt);
        dataSet.setClassIndex(FeatureName.values().length - 1);

        for (int i = 0; i < dataCnt; i++) {
            Instance example = setFeatureForSingleInstance(features.get(i), isTraining);
            dataSet.add(example);
        }
        return dataSet;
    }

    // features and candidates are one-to-one. Thinking about putting features into candidate,
    // and remove some of old code. Wait all the feature extractions to be finished and tested
    public Candidate selectBestCandidate(
            ArrayList<HashMap<String, Double>> features,
            ArrayList<Candidate> candidates) throws Exception {
        double bestScore = -1;
        int bestIdx = -1;
        Instances dataSet = buildDataSet(features, false);
        for (int i = 0; i < features.size(); i++) {
            Instance s = dataSet.get(i);
            double score = localClassifierModel.classifyInstance(s);
            if (score > bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }
        return candidates.get(bestIdx);
    }

    public void train(ArrayList<HashMap<String, Double>> features) throws Exception {
        Instances trainingData = buildDataSet(features, true);
        Classifier cModel = getModel(trainingData);

        this.localClassifierModel = cModel;
    }

    public double[] predict(ArrayList<HashMap<String, Double>> testing) throws Exception {
        Instances testingData = buildDataSet(testing, false);
        double[] res = new double[testingData.size()];

        for (int i=0; i<testingData.size(); i++) {
            Instance ins = testingData.get(i);
            double[] dist = this.localClassifierModel.distributionForInstance(ins);
            res[i] = dist[0];
        }
        return res;
    }
}
