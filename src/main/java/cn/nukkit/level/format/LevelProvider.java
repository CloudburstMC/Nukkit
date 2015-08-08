package cn.nukkit.level.format;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class LevelProvider {
    public static final byte ORDER_YZX = 0;
    public static final byte ORDER_ZXY = 1;

    public LevelProvider(Level level, String path) {
    }

    public static String getProviderName() {
        return null;
    }

    public static int getProviderOrder() {
        return 0;
    }

    public static boolean usesChunkSection() {
        return false;
    }

    public abstract AsyncTask requestChunkTask(int x, int z);

    public abstract String getPath();

    public static boolean isValid(String path) {
        return true;
    }

    public static void generate(String path, String name, int seed, String generator) {
        generate(path, name, seed, generator, new String[]{});
    }

    public static void generate(String path, String name, int seed, String generator, String[] options) {

    }

    public abstract String getGenerator();

    public abstract String[] getGeneratorOptions();

    public abstract FullChunk getChunk(int X, int Z);

    public abstract FullChunk getChunk(int X, int Z, boolean create);

    public static ChunkSection createChunkSection(int Y) {
        return null;
    }

    public abstract void saveChunks();

    public abstract void saveChunk(int X, int Z);

    public abstract void loadChunk(int X, int Z);

    public abstract void loadChunk(int X, int Z, boolean create);

    public abstract boolean unloadChunk(int X, int Z);

    public abstract boolean unloadChunk(int X, int Z, boolean safe);

    public abstract boolean isChunkGenerated(int X, int Z);

    public abstract boolean isChunkPopulated(int X, int Z);

    public abstract boolean isChunkLoaded(int X, int Z);

    public abstract Object setChunk(int chunkX, int chunkZ, FullChunk chunk);

    public abstract String getName();

    public abstract long getTime();

    public abstract void setTime(long value);

    public abstract long getSeed();

    public abstract void setSeed(long value);

    public abstract Vector3 getSpawn();

    public abstract void setSpawn(Vector3 pos);

    public abstract FullChunk[] getLoadedChunks();

    public abstract Level getLevel();

    public abstract void close();
}
