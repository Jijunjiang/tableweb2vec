package edu.northwestern.websail.tabel.io;

import edu.northwestern.websail.tabel.model.Mention;
import edu.northwestern.websail.tabel.model.MentionDoc;
import edu.northwestern.websail.tabel.model.WikiCell;
import edu.northwestern.websail.tabel.model.WtTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.nashorn.internal.runtime.ECMAException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TableDataReader {

    public static ArrayList<MentionDoc> loadMentionDocs(String filePath) throws Exception {
        ArrayList<MentionDoc> mentionDocs = new ArrayList<MentionDoc>();
        File f = new File(filePath);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            Gson gson = new GsonBuilder().create();
            long cnt = 0;

            while ((line = br.readLine()) != null) {
                cnt++;
                if (cnt % 500000 == 0) {
                    System.out.println("read in: " + cnt/1000 + " k");
                }

                MentionDoc mentionDoc = gson.fromJson(line, MentionDoc.class);
                mentionDocs.add(mentionDoc);
            }
            br.close();

            System.out.println("finished reading all mention docs");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mentionDocs;
    }

    public static ArrayList<MentionDoc> loadMentionDocsFromResource(String filename) throws Exception {
        ArrayList<MentionDoc> mentionDocs = new ArrayList<MentionDoc>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(TableDataReader.class.getResourceAsStream(filename)));
            String line;
            Gson gson = new GsonBuilder().create();

            while ((line = br.readLine()) != null) {
                MentionDoc mentionDoc = gson.fromJson(line, MentionDoc.class);
                mentionDocs.add(mentionDoc);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mentionDocs;
    }

    public static ArrayList<String> loadMentionIDs(String filePath) throws Exception {
        ArrayList<String> mentionIDs = new ArrayList<String>();
        File f = new File(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String line;
        while ((line = br.readLine()) != null) {
            mentionIDs.add(line);
        }
        br.close();
        return mentionIDs;
    }

    public static HashMap<Integer, String> loadIdTitleMap(String filePath){
        HashMap<Integer, String> idTitleMap = new HashMap<Integer, String>();
        File f = new File(TableDataReader.class.getClassLoader().getResource(filePath).getFile());

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\\s+");
                idTitleMap.put(Integer.parseInt(split[0]), split[1]);
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return idTitleMap;
    }

    public static ArrayList<WtTable> loadTable(String filePath) {
        ArrayList<WtTable> tables = new ArrayList<WtTable>();
        File f = new File(filePath);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line;
            Gson gson = new GsonBuilder().create();

            int cnt = 0;
            while ((line = br.readLine()) != null) {
                cnt++;
                if (cnt % 100000 == 0) {
                    System.out.println("read in: " + cnt/1000 + " k");
                }

                WtTable table = gson.fromJson(line, WtTable.class);
                tables.add(table);
            }
            br.close();

            System.out.println("finished loading all tables");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tables;
    }

    public static ArrayList<WtTable> loadTableFromResource(String filename) {
        ArrayList<WtTable> tables = new ArrayList<WtTable>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(TableDataReader.class.getResourceAsStream(filename)));
            String line;
            Gson gson = new GsonBuilder().create();

            while ((line = br.readLine()) != null) {
                WtTable table = gson.fromJson(line, WtTable.class);
                tables.add(table);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tables;
    }
}
