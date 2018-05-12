package edu.northwestern.websail.tabel.io;

import com.google.gson.Gson;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class InputFileManager implements Closeable {

    public BufferedReader in = null;
    public Boolean isLowerCase = false;

    public InputFileManager(String fileName) {
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(
                    fileName), "UTF-8");
            in = new BufferedReader(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFileText(String fileName) throws IOException {
        File file = new File(fileName);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(
                file));
        int character;
        StringBuffer output = new StringBuffer();
        while ((character = reader.read()) != -1) {
            output.append((char) character);
        }
        reader.close();
        return output.toString();
    }

    public String readLine() {
        try {
            String line = in.readLine();
            if (line == null)
                return null;
            if (isLowerCase)
                return line.toLowerCase().trim();
            return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Object deserializeObjectFromFile(String fileName)
            throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
                new FileInputStream(fileName)));
        Object object = ois.readObject();
        ois.close();
        return object;
    }

    public static <T> T deserializeObjectFromJSON(String fileName,
                                                  boolean compressed, Class<T> classOfT) throws IOException {
        String text = "";
        if (!compressed)
            text = readFileText(fileName);
        else {
            File file = new File(fileName);
            InputStreamReader reader = new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(file)));
            int character;
            StringBuffer output = new StringBuffer();
            while ((character = reader.read()) != -1) {
                output.append((char) character);
            }
            reader.close();
            text = output.toString();
        }
        Gson gson = new Gson();
        return gson.fromJson(text, classOfT);
    }
}
