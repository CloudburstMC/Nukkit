package cn.nukkit.level.format;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class LevelProvider {
    public LevelProvider(Level level, String path) {

    }

    public abstract String getProvideName();

    public abstract int getProviderOrder();

    public abstract boolean usesChunkSection();

    //todo requestChunkTask(int x, int z)

    public abstract String getPath();

    public abstract boolean isValid(String path);

    public abstract void generate(String path, String name, int seed, String generator, Object[] options);

    public abstract String getGenerator();

    public abstract Object[] getGeneratorOptions();

    //todo getChunk

    //todo createChunkSection

    public abstract void saveChunks();

    public abstract void saveChunk(int X, int Z);

    public abstract void unloadChunks();

    public boolean loadChunk(int X, int Z) {
        return this.loadChunk(X, Z, false);
    }

    public abstract boolean loadChunk(int X, int Z, boolean create);

    public boolean unloadChunk(int X, int Z) {
        return this.unloadChunk(X, Z, false);
    }

    public abstract boolean unloadChunk(int X, int Z, boolean safe);

    public abstract boolean isChunkGenerated(int X, int Z);

    public abstract boolean isChunkPopulated(int X, int Z);

    public abstract boolean isChunkLoaded(int X, int Z);

    //todo setChunk

    public abstract String getName();

    public abstract long getTime();

    public abstract void setTime(long value);

    public abstract long getSeed();

    public abstract void setSeed(long value);

    public abstract Vector3 getSpawn();

    public abstract void setSpawn(Vector3 pos);

    //todo getLoadedChunks

    public abstract Level getLevel();

    public abstract void close();

}
