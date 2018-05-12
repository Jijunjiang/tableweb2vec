package edu.northwestern.websail.tabel.utils;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellTextTokenizer {

    static String sqBrktRegex = "[\\[].*?[\\]]";
    static String clyBrktRegex = "[\\{].*?[\\}]";
    static String rndBrktRegex = "[\\(].*?[\\)]";
    static Pattern sqBrktPat = Pattern.compile(sqBrktRegex);
    static Pattern clyBrktPat = Pattern.compile(clyBrktRegex);
    static Pattern rndBrktPat = Pattern.compile(rndBrktRegex);
    static Pattern isNumericPat = Pattern
            .compile("(?:^|\\s)([1-9](?:\\d*|(?:\\d{0,2})(?:,\\d{3})*)(?:\\.\\d*[1-9])?|0?\\.\\d*[1-9]|0)(?:\\s|$)");

    public static HashSet<String> normalizedText(String text) {
        HashSet<String> textValues = new HashSet<String>();

        Matcher m = sqBrktPat.matcher(text);
        String t;
        while (m.find()) {
            t = m.group().replaceAll("[\\[|\\]]", "").trim();
            if (t.length() > 3 && isNotNumeric(t) && t.length() < 30)
                textValues.add(t);
        }
        text = text.replaceAll(sqBrktRegex, "");

        m = clyBrktPat.matcher(text);
        while (m.find()) {
            t = m.group().replaceAll("[\\{|\\}]", "").trim();
            if (t.length() > 3 && isNotNumeric(t) && t.length() < 30)
                textValues.add(t);
        }
        text = text.replaceAll(clyBrktRegex, "");

        m = rndBrktPat.matcher(text);
        while (m.find()) {
            t = m.group().replaceAll("[\\(|\\)]", "").trim();
            if (t.length() > 3 && isNotNumeric(t) && t.length() < 30)
                textValues.add(t);
        }
        text = text.replaceAll(rndBrktRegex, "");

        String[] parts = text.split(",");

        if (parts.length > 0)
            for (int i = 0; i < parts.length; i++) {
                t = parts[i].trim();
                if (t.length() > 3 && isNotNumeric(t) && t.length() < 30)
                    textValues.add(t);
            }
        // else {
        // if (tokenizer.length() > 3 && isNotNumeric(tokenizer) && tokenizer.length() < 30)
        // textValues.add(text);
        // }

        return textValues;

    }

    public static boolean isNotNumeric(String t) {

        Matcher m = isNumericPat.matcher(t);
        return !m.find();

    }
}
