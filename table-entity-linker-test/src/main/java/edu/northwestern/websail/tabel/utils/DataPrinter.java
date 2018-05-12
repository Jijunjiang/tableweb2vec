package edu.northwestern.websail.tabel.utils;

import edu.northwestern.websail.tabel.io.OutputFileManager;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DataPrinter implements Closeable {
    public OutputFileManager out;

    public DataPrinter(String filepath) {
        out = new OutputFileManager(filepath);
    }

    public void printMap(HashMap mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            out.print(pair.getKey() + " " + pair.getValue() + "\t");
            it.remove(); // avoids a ConcurrentModificationException
        }
        out.println("");
    }

    public void printFeatures(ArrayList<HashMap<String, Double>> features) {
        for (int i=0; i<features.size(); i++) {
            HashMap<String, Double> f = features.get(i);
            printMap(f);
        }
    }

    public void close() {
        out.close();
    }
}
