package edu.northwestern.websail.tabel.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.InputFileManager;
import edu.northwestern.websail.wda.model.TermMeasureStat;

import java.io.*;
import java.util.HashMap;

public class MentionDocRAFManager implements Closeable {

    public final HashMap<String, Long> mentionDocPos;
    public final RandomAccessFile mentionDocRAF;

    public MentionDocRAFManager() throws FileNotFoundException {
        String posFile = GlobalConfig.tableMentionPos;
        String rafFile = GlobalConfig.tableMentionRAF;
        mentionDocPos = loadPosition(posFile);
        mentionDocRAF = new RandomAccessFile(rafFile, "r");
    }

    public MentionDocRAFManager(String posFile, String rafFile) throws FileNotFoundException {
        mentionDocPos = loadPosition(posFile);
        mentionDocRAF = new RandomAccessFile(rafFile, "r");
    }

    public final HashMap<String, Long> loadPosition(String filename) {
        InputFileManager inMgr = new InputFileManager(filename);
        HashMap<String, Long> positionMap = new HashMap<String, Long>();
        String line = null;

        int cnt = 0;
        while ((line = inMgr.readLine()) != null) {
            cnt++;
            if (cnt % 500000 == 0) {
                System.out.println("load: " + cnt / 1000 + " k");
            }

            if (line.trim().equals(""))
                continue;
            String[] parts = line.split("\t");
            positionMap.put(parts[0], Long.valueOf(parts[1]));
        }
        inMgr.close();
        System.out.println("finish loading mention doc pos");
        return positionMap;
    }

    public String getLineFromRAF(String key) throws IOException {
        if (!this.mentionDocPos.containsKey(key)) {
            return null;
        } else {
            mentionDocRAF.seek(((Long)this.mentionDocPos.get(key)).longValue());
            String line = mentionDocRAF.readUTF();
            return line;
        }
    }

    public MentionDoc getMentionDocFromRAF(String key) throws IOException {
        if (!this.mentionDocPos.containsKey(key)) {
            return null;
        } else {
            mentionDocRAF.seek(((Long)this.mentionDocPos.get(key)).longValue());
            String line = mentionDocRAF.readUTF();
            Gson gson = new GsonBuilder().create();
            return (MentionDoc)gson.fromJson(line, MentionDoc.class);
        }
    }

    public void close() throws IOException {
        mentionDocRAF.close();
    }
}

