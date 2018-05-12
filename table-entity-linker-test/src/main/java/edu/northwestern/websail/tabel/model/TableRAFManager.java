package edu.northwestern.websail.tabel.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.InputFileManager;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class TableRAFManager implements Closeable {
    public final HashMap<String, Long> tablesPos;
    public final RandomAccessFile tablesRAF;

    public TableRAFManager() throws FileNotFoundException {
        String posFile = GlobalConfig.tablesPos;
        String rafFile = GlobalConfig.tablesRAF;
        tablesPos = loadPosition(posFile);
        tablesRAF = new RandomAccessFile(rafFile, "r");
    }

    public TableRAFManager(String posFile, String rafFile) throws FileNotFoundException {
        tablesPos = loadPosition(posFile);
        tablesRAF = new RandomAccessFile(rafFile, "r");
    }

    public final HashMap<String, Long> loadPosition(String filename) {
        InputFileManager inMgr = new InputFileManager(filename);
        HashMap<String, Long> positionMap = new HashMap<String, Long>();
        String line = null;
        int cnt = 0;
        while ((line = inMgr.readLine()) != null) {
            cnt++;
            if (cnt % 100000 == 0) {
                System.out.println("load: " + cnt / 1000 + " k");
            }

            if (line.trim().equals(""))
                continue;
            String[] parts = line.split("\t");
            positionMap.put(parts[0], Long.valueOf(parts[1]));
        }
        inMgr.close();
        System.out.println("finish loading tables pos");
        return positionMap;
    }

    public String getLineFromRAF(String key) throws IOException {
        if (!this.tablesPos.containsKey(key)) {
            System.out.println("table not found");
            return null;
        } else {
            tablesRAF.seek(((Long)this.tablesPos.get(key)).longValue());
            int len = tablesRAF.readInt();
            byte[] bline = new byte[len];
            tablesRAF.read(bline);
            String line = new String(bline);
            return line;
        }
    }

    public WtTable getTableFromRAF(String key) throws IOException {
        if (!this.tablesPos.containsKey(key)) {
            return null;
        } else {
            tablesRAF.seek(((Long)this.tablesPos.get(key)).longValue());
            int len = tablesRAF.readInt();
            byte[] bline = new byte[len];
            tablesRAF.read(bline);
            String line = new String(bline);
            Gson gson = new GsonBuilder().create();
            return (WtTable)gson.fromJson(line, WtTable.class);
        }
    }

    public void close() throws IOException {
        tablesRAF.close();
    }
}
