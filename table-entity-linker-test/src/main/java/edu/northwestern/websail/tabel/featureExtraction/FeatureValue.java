package edu.northwestern.websail.tabel.featureExtraction;

public class FeatureValue {
    public Double value;
    public Boolean isNominal;
    public Boolean isMissing;
    public static FeatureValue missing = new FeatureValue(Double.NEGATIVE_INFINITY, false, true);
    public FeatureValue() {
    }

    public FeatureValue(Double value) {
        this.value = value;
        this.isNominal = false;
        this.isMissing = false;
    }

    public FeatureValue(Double value, Boolean isNominal) {
        this(value);
        this.isNominal = isNominal;
        this.isMissing = false;
    }

    public FeatureValue(Double value, Boolean isNominal, Boolean isMissing) {
        this.value = value;
        this.isNominal = isNominal;
        this.isMissing = isMissing;
    }

    public String toString(){
        return "{Value:" + value + ", isNominal:" + isNominal + ", isMissing:" + isMissing + "}";
    }

    public FeatureValue copy(){
        return new FeatureValue(this.value, this.isNominal, this.isMissing);
    }
}
