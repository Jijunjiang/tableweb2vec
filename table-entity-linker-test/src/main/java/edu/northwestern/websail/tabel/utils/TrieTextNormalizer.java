package edu.northwestern.websail.tabel.utils;

public class TrieTextNormalizer {
    public static String getCleanStr(String str) {

        str = str.replaceAll("_", " ");
        str = str.replaceAll("\\s+", " ");
        str = str.trim();

        return str;
    }
}
