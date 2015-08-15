package cn.nukkit.level.format;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;

import java.io.IOException;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface LevelProvider {
    byte ORDER_YZX = 0;
    byte ORDER_ZXY = 1;

    static String getProviderName() {
        return null;
    }

    static int getProviderOrder() {
        return ORDER_ZXY;
    }

    static boolean usesChunkSection() {
        return true;
    }

    AsyncTask requestChunkTask(int x, int z) throws IOException;

    String getPath();

    /*static boolean isValid(String path) {
        return false;
    }

    static void generate(String path, String name, int seed, String generator) {
        generate(path, name, seed, generator, new String[]{});
    }

    static void generate(String path, String name, int seed, String generator, String[] options) {

    }*/

    String getGenerator();

    TreeMap<String, String> getGeneratorOptions();

    FullChunk getChunk(int X, int Z) throws IOException;

    FullChunk getChunk(int X, int Z, boolean create) throws IOException;

    void saveChunks() throws Exception;

    void saveChunk(int X, int Z) throws Exception;

    public void unloadChunks() throws Exception;

    boolean loadChunk(int X, int Z) throws IOException;

    boolean loadChunk(int X, int Z, boolean create) throws IOException;

    boolean unloadChunk(int X, int Z) throws Exception;

    boolean unloadChunk(int X, int Z, boolean safe) throws Exception;

    boolean isChunkGenerated(int X, int Z) throws IOException;

    boolean isChunkPopulated(int X, int Z) throws IOException;

    boolean isChunkLoaded(int X, int Z);

    void setChunk(int chunkX, int chunkZ, FullChunk chunk) throws Exception;

    String getName();

    long getTime();

    void setTime(int value);

    long getSeed();

    void setSeed(long value);

    Vector3 getSpawn();

    void setSpawn(Vector3 pos);

    TreeMap<String, ? extends FullChunk> getLoadedChunks();

    void doGarbageCollection() throws IOException;

    Level getLevel();

    void close() throws Exception;
}
