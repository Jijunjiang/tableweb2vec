package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.io.TableDataReader;
import edu.northwestern.websail.tabel.model.*;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateTablePlainText {

    static private String sourcePath = "/websail/common/wikification/data/webTables/tables/tables.json";
    static private String outPath = "/websail/jijun/data/";
    static ArrayList<WtTable> tables;
    public static void main(String[] args) throws Exception{
       tables = TableDataReader.loadTable(sourcePath);
       //generateRowContext(tables);
       generateColContext(tables);
       //dataStatistic(tables);
    }

    static private void generateRowContext(ArrayList<WtTable> tables) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter("/websail/jijun/marked_row.txt"));
		int size = tables.size();
		System.out.println("totle table: " + size);
		int num = 1;
		for (WtTable table : tables) {
			if (num % 1000 == 0) {
				System.out.println((double)num / size * 100 + " %");
			}
			for (int i=0; i<table.numDataRows; i++) {
	           	for (int j=0; j<table.numCols; j++) {
	                WikiCell cell = table.tableData[i][j];
	               	StringBuilder entities = new StringBuilder();
	                for (WikiLink link : cell.surfaceLinks) {
						entities.append(link.target.id == -1 ? "" : "e:" + Integer.toString(link.target.id)).append(" ");
					}
	                out.write(cell.text + " " + entities.toString().replaceAll("[-+.^:,]",""));
	            }
				out.write("\n");
	        } 
			num++;
	    }
		out.flush();
    }

    static private void generateColContext(ArrayList<WtTable> tables) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter("/websail/jijun/marked_col.txt"));
		int size = tables.size();
		System.out.println("totle table: " + size);
		int num = 1;
		for (WtTable table : tables) {
			if (num % 1000 == 0) {
				System.out.println((double)num / size * 100 + " %");
			}
			for (int j=0; j<table.numCols; j++) {
	           	for (int i=0; i<table.numDataRows; i++) {
	                WikiCell cell = table.tableData[i][j];
	               	StringBuilder entities = new StringBuilder();
					for (WikiLink link : cell.surfaceLinks) {
						entities.append(link.target.id == -1 ? "" : "e:" + Integer.toString(link.target.id)).append(" ");
					}
	                out.write(cell.text + " " + entities.toString().replaceAll("[-+.^:,]",""));
	            }
				out.write("\n");
	        } 
			num++;
	    }
		out.flush();
    }

    static private void dataStatistic(ArrayList<WtTable> tables) throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
    	Map<String, Object> map = new HashMap<String, Object>();
    	Map<Integer, Integer> lengthStringMap = new HashMap<Integer, Integer>();
    	Map<Integer, Integer> lengthSurfaceMap = new HashMap<Integer, Integer>();
    	Map<Integer, Integer> lengthWithOutSurfaceMap = new HashMap<Integer, Integer>();

		int size = tables.size();
		System.out.println("totle table: " + size);
		int num = 1;
		for (WtTable table : tables) {
			if (num % 1000 == 0) {
				System.out.println((double)num / size * 100 + " %");
			}
			for (int i=0; i<table.numDataRows; i++) {
	           	for (int j=0; j<table.numCols; j++) {
	           		boolean ignore = false;
	                WikiCell cell = table.tableData[i][j];
	                int lengthOfString = cell.text.split("\\s+").length;
	                lengthStringMap.put(lengthOfString, lengthStringMap.getOrDefault(lengthOfString, 0) + 1);
	                int totalLengthOfSurface = 0;
	                for (WikiLink link : cell.surfaceLinks) {
	                	int lengthOfSurface = link.surface.split("\\s+").length;
	                	lengthSurfaceMap.put(lengthOfSurface, lengthSurfaceMap.getOrDefault(lengthOfSurface, 0) + 1);
	                	if (cell.text.indexOf(link.surface) != -1) {
	                		ignore = true;
	                		break;
						}
						totalLengthOfSurface += lengthOfSurface;
	                }
	                int lengthWithOutSurface = lengthOfString - totalLengthOfSurface;
	                if (lengthWithOutSurface < 0 || ignore || lengthWithOutSurface == lengthOfString) continue;
	                lengthWithOutSurfaceMap.put(lengthWithOutSurface, lengthWithOutSurfaceMap.getOrDefault(lengthWithOutSurface, 0) + 1);
	            }
	        }
			num++;
	    }

	    map.put("lengthStringMap", lengthStringMap);
	    map.put("lengthSurfaceMap", lengthSurfaceMap);
	    map.put("lengthWithOutSurfaceMap", lengthWithOutSurfaceMap);
		try {///Users/apple/PycharmProjects/data/statistic.json
			mapper.writeValue(new File(outPath + "statistic.json"), map);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }







}
