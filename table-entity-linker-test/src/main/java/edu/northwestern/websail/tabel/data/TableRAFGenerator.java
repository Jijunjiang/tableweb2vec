package edu.northwestern.websail.tabel.data;

import com.google.gson.Gson;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.InputFileManager;
import edu.northwestern.websail.tabel.io.OutputFileManager;
import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.MentionDoc;
import edu.northwestern.websail.tabel.model.MentionDocRAFManager;
import edu.northwestern.websail.tabel.model.TableRAFManager;
import edu.northwestern.websail.tabel.model.WtTable;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TableRAFGenerator {
    String jsonFile;
    String rafFile;
    String posFile;

    public TableRAFGenerator() {
        jsonFile = GlobalConfig.tablesDir;
        rafFile = GlobalConfig.tablesRAF;
        posFile = GlobalConfig.tablesPos;
    }

    public TableRAFGenerator(String jsonFile, String rafFile, String posFile) {
        this.jsonFile = jsonFile;
        this.rafFile = rafFile;
        this.posFile = posFile;
    }

    public void createTablesRAF() throws Exception {
        OutputFileManager out = new OutputFileManager(this.posFile);
        InputFileManager in = new InputFileManager(this.jsonFile);
        RandomAccessFile raf = new RandomAccessFile(this.rafFile, "rw");
        Gson gson = new Gson();
        String line;
        long pos = 0;
        long cnt = 0;
        while ((line = in.readLine()) != null) {
            cnt++;
            if (cnt % 100000 == 0) {
                System.out.println("read in: " + cnt/1000 + " k");
            }

            WtTable t = gson.fromJson(line, WtTable.class);
            out.println(t._id + "\t" + pos);
            raf.seek(pos);
            byte[] b = line.getBytes();
            int len = b.length;
            raf.writeInt(len);
            raf.write(b);
            pos = raf.length();
        }
        in.close();
        raf.close();
    }

    public static void generateModelTables() throws Exception {
        TableRAFManager tblMgr;
        tblMgr = new TableRAFManager();

        String mentionFile = GlobalConfig.modelDataMention;
        ArrayList<MentionDoc> mentionDocs = TableDataReader.loadMentionDocs(mentionFile);
        HashMap<String, String> tableMap = new HashMap<String, String>();
        for (int i=0; i<mentionDocs.size(); i++) {
            MentionDoc md = mentionDocs.get(i);
            int pgId = md.pgId;
            int tableId = md.tableId;
            String tableKey = Integer.toString(pgId) + "-" + Integer.toString(tableId);
            String line = tblMgr.getLineFromRAF(tableKey);
            if (line == null) {
                System.out.println("line is null");
            }
            if (!tableMap.containsKey(tableKey)) {
                tableMap.put(tableKey, line);
            }
        }

        String outputPath = GlobalConfig.modelDataTables;
        OutputFileManager out = new OutputFileManager(outputPath);
        Iterator it = tableMap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            out.println((String)pair.getValue());
        }
        out.close();
    }

    public static void main(String[] args) throws Exception {
        TableRAFGenerator g = new TableRAFGenerator(
                GlobalConfig.modelDataTables,
                GlobalConfig.modelDataTablesRaf,
                GlobalConfig.modelDataTablesPos
        );
        g.createTablesRAF();

        //generateModelTables();
    }
}
