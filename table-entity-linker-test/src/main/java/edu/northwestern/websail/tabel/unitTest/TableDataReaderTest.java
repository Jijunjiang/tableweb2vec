package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.data.TableRAFGenerator;
import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.*;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class TableDataReaderTest {

    @Test
    public void testTable() {
        ArrayList<WtTable> tables = TableDataReader.loadTableFromResource("/test.json");
        WtTable t1 = tables.get(0);

        System.out.println("_id: " + t1._id + "\n" + "numCols: " + t1.numCols + "\n" + "numDataRows: " + t1.numDataRows);
        for (int i=0; i<t1.numDataRows; i++) {
            for (int j=0; j<t1.numCols; j++) {
                WikiCell cell = t1.tableData[i][j];
                System.out.print(cell.text + "\t\t");
            }
            System.out.println("");
        }

        assertEquals("row",  11, (int)t1.numDataRows);
        assertEquals("col", 4, (int)t1.numCols);
        assertEquals("title", "Mid Antrim (Northern Ireland Parliament constituency)", t1.pgTitle);
        WikiCell cell = t1.tableData[0][1];
        assertEquals("cell text", "1929", cell.text);
        WikiTitle title = cell.surfaceLinks.get(0).target;
        assertEquals("link title id", 3611706, title.id);
        assertEquals("link title", "Northern_Ireland_general_election,_1929", title.title);
    }

    @Test
    public void testTableRAF() throws Exception {
        String posFile = GlobalConfig.tablesPos;
        String rafFile = GlobalConfig.tablesRAF;
        //posFile = "/Users/ruohongzhang/Desktop/websail/table-entity-linker/testTables.pos";
        //rafFile = "/Users/ruohongzhang/Desktop/websail/table-entity-linker/testTables.raf";
        TableRAFManager tbMgr = new TableRAFManager(posFile, rafFile);
        WtTable t1 = tbMgr.getTableFromRAF("10000032-1");

        assertEquals("row",  11, (int)t1.numDataRows);
        assertEquals("col", 4, (int)t1.numCols);
        assertEquals("title", "Mid Antrim (Northern Ireland Parliament constituency)", t1.pgTitle);
        WikiCell cell = t1.tableData[0][1];
        assertEquals("cell text", "1929", cell.text);
        WikiTitle title = cell.surfaceLinks.get(0).target;
        assertEquals("link title id", 3611706, title.id);
        assertEquals("link title", "Northern_Ireland_general_election,_1929", title.title);

        WtTable t2 = tbMgr.getTableFromRAF("10000088-1");
        assertEquals("title", "Whispermoon", t2.pgTitle);

        WtTable t3 = tbMgr.getTableFromRAF("10000309-1");
        assertEquals("title", "Real Voice", t3.pgTitle);

        tbMgr.close();
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TableDataReaderTest.class);
        if (result.getFailureCount() == 0) {
            System.out.println("all passed");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
}
