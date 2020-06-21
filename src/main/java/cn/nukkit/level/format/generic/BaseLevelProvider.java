package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.LevelException;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.SneakyThrows;
/*
Wool Format
    * 3 bytes - header
    * 1 byte - version (byte)
    * 2 bytes - minx (short)
    * 2 bytes - minz (short)
    * 2 bytes - width (short)
    * WoolChunk Bitmask
    * 4 bytes - Compressed Chunks Size (int)
    * 4 bytes - Uncompressed Chunks Size (int)
    * Chunks (Compressed with Zstd)
        * WoolChunkSection[]
    * 4 bytes - Compressed BlockEntities Size
    * 4 bytes - Uncompressed BlockEntities Size
    * BlockEntities (tiles) (Compressed with Zstd)
        * Array of BlockEntity Compounds
    * 1 byte - has entities (boolean)
    * 4 bytes - Compressed Entities Size
    * 4 bytes - Uncompressed Entities Size
    * Entities (entities) (Compressed with Zstd)
        * Array of Entity Compounds
    * 4 bytes - Compressed "extra" size
    * 4 bytes - Uncompressed "extra" size
    * Extra CompoundTag (Compressed with Zstd)
    * 4 bytes - Compressed "levelData" size
    * 4 bytes - Uncompressed "levelData" size
    * LevelData CompoundTag (Compressed with Zstd)

WoolChunk Format
    * 256 ints - heightmap
    * 256 bytes - biomes
    * 2 bytes - sections bitmask (bottom to top)
    * 2048 bytes - block light
    * 4096 bytes - blocks
    * 2048 bytes - data
    * 2048 bytes - skylight
    *
*/
/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BaseLevelProvider implements LevelProvider {

    protected final String path;

    protected final AtomicReference<BaseRegionLoader> lastRegion = new AtomicReference<>();

    protected final Long2ObjectMap<BaseRegionLoader> regions = new Long2ObjectOpenHashMap<>();

    protected final Long2ObjectMap<BaseFullChunk> chunks = new Long2ObjectOpenHashMap<>();

    protected final AtomicReference<BaseFullChunk> lastChunk = new AtomicReference<>();

    protected Level level;

    protected CompoundTag levelData;

    protected Vector3 spawn;

    public BaseLevelProvider(final Level level, final String path, final String name) throws IOException {
        this.level = level;
        this.path = path;
        final File file_path = new File(this.path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }
        final CompoundTag levelData = NBTIO.readCompressed(new FileInputStream(new File(this.path + "level.dat")), ByteOrder.BIG_ENDIAN);
        if (levelData.get("Data") instanceof CompoundTag) {
            this.levelData = levelData.getCompound("Data");
        } else {
            throw new LevelException("Invalid level.dat");
        }

        if (!this.levelData.contains("generatorName")) {
            this.levelData.putString("generatorName", Generator.getGenerator("DEFAULT").getSimpleName().toLowerCase(Locale.ENGLISH));
        }

        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorOptions", "");
        }

        this.spawn = new Vector3(this.levelData.getInt("SpawnX"), this.levelData.getInt("SpawnY"), this.levelData.getInt("SpawnZ"));
    }

    // Wool Provider
    public BaseLevelProvider(final Level level, final String name, boolean w) {
        this.level = level;
        this.path = null;
    }

    // Wool Provider
    public BaseLevelProvider(final Level level, final String path, final String name, boolean w) {
        this.level = level;
        this.path = path;
        final File file_path = new File(this.path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }
        final File file = new File(this.path + "/" + name + ".wool");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static int getRegionIndexX(final int chunkX) {
        return chunkX >> 5;
    }

    protected static int getRegionIndexZ(final int chunkZ) {
        return chunkZ >> 5;
    }

    public abstract BaseFullChunk loadChunk(long index, int chunkX, int chunkZ, boolean create);

    public int size() {
        synchronized (this.chunks) {
            return this.chunks.size();
        }
    }

    public void putChunk(final long index, final BaseFullChunk chunk) {
        synchronized (this.chunks) {
            this.chunks.put(index, chunk);
        }
    }

    public BaseRegionLoader getRegion(final int x, final int z) {
        final long index = Level.chunkHash(x, z);
        synchronized (this.regions) {
            return this.regions.get(index);
        }
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getGenerator() {
        return this.levelData.getString("generatorName");
    }

    @Override
    public Map<String, Object> getGeneratorOptions() {
        return new HashMap<String, Object>() {
            {
                this.put("preset", BaseLevelProvider.this.levelData.getString("generatorOptions"));
            }
        };
    }

    @Override
    public BaseFullChunk getLoadedChunk(final int chunkX, final int chunkZ) {
        BaseFullChunk tmp = this.lastChunk.get();
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        final long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            this.lastChunk.set(tmp = this.chunks.get(index));
        }
        return tmp;
    }

    @Override
    public BaseFullChunk getLoadedChunk(final long hash) {
        BaseFullChunk tmp = this.lastChunk.get();
        if (tmp != null && tmp.getIndex() == hash) {
            return tmp;
        }
        synchronized (this.chunks) {
            this.lastChunk.set(tmp = this.chunks.get(hash));
        }
        return tmp;
    }

    @Override
    public BaseFullChunk getChunk(final int chunkX, final int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Override
    public BaseFullChunk getChunk(final int x, final int z, final boolean create) {
        BaseFullChunk tmp = this.lastChunk.get();
        if (tmp != null && tmp.getX() == x && tmp.getZ() == z) {
            return tmp;
        }
        final long index = Level.chunkHash(x, z);
        synchronized (this.chunks) {
            this.lastChunk.set(tmp = this.chunks.get(index));
        }
        if (tmp != null) {
            return tmp;
        } else {
            tmp = this.loadChunk(index, x, z, create);
            this.lastChunk.set(tmp);
            return tmp;
        }
    }

    @Override
    public void saveChunks() {
        synchronized (this.chunks) {
            for (final BaseFullChunk chunk : this.chunks.values()) {
                if (chunk.getChanges() != 0) {
                    chunk.setChanged(false);
                    this.saveChunk(chunk.getX(), chunk.getZ());
                }
            }
        }
    }

    @Override
    public void unloadChunks() {
        final ObjectIterator<BaseFullChunk> iter = this.chunks.values().iterator();
        while (iter.hasNext()) {
            iter.next().unload(true, false);
            iter.remove();
        }
    }

    @Override
    public boolean loadChunk(final int chunkX, final int chunkZ) {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    @Override
    public boolean loadChunk(final int chunkX, final int chunkZ, final boolean create) {
        final long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            if (this.chunks.containsKey(index)) {
                return true;
            }
        }
        return this.loadChunk(index, chunkX, chunkZ, create) != null;
    }

    @Override
    public boolean unloadChunk(final int X, final int Z) {
        return this.unloadChunk(X, Z, true);
    }

    @Override
    public boolean unloadChunk(final int X, final int Z, final boolean safe) {
        final long index = Level.chunkHash(X, Z);
        synchronized (this.chunks) {
            final BaseFullChunk chunk = this.chunks.get(index);
            if (chunk != null && chunk.unload(false, safe)) {
                this.lastChunk.set(null);
                this.chunks.remove(index, chunk);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isChunkGenerated(final int chunkX, final int chunkZ) {
        final BaseRegionLoader region = this.getRegion(chunkX >> 5, chunkZ >> 5);
        return region != null && region.chunkExists(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32) && this.getChunk(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32, true).isGenerated();
    }

    @Override
    public boolean isChunkPopulated(final int chunkX, final int chunkZ) {
        final BaseFullChunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.isPopulated();
    }

    @Override
    public boolean isChunkLoaded(final int X, final int Z) {
        return this.isChunkLoaded(Level.chunkHash(X, Z));
    }

    @Override
    public boolean isChunkLoaded(final long hash) {
        synchronized (this.chunks) {
            return this.chunks.containsKey(hash);
        }
    }

    @Override
    public void setChunk(final int chunkX, final int chunkZ, final FullChunk chunk) {
        if (!(chunk instanceof BaseFullChunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        chunk.setProvider(this);
        chunk.setPosition(chunkX, chunkZ);
        final long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            if (this.chunks.containsKey(index) && !this.chunks.get(index).equals(chunk)) {
                this.unloadChunk(chunkX, chunkZ, false);
            }
            this.chunks.put(index, (BaseFullChunk) chunk);
        }
    }

    @Override
    public String getName() {
        return this.levelData.getString("LevelName");
    }

    @Override
    public boolean isRaining() {
        return this.levelData.getBoolean("raining");
    }

    @Override
    public void setRaining(final boolean raining) {
        this.levelData.putBoolean("raining", raining);
    }

    @Override
    public int getRainTime() {
        return this.levelData.getInt("rainTime");
    }

    @Override
    public void setRainTime(final int rainTime) {
        this.levelData.putInt("rainTime", rainTime);
    }

    @Override
    public boolean isThundering() {
        return this.levelData.getBoolean("thundering");
    }

    @Override
    public void setThundering(final boolean thundering) {
        this.levelData.putBoolean("thundering", thundering);
    }

    @Override
    public int getThunderTime() {
        return this.levelData.getInt("thunderTime");
    }

    @Override
    public void setThunderTime(final int thunderTime) {
        this.levelData.putInt("thunderTime", thunderTime);
    }

    @Override
    public long getCurrentTick() {
        return this.levelData.getLong("Time");
    }

    @Override
    public void setCurrentTick(final long currentTick) {
        this.levelData.putLong("Time", currentTick);
    }

    @Override
    public long getTime() {
        return this.levelData.getLong("DayTime");
    }

    @Override
    public void setTime(final long value) {
        this.levelData.putLong("DayTime", value);
    }

    @Override
    public long getSeed() {
        return this.levelData.getLong("RandomSeed");
    }

    @Override
    public void setSeed(final long value) {
        this.levelData.putLong("RandomSeed", value);
    }

    @Override
    public Vector3 getSpawn() {
        return this.spawn;
    }

    @Override
    public void setSpawn(final Vector3 pos) {
        this.levelData.putInt("SpawnX", (int) pos.x);
        this.levelData.putInt("SpawnY", (int) pos.y);
        this.levelData.putInt("SpawnZ", (int) pos.z);
        this.spawn = pos;
    }

    @Override
    public Map<Long, BaseFullChunk> getLoadedChunks() {
        synchronized (this.chunks) {
            return ImmutableMap.copyOf(this.chunks);
        }
    }

    @Override
    public void doGarbageCollection() {
        final int limit = (int) (System.currentTimeMillis() - 50);
        synchronized (this.regions) {
            if (this.regions.isEmpty()) {
                return;
            }

            final ObjectIterator<BaseRegionLoader> iter = this.regions.values().iterator();
            while (iter.hasNext()) {
                final BaseRegionLoader loader = iter.next();

                if (loader.lastUsed <= limit) {
                    try {
                        loader.close();
                    } catch (final IOException e) {
                        throw new RuntimeException("Unable to close RegionLoader", e);
                    }
                    this.lastRegion.set(null);
                    iter.remove();
                }

            }
        }
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public synchronized void close() {
        this.unloadChunks();
        synchronized (this.regions) {
            final ObjectIterator<BaseRegionLoader> iter = this.regions.values().iterator();

            while (iter.hasNext()) {
                try {
                    iter.next().close();
                } catch (final IOException e) {
                    throw new RuntimeException("Unable to close RegionLoader", e);
                }
                this.lastRegion.set(null);
                iter.remove();
            }
        }
        this.level = null;
    }

    @Override
    public void saveLevelData() {
        try {
            NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", this.levelData), new FileOutputStream(this.getPath() + "level.dat"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateLevelName(final String name) {
        if (!this.getName().equals(name)) {
            this.levelData.putString("LevelName", name);
        }
    }

    @Override
    public GameRules getGamerules() {
        final GameRules rules = GameRules.getDefault();

        if (this.levelData.contains("GameRules")) {
            rules.readNBT(this.levelData.getCompound("GameRules"));
        }

        return rules;
    }

    @Override
    public void setGameRules(final GameRules rules) {
        this.levelData.putCompound("GameRules", rules.writeNBT());
    }

    public Server getServer() {
        return this.level.getServer();
    }

    public CompoundTag getLevelData() {
        return this.levelData;
    }

}
