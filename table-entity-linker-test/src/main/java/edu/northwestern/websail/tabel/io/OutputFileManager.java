package edu.northwestern.websail.tabel.io;

import com.google.gson.Gson;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class OutputFileManager implements Closeable {

    public PrintStream out = null;

    public OutputFileManager(String fileName) {
        try {
            OutputFileManager.createFile(fileName);
            out = new PrintStream(new FileOutputStream(fileName));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public OutputFileManager(String fileName, boolean append) {
        try {
            OutputFileManager.createFile(fileName);
            out = new PrintStream(new FileOutputStream(fileName, append));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void createFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (fileName.contains("/")) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static void printFileText(String fileName, String text) throws IOException {
        OutputFileManager.createFile(fileName);
        PrintStream out = new PrintStream(new FileOutputStream(fileName));
        out.print(text);
        out.flush();
        out.close();
    }

    public static void printFileText(String fileName, String text, boolean append) throws IOException {
        OutputFileManager.createFile(fileName);
        PrintStream out = new PrintStream(new FileOutputStream(fileName, append));
        out.print(text);
        out.flush();
        out.close();
    }

    public static void serializeObjectToFile(Object object, String fileName) throws IOException {
        OutputFileManager.createFile(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        oos.writeObject(object);
        oos.flush();
        oos.close();
    }

    public static void serializeObjectToFile(Object object, ObjectOutputStream oos) throws IOException {
        oos.writeObject(object);
        oos.flush();
    }

    public static void serializeObjectToJSONFile(Object object, String fileName, boolean compress) throws IOException{
        Gson gson = new Gson();
        String json = gson.toJson(object);
        if(!compress) {
            OutputFileManager.printFileText(fileName, json);
            return;
        }
        OutputFileManager.createFile(fileName);
        PrintStream out = new PrintStream(new GZIPOutputStream(
                new FileOutputStream(fileName)));
        out.println(json);
        out.close();

    }

    public void println(String s) {
        out.println(s);
    }

    public void print(String s) {
        out.print(s);
    }

    public void close() {
        out.flush();
        out.close();
    }
}

