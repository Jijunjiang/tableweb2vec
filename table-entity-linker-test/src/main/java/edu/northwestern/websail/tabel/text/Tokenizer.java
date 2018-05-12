package edu.northwestern.websail.tabel.text;

import org.apache.lucene.analysis.util.CharArraySet;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Tokenizer {
    protected String text;
    protected CharArraySet stopwords;

    public Tokenizer() {
    }

    public Tokenizer(String text) {
        this.text = text;
    }

    public void initialize(String text) throws IOException {
        this.text = text;
    }

    public void setStopwords(CharArraySet stopwords) {
        this.stopwords = stopwords;
    }

    public abstract List<Token> getAllTokens() throws IOException;

    public abstract Token next() throws IOException;

    public abstract Token getCurrentToken() throws IOException;

    public abstract void clear() throws IOException;

    public static String[] convertTokensToStringArray(List<Token> tokens) {
        String[] result = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            result[i] = tokens.get(i).text;
        }
        return result;
    }

    public static String[] convertTokensToStringArray(List<Token> tokens,
                                                      boolean lowerCase) {
        String[] result = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            if (lowerCase)
                result[i] = tokens.get(i).text.toLowerCase();
            else
                result[i] = tokens.get(i).text;
        }
        return result;
    }

    public static HashSet<String> convertTokensToStringSet(List<Token> tokens) {
        HashSet<String> set = new HashSet<String>();
        for (Token t : tokens) {
            set.add(t.text);
        }
        return set;
    }

    public static String convertTokensToString(List<Token> tokens) {
        if (tokens.size() == 0) return "";
        if (tokens.size() == 1) return tokens.get(0).text;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            result.append(tokens.get(i).text);
            result.append(' ');
        }
        return result.substring(0, result.length() - 1);
    }

    public static List<String> convertTokensToStringList(List<Token> tokens) {
        List<String> result = new ArrayList<String>(tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            result.add(tokens.get(i).text);
        }
        return result;
    }

    public static HashMap<String, Integer> convertTokensToCountMap(List<Token> tokens) {
        HashMap<String, Integer> result = new HashMap<String, Integer>(tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            String key = tokens.get(i).text;
            if (!result.containsKey(key)) {
                result.put(key, 0);
            }
            result.put(key, 1 + result.get(key));
        }
        return result;
    }

    public static List<Token> convertStringListToTokens(List<String> tokenStr) throws IOException {
        List<Token> tokens = new ArrayList<Token>();
        for (String s : tokenStr) {
            tokens.add(new Token(s, 0, 0, 1));
        }
        return tokens;
    }

    public static void adjustTokensOffset(List<Token> tokens, int offset) {
        for (Token tk : tokens) {
            tk.startOffset = (tk.startOffset + offset);
            tk.endOffset = (tk.endOffset + offset);
        }
    }

    public static int getTokenForCharacterOffset(List<Token> tokens, int offset) {
        int low = 0;
        int high = tokens.size();
        int medium = (low + high) / 2;

        int sol = -1;
        while (low < high) {
            medium = (low + high) / 2;
            if (low == high - 1)
                return medium;
            if (tokens.get(medium).startOffset <= offset
                    && tokens.get(medium).startOffset >= offset) {
                sol = medium;
                break;
            } else {

                if (tokens.get(medium).startOffset > offset) {
                    high = medium;
                } else
                    low = medium;
            }
        }
        return sol;
    }

    public static String genPattern(List<Token> tokens, String text) {
        return genPattern(tokens, text, false);
    }

    /*
    public static String getTokenPOSString(List<Token> tokens) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();
        for (Token tokenizer : tokens) {
            if (tokenizer instanceof TaggedToken) {
                stringBuilder.append(((TaggedToken) tokenizer).getPOS() + " ");
            } else {
                throw new Exception("No POS information found in tag");
            }
        }
        return stringBuilder.toString().trim();
    }*/

    public static String genPattern(List<Token> tokens, String text,
                                    boolean resetTokenOffset) {
        return genPattern(tokens, text, resetTokenOffset, false);
    }

    public static String genPattern(List<Token> tokens, String text,
                                    boolean resetTokenOffset, boolean lowercase) {
        if (lowercase)
            text = text.toLowerCase();
        int tokenStart = 0;
        if (resetTokenOffset)
            tokenStart = tokens.get(0).startOffset;
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) {
            sb.append(Pattern.quote(text.substring(t.startOffset
                    - tokenStart, t.endOffset - tokenStart)));
            sb.append("[\\s\\-]*");
        }
        return sb.toString();
    }

    public static List<Token> normalize(List<Token> tokens, boolean lowercase,
                                        boolean stem) {
        ArrayList<Token> newTokens = new ArrayList<Token>();
        PorterStemmer stemmer = new PorterStemmer();
        for (Token t : tokens) {
            if (lowercase) {
                t.text = t.text.toLowerCase();
            }
            if (stem) {
                stemmer.setCurrent(t.text);
                stemmer.stem();
                t.text = (stemmer.getCurrent());
            }
            if (t.text.length() > 0) {
                newTokens.add(t);
            }
        }
        return newTokens;

    }

    public static int kmpSearch(List<Token> hayStack, List<Token> needles,
                                int startOffset) {
        int[] t = kmpTable(needles);
        int m = startOffset;
        int i = 0;
        while (m + i < hayStack.size()) {
            if (needles.get(i).text.equals(hayStack.get(m + i).text)) {
                if (i == needles.size() - 1) {
                    return m;
                }
                i++;
            } else {
                if (t[i] > -1) {
                    m = m + i - t[i];
                    i = t[i];
                } else {
                    i = 0;
                    m++;
                }
            }
        }
        return -1;
    }

    public static int[] kmpTable(List<Token> needles) {
        if (needles.size() == 0)
            return null;
        int[] t = new int[needles.size()];
        t[0] = -1;
        int pos = 2;
        int cnd = 0;
        while (pos < needles.size()) {
            if (needles.get(pos - 1).text
                    .equals(needles.get(cnd).text)) {
                cnd += 1;
                t[pos] = cnd;
                pos++;
            } else if (cnd > 0) {
                cnd = t[cnd];
            } else {
                t[pos] = 0;
                pos++;
            }
        }
        return t;
    }

    public static String convertStringListToString(List<String> list, String sep) {
        StringBuffer sb = new StringBuffer();
        for(String s : list){
            sb.append(s).append(sep);
        }
        return sb.toString();
    }

    /*
    public static void main(String[] args) throws IOException {
        StanfordNLPTokenizer tokenizer = new StanfordNLPTokenizer();
        String hay = "The above example contains all the elements of the algorithm. above example";
        String needle = "above example";
        tokenizer.initialize(hay);
        List<Token> hayStack = tokenizer.getAllTokens();
        tokenizer.initialize(needle);
        List<Token> needles = tokenizer.getAllTokens();
        hayStack = Tokenizer.normalize(hayStack, true, true);
        needles = Tokenizer.normalize(needles, true, true);
        System.out.println(Tokenizer.kmpSearch(hayStack, needles, 2));

    }*/

}
