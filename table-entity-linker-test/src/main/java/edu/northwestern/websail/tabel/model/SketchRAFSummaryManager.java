package edu.northwestern.websail.tabel.model;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import edu.northwestern.websail.datastructure.sketch.CountMinSketch;
import edu.northwestern.websail.tabel.config.GlobalConfig;
import edu.northwestern.websail.tabel.io.InputFileManager;
import edu.northwestern.websail.tabel.io.OutputFileManager;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class SketchRAFSummaryManager extends SketchSummaryManager implements
        Closeable {

    protected final RAFDescriptor titleSketchRAFDesc;
    protected final RAFDescriptor contextSketchRAFDesc;

    protected final HashMap<String, Long> titleSketchPos;
    protected final HashMap<String, Long> titleSketchMetaPos;

    protected final HashMap<String, Long> contextSketchPos;
    protected final HashMap<String, Long> contextSketchMetaPos;

    protected Random rand = new Random();

    public SketchRAFSummaryManager(String titleSketchPrefix,
                                   String contextSketchPrefix,
                                    String titleSketchMetaPrefix,
                                   String contextSketchMetaPrefix) throws IOException {
        super();
        this.titleSketchPos = this.loadPosition(titleSketchPrefix + ".pos");
        this.titleSketchMetaPos = this.loadPosition(titleSketchMetaPrefix
                + ".pos");

        this.contextSketchPos = this.loadPosition(contextSketchPrefix + ".pos");
        this.contextSketchMetaPos = this.loadPosition(contextSketchMetaPrefix
                + ".pos");

        this.titleSketchRAFDesc = this.initializeRAF(
                titleSketchPrefix + ".raf", titleSketchMetaPrefix + ".raf");

        this.contextSketchRAFDesc = this.initializeRAF(
                contextSketchPrefix + ".raf", contextSketchMetaPrefix + ".raf");
    }

    public SketchRAFSummaryManager(String titleSketchMetaPrefix,
                                   String contextSketchMetaPrefix
                                   ) throws IOException {
        super();
        this.titleSketchMetaPos = this.loadPosition(titleSketchMetaPrefix
                + ".pos");

        this.contextSketchMetaPos = this.loadPosition(contextSketchMetaPrefix
                + ".pos");
        this.titleSketchPos = null;
        this.contextSketchPos = null;
        this.titleSketchRAFDesc = null;
        this.contextSketchRAFDesc = null;
    }

    private final RAFDescriptor initializeRAF(String rafSketchFile, String rafMetaFile) throws IOException {
        RAFDescriptor descriptor =  new RAFDescriptor(rafSketchFile, rafMetaFile);
        return descriptor;
    }

    public final HashMap<String, Long> loadPosition(String filename) {
        InputFileManager inMgr = new InputFileManager(filename);
        HashMap<String, Long> positionMap = new HashMap<String, Long>();
        String line = null;
        while ((line = inMgr.readLine()) != null) {
            if (line.trim().equals(""))
                continue;
            String[] parts = line.split("\t");
            positionMap.put(parts[0], Long.valueOf(parts[1]));
        }
        inMgr.close();
        return positionMap;
    }

    private RAFDescriptor getDescriptor(SketchType type) {
        if (type == SketchType.TITLE)
            return this.titleSketchRAFDesc;
        if (type == SketchType.CONTEXT)
            return this.contextSketchRAFDesc;
        return null;
    }

    private HashMap<String, Long> getSketchPositionMap(SketchType type) {
        if (type == SketchType.TITLE)
            return this.titleSketchPos;
        if (type == SketchType.CONTEXT)
            return this.contextSketchPos;
        return null;
    }

    private HashMap<String, Long> getMetaPositionMap(SketchType type) {
        if (type == SketchType.TITLE)
            return this.titleSketchMetaPos;
        if (type == SketchType.CONTEXT)
            return this.contextSketchMetaPos;
        return null;
    }

    @Override
    protected MaxTFSketch loadSketch(String lang, String titleId,
                                     SketchType type, boolean compressedJSON) {
        RAFDescriptor desc = this.getDescriptor(type);
        if (desc == null)
            return NOT_FOUND_SKETCH;
        HashMap<String, Long> positionMap = this.getSketchPositionMap(type);
        if (positionMap == null)
            return NOT_FOUND_SKETCH;
        Long pos = positionMap.get(titleId);
        if (pos == null)
            return NOT_FOUND_SKETCH;
        MaxTFSketch sk = null;
        try {
            sk = desc.loadSketch(pos);
        } catch (IOException e) {

        }
        if (sk == null)
            return NOT_FOUND_SKETCH;
        return sk;

    }

    @Override
    protected SketchMetadata loadSketchMeta(String lang, String titleId,
                                            SketchType type) {
        RAFDescriptor desc = this.getDescriptor(type);
        if (desc == null)
            return NOT_FOUND_METADATA;
        HashMap<String, Long> positionMap = this.getMetaPositionMap(type);
        if (positionMap == null)
            return NOT_FOUND_METADATA;
        Long pos = positionMap.get(titleId);
        if (pos == null)
            return NOT_FOUND_METADATA;
        SketchMetadata meta = null;
        try {
            meta = desc.loadMeta(pos);
        } catch (IOException e) {
        }
        if (meta == null)
            return NOT_FOUND_METADATA;
        return meta;
    }

    public void close() throws IOException {
        if (titleSketchRAFDesc != null) {
            titleSketchRAFDesc.close();
        }

        if (contextSketchRAFDesc != null) {
            contextSketchRAFDesc.close();
        }
    }

    public final static Kryo createKryoForSketch() {
        Serializer<TIntIntHashMap> s = new TIntIntHashMapSerializer();
        Kryo kryo = new Kryo();
        kryo.register(MaxTFSketch.class);
        kryo.register(CountMinSketch.class);
        kryo.register(TIntIntHashMap.class, s);
        kryo.register(SketchMetadata.class);
        kryo.register(HashSet.class);
        return kryo;
    }

    public static final class RAFDescriptor implements Closeable {
        final RandomAccessFile rafLocalSk;
        // final DataInputStream buffSk;
        // final Input inputSk;
        final RandomAccessFile rafLocalMeta;
        // final DataInputStream buffMeta;
        // final Input inputMeta;
        final Kryo skKryo;
        final Kryo metaKryo;
        long usage;

        public RAFDescriptor(String rafSketchFile, String rafMetaFile)
                throws IOException {
            rafLocalSk = new RandomAccessFile(rafSketchFile, "r");
            // buffSk = new DataInputStream(new BufferedInputStream(
            // new FileInputStream(rafLocalSk.getFD())));
            // inputSk = new Input(buffSk);

            rafLocalMeta = new RandomAccessFile(rafMetaFile, "r");
            // buffMeta = new DataInputStream(new BufferedInputStream(
            // new FileInputStream(rafLocalMeta.getFD())));
            // inputMeta = new Input(buffMeta);
            skKryo = createKryoForSketch();
            metaKryo = createKryoForSketch();
            usage = 0;
        }

        public MaxTFSketch loadSketch(long position)
                throws IOException {
            MaxTFSketch sk = SketchSummaryManager.NOT_FOUND_SKETCH;
            usage++;
            DataInputStream buffSk = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(
                            rafLocalSk.getFD())));
            Input inputSk = new Input(buffSk);
            this.rafLocalSk.seek(position);
            sk = skKryo.readObject(inputSk, MaxTFSketch.class);
            usage--;
            inputSk = null;
            buffSk = null;
            return sk;
        }

        public SketchMetadata loadMeta(long position)
                throws IOException {
            SketchMetadata metadata = SketchSummaryManager.NOT_FOUND_METADATA;
            usage++;
            DataInputStream buffMeta = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(
                            rafLocalMeta.getFD())));
            Input inputMeta = new Input(buffMeta);

            this.rafLocalMeta.seek(position);
            metadata = metaKryo.readObject(inputMeta, SketchMetadata.class);
            usage--;
            buffMeta = null;
            inputMeta = null;
            return metadata;
        }

        @Override
        public void close() throws IOException {
            if (rafLocalSk != null)
                rafLocalSk.close();
            if (rafLocalMeta != null)
                rafLocalMeta.close();
            // if (buffSk != null)
            // buffSk.close();
            // if (buffMeta != null)
            // buffMeta.close();
            // if (inputSk != null)
            // inputSk.close();
            // if (inputMeta != null)
            // inputMeta.close();
        }

    }

    public final static SketchRAFSummaryManager create(String lang)
            throws IOException {
        return new SketchRAFSummaryManager(
                GlobalConfig.titleSketchSummaryRAFDirectory + "/" + lang,
                GlobalConfig.contextSketchSummaryRAFDirectory + "/"
                        + lang,
                GlobalConfig.titleSketchMetaRAFDirectory + "/" + lang,
                GlobalConfig.contextSktechMetaRAFDirectory + "/" + lang);
    }

    public final static void createSketchRAF(File directory,
                                             String outputFileName, SketchType type, String language)
            throws IOException, ClassNotFoundException {
        SketchSummaryManager skMgr = new SketchSummaryManager();
        File rafObject = new File(outputFileName + ".raf");
        if (rafObject.exists()) {
            //logger.severe(outputFileName + " already exists.");
            return;
        }
        rafObject.createNewFile();

        DataOutputStream fileStream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputFileName
                        + ".raf")));
        OutputFileManager positionOut = new OutputFileManager(outputFileName
                + ".pos");
        Kryo kryo = createKryoForSketch();
        Output output = new Output(fileStream);
        long bytes = 0l;
        // int count = 0;
        for (final File fileEntry : directory.listFiles()) {
            if (fileEntry.isDirectory()
                    || fileEntry.getName().equals("")
                    || fileEntry.getName().charAt(0) == '.'
                    || !fileEntry.getName()
                    .substring(fileEntry.getName().length() - 6)
                    .equalsIgnoreCase("sketch")) {
                continue;
            }
            String title = fileEntry.getName().substring(0,
                    fileEntry.getName().length() - 7);
            output.flush();
            bytes += output.total();
            positionOut.println(title + "\t" + bytes);
            output.clear();
            MaxTFSketch sk = skMgr.loadSketch(language, title, type, false);
            kryo.writeObject(output, sk);
            // if (count % 100000 == 0)
            // System.out.println();
            // if (count % 1000 == 0)
            // System.out.print(".");
            // count++;
        }
        output.close();
        fileStream.close();
        positionOut.close();
    }

    public final static void createSketchMetaRAF(File directory,
                                                 String outputFileName, SketchType type, String language)
            throws IOException, ClassNotFoundException {
        SketchSummaryManager skMgr = new SketchSummaryManager();
        File rafObject = new File(outputFileName + ".raf");
        if (rafObject.exists()) {
            //logger.severe(outputFileName + " already exists.");
            return;
        }
        rafObject.createNewFile();

        DataOutputStream fileStream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputFileName
                        + ".raf")));
        OutputFileManager positionOut = new OutputFileManager(outputFileName
                + ".pos");
        Kryo kryo = createKryoForSketch();
        Output output = new Output(fileStream);
        long bytes = 0l;
        //int count = 0;
        for (final File fileEntry : directory.listFiles()) {
            if (fileEntry.isDirectory()
                    || fileEntry.getName().equals("")
                    || fileEntry.getName().charAt(0) == '.'
                    || !fileEntry.getName()
                    .substring(fileEntry.getName().length() - 4)
                    .equalsIgnoreCase("meta")) {
                continue;
            }
            String title = fileEntry.getName().substring(0,
                    fileEntry.getName().length() - 5);
            output.flush();
            bytes += output.total();
            positionOut.println(title + "\t" + bytes);
            output.clear();
            SketchMetadata sk = skMgr.loadSketchMeta(language, title, type);
            kryo.writeObject(output, sk);

//			if (count % 100000 == 0)
//				System.out.println();
//			if (count % 1000 == 0)
//				System.out.print(".");
//			count++;
        }
        output.close();
        fileStream.close();
        positionOut.close();
    }

    private final static class TIntIntHashMapSerializer extends
            Serializer<TIntIntHashMap> {

        @Override
        public void write(Kryo kryo, Output output, TIntIntHashMap object) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(output);
                oos.writeObject(object);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public TIntIntHashMap read(Kryo kryo, Input input,
                                   Class<TIntIntHashMap> type) {
            try {
                ObjectInputStream ois = new ObjectInputStream(input);
                Object object = ois.readObject();
                return (TIntIntHashMap) object;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new TIntIntHashMap();
        }
    }

}
