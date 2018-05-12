package edu.northwestern.websail.tabel.data;

import com.google.gson.Gson;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.InputFileManager;
import edu.northwestern.websail.tabel.io.OutputFileManager;
import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.MentionDoc;
import edu.northwestern.websail.tabel.model.MentionDocRAFManager;
import edu.northwestern.websail.tabel.model.WtTable;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.runtime.ECMAException;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class MentionDocRAFGenerator {
    String jsonFile;
    String rafFile;
    String posFile;

    public MentionDocRAFGenerator() {
        jsonFile = GlobalConfig.tableMentionsDir;
        rafFile = GlobalConfig.tableMentionRAF;
        posFile = GlobalConfig.tableMentionPos;
    }

    public MentionDocRAFGenerator(String jsonFile, String rafFile, String posFile) {
        this.jsonFile = jsonFile;
        this.rafFile = rafFile;
        this.posFile = posFile;
    }

    public void createMentionDocRAF() throws Exception {
        OutputFileManager out = new OutputFileManager(this.posFile);
        InputFileManager in = new InputFileManager(this.jsonFile);
        RandomAccessFile raf = new RandomAccessFile(this.rafFile, "rw");
        Gson gson = new Gson();
        String line;
        long pos = 0;
        long cnt = 0;
        while ((line = in.readLine()) != null) {
            cnt++;
            if (cnt % 500000 == 0) {
                System.out.println("read in: " + cnt/1000 + " k");
            }

            MentionDoc m = gson.fromJson(line, MentionDoc.class);
            out.println(m._id.$oid + "\t" + pos);
            raf.seek(pos);
            raf.writeUTF(line);
            pos = raf.length();
        }
        in.close();
        raf.close();
    }

    public static void generateModelMentionDoc() throws Exception {
        MentionDocRAFManager mdMgr;
        mdMgr = new MentionDocRAFManager();

        String trainingFile = GlobalConfig.trainingMentionDir;
        String testingFile = GlobalConfig.testingMentionDir;
        String outputPath = GlobalConfig.modelDataMention;
        OutputFileManager out = new OutputFileManager(outputPath);
        ArrayList<String> ids = TableDataReader.loadMentionIDs(trainingFile);
        ArrayList<String> testingIDs = TableDataReader.loadMentionIDs(testingFile);
        ids.addAll(testingIDs);

        for (int i=0; i<ids.size(); i++) {
            String key = ids.get(i);
            String line = mdMgr.getLineFromRAF(key);
            out.println(line);
        }
        out.close();
    }

    public static void main(String[] args) throws Exception {
        MentionDocRAFGenerator g = new MentionDocRAFGenerator(
                GlobalConfig.modelDataMention,
                GlobalConfig.modelDataMentionRaf,
                GlobalConfig.modelDataMentionPos
        );
        g.createMentionDocRAF();
        //generateModelMentionDoc();
    }
}
