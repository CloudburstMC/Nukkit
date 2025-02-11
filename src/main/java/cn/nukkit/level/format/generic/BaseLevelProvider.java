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
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public abstract class BaseLevelProvider implements LevelProvider {

    protected Level level;

    protected final String path;

    protected CompoundTag levelData;

    private Vector3 spawn;

    protected final AtomicReference<BaseRegionLoader> lastRegion = new AtomicReference<>();

    protected final Long2ObjectMap<BaseRegionLoader> regions = new Long2ObjectOpenHashMap<>();

    protected final Long2ObjectMap<BaseFullChunk> chunks = new Long2ObjectOpenHashMap<>();

    private final AtomicReference<BaseFullChunk> lastChunk = new AtomicReference<>();

    public BaseLevelProvider(Level level, String path) throws IOException {
        this.level = level;
        this.path = path;

        File file_path = new File(this.path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }

        CompoundTag levelData = null;
        File levelDat = new File(this.path + "level.dat");

        try {
            levelData = NBTIO.readCompressed(Files.newInputStream(levelDat.toPath()), ByteOrder.BIG_ENDIAN);
        } catch (Exception ex1) {
            Server.getInstance().getLogger().error("Failed to load level.dat in " + file_path.getPath(), ex1);

            try {
                File backup = new File(this.path + "level.dat_old");

                if (backup.exists()) {
                    Server.getInstance().getLogger().warning("Attempting to load level.dat_old in " + file_path.getPath());

                    // Save a copy of the corrupted one
                    com.google.common.io.Files.copy(levelDat, new File(this.path + "level.dat_invalid"));

                    // Replace the corrupted one with a backup
                    com.google.common.io.Files.copy(backup, levelDat);

                    levelData = NBTIO.readCompressed(Files.newInputStream(levelDat.toPath()), ByteOrder.BIG_ENDIAN);
                }
            } catch (Exception ex2) {
                Server.getInstance().getLogger().error("Failed to load level.dat_old in " + file_path.getPath(), ex2);
            }
        }

        if (levelData != null && levelData.get("Data") instanceof CompoundTag) {
            this.levelData = levelData.getCompound("Data");
        } else {
            throw new LevelException("Invalid level.dat");
        }

        if (!this.levelData.contains("generatorName")) {
            this.levelData.putString("generatorName", Generator.getGenerator("DEFAULT").getSimpleName().toLowerCase(Locale.ROOT));
        }

        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorOptions", "");
        }

        this.spawn = new Vector3(this.levelData.getInt("SpawnX"), this.levelData.getInt("SpawnY"), this.levelData.getInt("SpawnZ"));
    }

    public abstract BaseFullChunk loadChunk(long index, int chunkX, int chunkZ, boolean create);

    public int size() {
        synchronized (chunks) {
            return this.chunks.size();
        }
    }

    @Override
    public void unloadChunks() {
        ObjectIterator<BaseFullChunk> iter = chunks.values().iterator();
        while (iter.hasNext()) {
            iter.next().unload(level.isSaveOnUnloadEnabled(), false);
            iter.remove();
        }
    }

    @Override
    public String getGenerator() {
        return this.levelData.getString("generatorName");
    }

    @Override
    public Map<String, Object> getGeneratorOptions() {
        return new HashMap<String, Object>() {
            {
                put("preset", levelData.getString("generatorOptions"));
            }
        };
    }

    @Override
    public Map<Long, BaseFullChunk> getLoadedChunks() {
        synchronized (chunks) {
            return ImmutableMap.copyOf(chunks);
        }
    }

    @Override
    public boolean isChunkLoaded(int X, int Z) {
        return isChunkLoaded(Level.chunkHash(X, Z));
    }

    public void putChunk(long index, BaseFullChunk chunk) {
        synchronized (chunks) {
            chunks.put(index, chunk);
        }
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        synchronized (chunks) {
            return this.chunks.containsKey(hash);
        }
    }

    public BaseRegionLoader getRegion(int x, int z) {
        long index = Level.chunkHash(x, z);
        synchronized (regions) {
            return this.regions.get(index);
        }
    }

    protected static int getRegionIndexX(int chunkX) {
        return chunkX >> 5;
    }

    protected static int getRegionIndexZ(int chunkZ) {
        return chunkZ >> 5;
    }

    @Override
    public String getPath() {
        return path;
    }

    public Server getServer() {
        return this.level.getServer();
    }

    @Override
    public Level getLevel() {
        return level;
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
    public void setRaining(boolean raining) {
        this.levelData.putBoolean("raining", raining);
    }

    @Override
    public int getRainTime() {
        return this.levelData.getInt("rainTime");
    }

    @Override
    public void setRainTime(int rainTime) {
        this.levelData.putInt("rainTime", rainTime);
    }

    @Override
    public boolean isThundering() {
        return this.levelData.getBoolean("thundering");
    }

    @Override
    public void setThundering(boolean thundering) {
        this.levelData.putBoolean("thundering", thundering);
    }

    @Override
    public int getThunderTime() {
        return this.levelData.getInt("thunderTime");
    }

    @Override
    public void setThunderTime(int thunderTime) {
        this.levelData.putInt("thunderTime", thunderTime);
    }

    @Override
    public long getCurrentTick() {
        return this.levelData.getLong("Time");
    }

    @Override
    public void setCurrentTick(long currentTick) {
        this.levelData.putLong("Time", currentTick);
    }

    @Override
    public long getTime() {
        return this.levelData.getLong("DayTime");
    }

    @Override
    public void setTime(long value) {
        this.levelData.putLong("DayTime", value);
    }

    @Override
    public long getSeed() {
        return this.levelData.getLong("RandomSeed");
    }

    @Override
    public void setSeed(long value) {
        this.levelData.putLong("RandomSeed", value);
    }

    @Override
    public Vector3 getSpawn() {
        return spawn;
    }

    @Override
    public void setSpawn(Vector3 pos) {
        this.levelData.putInt("SpawnX", (int) pos.x);
        this.levelData.putInt("SpawnY", (int) pos.y);
        this.levelData.putInt("SpawnZ", (int) pos.z);
        spawn = pos;
    }

    @Override
    public GameRules getGamerules() {
        GameRules rules = GameRules.getDefault();

        if (this.levelData.contains("GameRules"))
            rules.readNBT(this.levelData.getCompound("GameRules"));

        return rules;
    }

    @Override
    public void setGameRules(GameRules rules) {
        this.levelData.putCompound("GameRules", rules.writeNBT());
    }

    @Override
    public void doGarbageCollection() {
        int limit = (int) (System.currentTimeMillis() - 50);
        synchronized (regions) {
            if (regions.isEmpty()) {
                return;
            }

            ObjectIterator<BaseRegionLoader> iter = regions.values().iterator();
            while (iter.hasNext()) {
                BaseRegionLoader loader = iter.next();

                if (loader.lastUsed <= limit) {
                    try {
                        loader.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to close RegionLoader", e);
                    }
                    lastRegion.set(null);
                    iter.remove();
                }
            }
        }
    }

    @Override
    public void saveChunks() {
        synchronized (chunks) {
            for (BaseFullChunk chunk : this.chunks.values()) {
                if (chunk.hasChanged()) {
                    chunk.setChanged(false);
                    this.saveChunk(chunk.getX(), chunk.getZ(), chunk);
                }
            }
        }
    }

    public CompoundTag getLevelData() {
        return levelData;
    }

    @Override
    public void saveLevelData() {
        String file = this.path + "level.dat";
        File old = new File(file);
        if (old.exists()) {
            try {
                com.google.common.io.Files.copy(old, new File(file + ".bak"));
            } catch (IOException e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        try {
            NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", this.levelData), Files.newOutputStream(Paths.get(file)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLevelName(String name) {
        this.levelData.putString("LevelName", name);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ) {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (chunks) {
            if (this.chunks.containsKey(index)) {
                return true;
            }
        }
        return loadChunk(index, chunkX, chunkZ, create) != null;
    }

    @Override
    public boolean unloadChunk(int X, int Z) {
        return this.unloadChunk(X, Z, true);
    }

    @Override
    public boolean unloadChunk(int X, int Z, boolean safe) {
        long index = Level.chunkHash(X, Z);
        synchronized (chunks) {
            BaseFullChunk chunk = this.chunks.get(index);
            if (chunk != null && chunk.unload(false, safe)) {
                lastChunk.set(null);
                this.chunks.remove(index, chunk);
                return true;
            }
        }
        return false;
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Override
    public BaseFullChunk getLoadedChunk(int chunkX, int chunkZ) {
        BaseFullChunk tmp = lastChunk.get();
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (chunks) {
            lastChunk.set(tmp = chunks.get(index));
        }
        return tmp;
    }

    @Override
    public BaseFullChunk getLoadedChunk(long hash) {
        BaseFullChunk tmp = lastChunk.get();
        if (tmp != null && tmp.getIndex() == hash) {
            return tmp;
        }
        synchronized (chunks) {
            lastChunk.set(tmp = chunks.get(hash));
        }
        return tmp;
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ, boolean create) {
        BaseFullChunk tmp = lastChunk.get();
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (chunks) {
            lastChunk.set(tmp = chunks.get(index));
        }
        if (tmp == null) {
            tmp = this.loadChunk(index, chunkX, chunkZ, create);
            lastChunk.set(tmp);
        }
        return tmp;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof BaseFullChunk)) {
            throw new ChunkException("Invalid chunk class (" + chunkX + ", " + chunkZ + ')');
        }
        chunk.setProvider(this);
        chunk.setPosition(chunkX, chunkZ);
        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (chunks) {
            FullChunk oldChunk = this.chunks.get(index);
            if (oldChunk != null && !oldChunk.equals(chunk)) {
                this.unloadChunk(chunkX, chunkZ, false);
            }
            this.chunks.put(index, (BaseFullChunk) chunk);
        }
    }

    @Override
    public boolean isChunkPopulated(int chunkX, int chunkZ) {
        BaseFullChunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.isPopulated();
    }

    @Override
    public synchronized void close() {
        this.unloadChunks();
        synchronized (regions) {
            ObjectIterator<BaseRegionLoader> iter = this.regions.values().iterator();

            while (iter.hasNext()) {
                try {
                    iter.next().close();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to close RegionLoader", e);
                }
                lastRegion.set(null);
                iter.remove();
            }
        }
        this.level = null;
    }

    @Override
    public boolean isChunkGenerated(int chunkX, int chunkZ) {
        BaseRegionLoader region = this.getRegion(chunkX >> 5, chunkZ >> 5);
        BaseFullChunk chunk;
        return region != null && region.chunkExists(chunkX - (region.getX() << 5), chunkZ - (region.getZ() << 5)) &&
                (chunk = this.getChunk(chunkX - (region.getX() << 5), chunkZ - (region.getZ() << 5))) != null && chunk.isGenerated();
    }
}
