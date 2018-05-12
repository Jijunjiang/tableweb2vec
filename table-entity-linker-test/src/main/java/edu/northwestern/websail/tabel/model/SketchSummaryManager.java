package edu.northwestern.websail.tabel.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.northwestern.websail.datastructure.sketch.CountMinSketch;
import edu.northwestern.websail.datastructure.sketch.Sketch;
import edu.northwestern.websail.tabel.io.InputFileManager;
import edu.northwestern.websail.tabel.io.OutputFileManager;
import edu.northwestern.websail.tabel.text.Token;
import edu.northwestern.websail.tabel.config.GlobalConfig;

public class SketchSummaryManager {
    public String contextSketchPath;
    public String titleSketchPath;
    public String contextMetaPath;
    public String titleMetaPath;
    public static boolean compressedJSONSketch = false;
    public int depth;
    public int width;
    public static int sketchSeed = 24016;
    public static final MaxTFSketch NOT_FOUND_SKETCH = new MaxTFSketch();
    public static final SketchMetadata NOT_FOUND_METADATA = new SketchMetadata(0);

    public SketchSummaryManager() {
        this.contextSketchPath = GlobalConfig.contextSketchSummaryDirectory;
        this.titleSketchPath = GlobalConfig.titleSketchSummaryDirectory;
        this.contextMetaPath = GlobalConfig.contextSktechMetaDirectory;
        this.titleMetaPath = GlobalConfig.titleSketchMetaDirectory;
        this.depth = GlobalConfig.sketchDepth;
        this.width = GlobalConfig.sketchWidth;
    }

    public MaxTFSketch sketch() {
        Sketch s = new CountMinSketch(this.depth, this.width, sketchSeed);
        MaxTFSketch result = new MaxTFSketch(s, -1, null);
        return result;
    }

    public MaxTFSketch sketch(List<Token> tokens) {
        Sketch s = new CountMinSketch(this.depth, this.width, sketchSeed);
        int value;
        String tempKey = "";
        int tempCount = 0;
        for (int i = 0; i < tokens.size(); i++) {
            String key = tokens.get(i).text;
            s.update(key, 1);
            value = s.query(key);

            if (value > tempCount) {
                tempCount = value;
                tempKey = key;
            }
        }
        MaxTFSketch result = new MaxTFSketch(s, tempCount, tempKey);
        return result;
    }

    public MaxTFSketch sketch(MaxTFSketch s, List<Token> tokens) {
        int value;
        String tempKey = s.term;
        int tempCount = s.max;
        for (int i = 0; i < tokens.size(); i++) {
            String key = tokens.get(i).text;
            s.sketch.update(key, 1);
            value = s.sketch.query(key);

            if (value > tempCount) {
                tempCount = value;
                tempKey = key;
            }
        }
        s.max = tempCount;
        s.term = tempKey;
        return s;
    }

    public MaxTFSketch sketch(MaxTFSketch s, String term, int count){
        String tempKey = s.term;
        int tempCount = s.max;
        s.sketch.update(term, count);
        int value = s.sketch.query(term);

        if (value > tempCount) {
            tempCount = value;
            tempKey = term;
        }
        s.max = tempCount;
        s.term = tempKey;
        return s;
    }

    public void updateContextSketch(String lang, String titleId, List<Token> leftTokens, List<Token> rightTokens) throws Exception {
        MaxTFSketch sk = this.loadContextSketch(lang, titleId);
        if (sk == null) {
            //System.out.println("sketch no: "+contextSketches.size() + ", new sketch for "+m.goldAnnotation.title);
            sk = this.sketch(leftTokens);
            sk = this.sketch(sk, rightTokens);
        } else {
            sk = this.sketch(sk, leftTokens);
            sk = this.sketch(sk, rightTokens);
        }
        this.saveContextSketch(lang, titleId, sk);
    }

    public String saveTitleSketch(String lang, String titleId, MaxTFSketch sketch) throws IOException {
        return this.saveSketch(lang, titleId, sketch, SketchType.TITLE, compressedJSONSketch);
    }

    public String saveContextSketch(String lang, String titleId, MaxTFSketch sketch) throws IOException {
        return this.saveSketch(lang, titleId, sketch, SketchType.CONTEXT, compressedJSONSketch);
    }

    public String saveTitleSketch(String lang, Integer titleId, MaxTFSketch sketch) throws IOException {
        return this.saveSketch(lang, titleId.toString(), sketch, SketchType.TITLE, compressedJSONSketch);
    }

    public String saveContextSketch(String lang, Integer titleId, MaxTFSketch sketch) throws IOException {
        return this.saveSketch(lang, titleId.toString(), sketch, SketchType.CONTEXT,
                compressedJSONSketch);
    }

    private String saveSketch(String lang, String titleId, MaxTFSketch sketch, SketchType type,
                              boolean compressedJSON) throws IOException {
        String sketchFile;
        if (type == SketchType.CONTEXT) {
            sketchFile = this.buildContextSketchPath(lang, titleId);
        } else if (type == SketchType.TITLE) {
            sketchFile = this.buildTitleSketchPath(lang, titleId);
        }else
            sketchFile = "";

        if (!compressedJSON) {
            OutputFileManager.serializeObjectToFile(sketch, sketchFile);
        } else {
            OutputFileManager.serializeObjectToJSONFile(sketch, sketchFile, true);
        }
        return sketchFile;
    }

    public MaxTFSketch loadTitleSketch(String lang, String titleId) throws IOException, ClassNotFoundException {
        return this.loadSketch(lang, titleId, SketchType.TITLE, compressedJSONSketch);
    }

    public MaxTFSketch loadTitleSketch(String lang, Integer titleId) throws IOException, ClassNotFoundException {
        return this.loadSketch(lang, titleId.toString(), SketchType.TITLE, compressedJSONSketch);
    }

    public MaxTFSketch loadContextSketch(String lang, String titleId) throws IOException, ClassNotFoundException {
        return this.loadSketch(lang, titleId, SketchType.CONTEXT, compressedJSONSketch);
    }

    public MaxTFSketch loadContextSketch(String lang, Integer titleId) throws IOException, ClassNotFoundException {
        return this.loadSketch(lang, titleId.toString(), SketchType.CONTEXT, compressedJSONSketch);
    }

    protected MaxTFSketch loadSketch(String lang, String titleId, SketchType type, boolean compressedJSON) throws
            IOException, ClassNotFoundException {
        MaxTFSketch s = null;
        String sketchFile;
        if (type == SketchType.CONTEXT)
            sketchFile = this.buildContextSketchPath(lang, titleId);
        else if (type == SketchType.TITLE)
            sketchFile = this.buildTitleSketchPath(lang, titleId);
        else sketchFile = "";
        try {
            if (!compressedJSON) s = (MaxTFSketch) InputFileManager.deserializeObjectFromFile(sketchFile);
            else s = (MaxTFSketch) InputFileManager.deserializeObjectFromJSON(sketchFile, true, MaxTFSketch.class);
        } catch (IOException e) {
            //System.out.println("WARNING: Cannot find " + type.toString() + " sketch for " + titleId);
            return NOT_FOUND_SKETCH;
        }
        return s;
    }

    public boolean deleteSketch(String lang, String titleId, SketchType type) {
        String sketchFile;
        if (type == SketchType.CONTEXT)
            sketchFile = this.buildContextSketchPath(lang, titleId);
        else if (type == SketchType.TITLE)
            sketchFile = this.buildTitleSketchPath(lang, titleId);
        else sketchFile = "";

        File file = new File(sketchFile);
        if(file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    private String buildTitleSketchPath(String lang, String titleId) {
        return this.titleSketchPath + "/" + lang + "/" + titleId + ".sketch";
    }

    private String buildContextSketchPath(String lang, String titleId) {
        return this.contextSketchPath + "/" + lang + "/" + titleId + ".sketch";
    }

    public String saveTitleMeta(String lang, int titleId, SketchMetadata skMeta) throws IOException {
        return this.saveSketchMeta(lang, titleId+"", skMeta, SketchType.TITLE);
    }

    public String saveContextMeta(String lang, int titleId, SketchMetadata skMeta) throws IOException {
        return this.saveSketchMeta(lang, titleId+"", skMeta, SketchType.CONTEXT);
    }

    private String saveSketchMeta(String lang, String titleId, SketchMetadata skMeta, SketchType type) throws IOException {
        String metaFile;
        if (type == SketchType.CONTEXT)
            metaFile = this.buildContextMetaPath(lang, titleId);
        else
            metaFile = this.buildTitleMetaPath(lang, titleId);
        OutputFileManager.serializeObjectToFile(skMeta, metaFile);
        return metaFile;
    }

    public SketchMetadata loadTitleMeta(String lang, int titleId) throws ClassNotFoundException {
        return this.loadSketchMeta(lang, titleId+"", SketchType.TITLE);
    }

    public SketchMetadata loadContextMeta(String lang, int titleId) throws ClassNotFoundException {
        return this.loadSketchMeta(lang, titleId+"", SketchType.CONTEXT);
    }
    public SketchMetadata loadContextMeta(String lang, String titleId) throws ClassNotFoundException {
        return this.loadSketchMeta(lang, titleId, SketchType.CONTEXT);
    }

    protected SketchMetadata loadSketchMeta(String lang, String titleId, SketchType type) throws ClassNotFoundException {
        String metaFile;
        SketchMetadata s = null;
        if (type == SketchType.CONTEXT)
            metaFile = this.buildContextMetaPath(lang, titleId);
        else
            metaFile = this.buildTitleMetaPath(lang, titleId);
        try {
            s = (SketchMetadata) InputFileManager.deserializeObjectFromFile(metaFile);
        } catch (IOException e) {

            return NOT_FOUND_METADATA;
        }
        return s;
    }

    private String buildContextMetaPath(String lang, String titleId) {
        return this.contextMetaPath + "/" + lang + "/" + titleId + ".meta";
    }

    private String buildTitleMetaPath(String lang, String titleId) {
        return this.titleMetaPath + "/" + lang + "/" + titleId + ".meta";
    }

    public enum SketchType {TITLE, CONTEXT}
}