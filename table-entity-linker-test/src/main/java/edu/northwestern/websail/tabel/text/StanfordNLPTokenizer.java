package edu.northwestern.websail.tabel.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.core.StopAnalyzer;

/**
 * Created by riflezhang on 11/15/16.
 */

public class StanfordNLPTokenizer extends Tokenizer {


    public CharArraySet stopwords;
    public String text;

    public StanfordCoreNLP pipeline;
    public Annotation document;
    public ArrayList<Token> tokens;
    public int currentIndex;
    public boolean toLower = false;

    public StanfordNLPTokenizer() {

        Properties props = new Properties();
        props.put("annotators", "tokenize");
        props.put("ssplit.newlineIsSentenceBreak", "always");
        props.put("ssplit.htmlBoundariesToDiscard", ".*");
        props.put("tokenize.options", "untokenizable=noneKeep");
        props.put("parse.model", "edu/stanford/nlp/models/lexparser/englishRNN.ser.gz");
        props.put("ner.applyNumericClassifiers", "false");
        pipeline = new StanfordCoreNLP(props);
        stopwords = new CharArraySet(Version.LUCENE_40, 0, true);
    }

    public StanfordNLPTokenizer(String text) {
        super(text);
        Properties props = new Properties();
        props.put("annotators", "tokenize");
        props.put("ssplit.newlineIsSentenceBreak", "always");
        props.put("ssplit.htmlBoundariesToDiscard", ".*");
        props.put("tokenize.options", "untokenizable=noneKeep");
        props.put("parse.model", "edu/stanford/nlp/models/lexparser/englishRNN.ser.gz");
        props.put("ner.applyNumericClassifiers", "false");
        pipeline = new StanfordCoreNLP(props);
        this.initialize(text);
    }

    public void initialize(String text) {
        try {
            this.
                    clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document = new Annotation(text);
        pipeline.annotate(document);
        tokens = new ArrayList<Token>();
        int i = 0;
        if (document == null) {
            System.out.println("BUG:\t" + text);
        }
        for (CoreLabel token : document.get(TokensAnnotation.class)) {
            Token t = new Token(token.get(TextAnnotation.class),
                    token.get(CharacterOffsetBeginAnnotation.class),
                    token.get(CharacterOffsetEndAnnotation.class),
                    i++);
            if (stopwords.contains(t.text.toLowerCase())) continue;
            if (toLower) t.text = t.text.toLowerCase();
            tokens.add(t);
        }
    }

    @Override
    public List<Token> getAllTokens() throws IOException {
        return tokens;
    }

    @Override
    public Token next() throws IOException {
        if ((++currentIndex) < tokens.size()) {
            Token t = this.tokens.get(currentIndex);
            return t;
        }
        return null;
    }

    @Override
    public Token getCurrentToken() throws IOException {
        if (currentIndex < tokens.size()) {
            Token t = this.tokens.get(currentIndex);
            return t;
        }
        return null;
    }

    @Override
    public void clear() throws IOException {
        document = null;

    }


    public boolean isToLower() {
        return toLower;
    }

    public void setToLower(boolean toLower) {
        this.toLower = toLower;
    }
}
