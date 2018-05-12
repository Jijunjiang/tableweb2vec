package edu.northwestern.websail.tabel.unitTest;

import edu.northwestern.websail.tabel.text.StanfordNLPTokenizer;
import edu.northwestern.websail.tabel.text.Token;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.junit.Test;

import java.io.IOException;

import static edu.northwestern.websail.tabel.text.StanfordNLPTokenizer.convertTokensToString;
import static org.junit.Assert.assertEquals;

/**
 * Util testing
 * Test if tokenizer parses data correctly.
 * Same as bitbucket file: StanfordNLPTokenizer.java
 * https://bitbucket.org/websail/websail/src/ce800909fc605714b0a55ca9e5527d3c6bcd704a/
 * text/src/main/java/edu/northwestern/websail/text/tokenizer/StanfordNLPTokenizer.java
 * ?at=master&fileviewer=file-view-default#StanfordNLPTokenizer.java-22,30,39
 */
public class TokenizerTest {
    public StanfordNLPTokenizer t = new StanfordNLPTokenizer();

    @Test
    public void testTokenizer () {
        String text = "o’donovan ~( Thanapon Noraset Category:Hello World spin-offs";

        t.stopwords = (CharArraySet) StopAnalyzer.ENGLISH_STOP_WORDS_SET;
        t.setToLower(true);
        t.initialize(text);

        String[] testString = new String[10];
        for (int i=0; i<10; i++) {
            testString[i] = t.tokens.get(i).text + " " + t.tokens.get(i).startOffset + "," + t.tokens.get(i).endOffset;
        }

        assertEquals("o’donovan 0,9",
                testString[0]);
        assertEquals("~ 10,11",
                testString[1]);
        assertEquals("-lrb- 11,12",
                testString[2]);
        assertEquals("thanapon 13,21",
                testString[3]);
        assertEquals("noraset 22,29",
                testString[4]);
        assertEquals("category 30,38",
                testString[5]);
        assertEquals(": 38,39",
                testString[6]);
        assertEquals("hello 39,44",
                testString[7]);
        assertEquals("world 45,50",
                testString[8]);
        assertEquals("spin-offs 51,60",
                testString[9]);


    }


    public static void main(String[] args) throws IOException {
        StanfordNLPTokenizer t = new StanfordNLPTokenizer();
        String text = "";

        t.stopwords = (CharArraySet) StopAnalyzer.ENGLISH_STOP_WORDS_SET;
        t.setToLower(true);
        t.initialize(text);

        for (Token token : t.tokens) {
            System.out.println(token.text + " " + token.startOffset + "," + token.endOffset);
        }
        System.out.println(convertTokensToString(t.tokens));
        String text2 = "o’donovan ~( Thanapon Noraset Category:Hello World spin-offs";
        t.initialize(text2);
        for (Token token : t.tokens) {
            System.out.println(token.text + " " + token.startOffset + "," + token.endOffset);
        }
    }
}
