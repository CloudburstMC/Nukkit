package cn.nukkit.level.format;

import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface LevelProvider {
    byte ORDER_YZX = 0;
    byte ORDER_ZXY = 1;

    /**
     * The path to the folder that this level is stored in
     * @return
     */
    String getPath();

    /**
     * The name of the generator used by this level
     * @return
     */
    String getGenerator();

    /**
     * The options passed to the generator
     * @return
     */
    Map<String, Object> getGeneratorOptions();

    /**
     * Gets a chunk from memory that's already loaded
     * @param X the X coordinate of the chunk
     * @param Z the Z coordinate of the chunk
     * @return
     */
    BaseFullChunk getLoadedChunk(int X, int Z);

    /**
     * Gets a chunk from memory that's already loaded
     * @param hash The long-encoded chunk position
     * @return
     */
    BaseFullChunk getLoadedChunk(long hash);

    /**
     * Get a chunk at a position
     * @param X the X coordinate of the chunk
     * @param Z the Z coordinate of the chunk
     * @return
     */
    BaseFullChunk getChunk(int X, int Z);

    /**
     * Get a chunk at a position
     * @param X the X coordinate of the chunk
     * @param Z the Z coordinate of the chunk
     * @param create if true, a new chunk will be created if there isn't already a chunk there
     * @return
     */
    BaseFullChunk getChunk(int X, int Z, boolean create);

    /**
     * Write all loaded chunks to disk
     */
    void saveChunks();

    /**
     * Save a specific chunk to disk
     * @param X
     * @param Z
     */
    void saveChunk(int X, int Z);

    /**
     * Save the data in the given chunks to a specific position
     * @param X
     * @param Z
     * @param chunk
     */
    void saveChunk(int X, int Z, FullChunk chunk);

    /**
     * Unload all loaded chunks
     */
    void unloadChunks();

    /**
     * Load a chunk at a specific position
     * @param X
     * @param Z
     * @return
     */
    boolean loadChunk(int X, int Z);

    /**
     * Load a chunk at a specific position
     * @param X
     * @param Z
     * @param create if true and the chunk doesn't exist, a new blank one will be created and generated
     * @return
     */
    boolean loadChunk(int X, int Z, boolean create);

    /**
     * Unload a chunk at a specific location
     * @param X
     * @param Z
     * @return
     */
    boolean unloadChunk(int X, int Z);

    /**
     * Unload a chunk at a specific location
     * @param X
     * @param Z
     * @param safe
     * @return
     */
    boolean unloadChunk(int X, int Z, boolean safe);

    /**
     * Whether or not the chunk at the given position is generated
     * @param X
     * @param Z
     * @return
     */
    boolean isChunkGenerated(int X, int Z);

    /**
     * Whether or not the chunk at the given position is populated
     * @param X
     * @param Z
     * @return
     */
    boolean isChunkPopulated(int X, int Z);

    /**
     * Whether or not the chunk at the given position is loaded
     * @param X
     * @param Z
     * @return
     */
    boolean isChunkLoaded(int X, int Z);

    /**
     * Whether or not the chunk at the given position is loaded
     * @param hash
     * @return
     */
    boolean isChunkLoaded(long hash);

    /**
     * Sets a chunk in the world
     * @param chunkX
     * @param chunkZ
     * @param chunk
     */
    void setChunk(int chunkX, int chunkZ, FullChunk chunk);

    /**
     * The name of this level
     * @return
     */
    String getName();

    /**
     * Whether or not it's currently raining
     * @return
     */
    boolean isRaining();

    /**
     * Make it rain!
     * @param raining
     */
    void setRaining(boolean raining);

    /**
     * How many ticks it's going to rain for
     * @return
     */
    int getRainTime();

    /**
     * Make it rain for a certain number of ticks
     * @param rainTime
     */
    void setRainTime(int rainTime);

    /**
     * Whether or not there's currently a thunderstorm
     * @return
     */
    boolean isThundering();

    /**
     * Make it thunder!
     * @param thundering
     */
    void setThundering(boolean thundering);

    /**
     * How many ticks the thunderstorm will last
     * @return
     */
    int getThunderTime();

    /**
     * Set the length of the thunderstorm
     * @param thunderTime
     */
    void setThunderTime(int thunderTime);

    /**
     * What tick the level is currently on
     * @return
     */
    long getCurrentTick();

    /**
     * Sets the level's current tick
     * @param currentTick
     */
    void setCurrentTick(long currentTick);

    /**
     * Get the current time in the level
     * @return
     */
    long getTime();

    /**
     * Sets the current time in the level
     * @param value
     */
    void setTime(long value);

    /**
     * Get the level's seed
     * @return
     */
    long getSeed();

    /**
     * Set the level's seed
     * @param value
     */
    void setSeed(long value);

    /**
     * Get the position in the world that new players will spawn at
     * @return
     */
    Vector3 getSpawn();

    /**
     * Set the spawn position for new players
     * @param pos
     */
    void setSpawn(Vector3 pos);

    /**
     * Get a collection of all currently loaded chunks
     * @return
     */
    Long2ObjectMap<? extends FullChunk> getLoadedChunks();

    /**
     * Unload unused chunks
     */
    void doGarbageCollection();

    /**
     * Unload unused chunks
     * @param time the maximum number of milliseconds to spend
     */
    default void doGarbageCollection(long time) {

    }

    /**
     * Get the actual Level instance that this is backing
     * @return
     */
    Level getLevel();

    /**
     * Unload this level
     */
    void close();

    /**
     * Write information about this level to disk
     */
    void saveLevelData();

    /**
     * Change this level's current name
     * @param name
     */
    void updateLevelName(String name);

    /**
     * Get this level's game rules
     * @return
     */
    GameRules getGamerules();

    /**
     * Set this level's game rules
     * @param rules
     */
    void setGameRules(GameRules rules);

    /**
     * Get an iterator over all currently loaded chunks
     * @return
     */
    ObjectIterator<? extends FullChunk> getLoadedChunkIterator();
}
