package edu.northwestern.websail.tabel.main;

import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.WtTable;
import edu.northwestern.websail.tabel.train.ModelTrainingDataExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EntityLinking {

    public static void printMap(HashMap mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            System.out.print(pair.getKey() + "=" + pair.getValue() + "\t");
            it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("\n");
    }


    public static void main(String[] args) throws Exception {
        // Finally, users will use this class to interact with our code. Input is provided in
        // JSON format, and output is the annotated table. This code will need to support
        // command line argument like execution. Please see Apache CLI for that.

        ArrayList<WtTable> tb = TableDataReader.loadTableFromResource("/test.json");
        WtTable t1 = tb.get(0);
        ModelTrainingDataExtractor ex = new ModelTrainingDataExtractor();
        // ArrayList<Mention> mentions = ex.extractMentionsFromTable(t1);

        ArrayList<HashMap<String, Double>> features = ex.getFeaturesFromOneTable(t1);
        for (int i = 0; i < features.size(); i++) {
            HashMap<String, Double> f = features.get(i);
            printMap(f);
        }
    }
}
