package edu.northwestern.websail.tabel.model;

import edu.northwestern.websail.datastructure.sketch.Sketch;

import java.io.Serializable;

public class MaxTFSketch implements Serializable {

    public static final long serialVersionUID = -4241633807750443072L;
    public String term;
    public int max;
    public Sketch sketch;


    public MaxTFSketch() {
        term = "";
        max = 0;
        sketch = null;
    }

    public MaxTFSketch(Sketch target){
        sketch = target;
    }

    public MaxTFSketch(Sketch target, int num, String word){
        term = word;
        max = num;
        sketch = target;
    }
}
