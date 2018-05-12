package edu.northwestern.websail.tabel.measure;

public class Measure {
    static final double esp = 1e-6;

    public static double accuracy(double[] predicted, double[] actual) {
        int cnt = 0;
        for (int i=0; i<predicted.length; i++) {
            long pred = Math.round(predicted[i]);
            long act = Math.round(actual[i]);
            if (pred == act) cnt++;
        }
        return (double)cnt/(double)predicted.length;
    }

    public static double f1(double[] predicted, double[] actual) {
        int intersect = 0;
        int a = 0;
        int b = 0;
        for (int i=0; i<predicted.length; i++) {
            long pred = Math.round(predicted[i]);
            long act = Math.round(actual[i]);
            if (pred == 1 && act == 1) intersect++;
            if (act == 1) a++;
            if (pred == 1) b++;
        }
        double acc = (double)(intersect + 1) / (double)(b + 1) ;
        double recall = (double)(intersect + 1) / (double)(a + 1);
        double f1 = 2*acc*recall / (acc + recall);
        return f1;
    }

    public static double f1Negative(double[] predicted, double[] actual) {
        int intersect = 0;
        int a = 0;
        int b = 0;
        for (int i=0; i<predicted.length; i++) {
            long pred = Math.round(predicted[i]);
            long act = Math.round(actual[i]);
            if (pred == 0 && act == 0) intersect++;
            if (act == 0) a++;
            if (pred == 0) b++;
        }
        double acc = (double)(intersect + 1) / (double)(b + 1) ;
        double recall = (double)(intersect + 1) / (double)(a + 1);
        double f1 = 2*acc*recall / (acc + recall);
        return f1;
    }
}
