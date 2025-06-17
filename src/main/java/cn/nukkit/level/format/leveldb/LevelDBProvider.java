package cn.nukkit.level.format.leveldb;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.leveldb.serializer.*;
import cn.nukkit.level.format.leveldb.structure.BlockStateSnapshot;
import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunkSection;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.LevelException;
import cn.nukkit.utils.MainLogger;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.daporkchop.ldbjni.DBProvider;
import net.daporkchop.ldbjni.LevelDB;
import net.daporkchop.lib.natives.FeatureBuilder;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static cn.nukkit.level.format.leveldb.LevelDBConstants.*;

public class LevelDBProvider implements LevelProvider {

    private Level level;
    private final Path path;
    private final DB db;

    private final Long2ObjectMap<BaseFullChunk> chunks = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());

    private CompoundTag levelData;
    private Vector3 spawn;
    private Long cachedSeed;
    private int lastGcPosition = 0;

    private final ExecutorService executor;

    private volatile boolean closed;

    private static final DBProvider JAVA_LDB_PROVIDER = (DBProvider) FeatureBuilder.create(LevelDBProvider.class).addJava("net.daporkchop.ldbjni.java.JavaDBProvider").build();

    public LevelDBProvider(Level level, String path) throws IOException {
        this.level = level;
        this.path = Paths.get(path);

        Path dbPath = this.path.resolve("db");
        Files.createDirectories(dbPath);
        Preconditions.checkArgument(Files.isDirectory(dbPath), "db is not a directory");

        Options options = new Options()
                .createIfMissing(true)
                .compressionType(CompressionType.ZLIB_RAW)
                .cacheSize(1024L * 1024L * level.getServer().getConfig("leveldb.cache-size-mb", 80))
                .blockSize(64 * 1024);

        this.db = level.getServer().getConfig("leveldb.use-native", false) ?
                LevelDB.PROVIDER.open(dbPath.toFile(), options) : JAVA_LDB_PROVIDER.open(dbPath.toFile(), options);

        this.levelData = loadLevelData(this.path);

        if (!this.levelData.contains("generatorName")) {
            Class<?> generator = null;
            if (this.levelData.contains("Generator")) {
                generator = Generator.getGenerator(this.levelData.getInt("Generator"));
            }

            if (generator == null) {
                generator = Generator.getGenerator("DEFAULT");
            }
            this.levelData.putString("generatorName", generator.getSimpleName().toLowerCase(Locale.ROOT));
        }

        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorOptions", "");
        }

        this.spawn = new Vector3(this.levelData.getInt("SpawnX"), this.levelData.getInt("SpawnY"), this.levelData.getInt("SpawnZ"));

        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat("LevelDB Executor for " + this.getName());
        builder.setUncaughtExceptionHandler((thread, ex) -> {
            Server.getInstance().getLogger().error("Exception in " + thread.getName(), ex);
        });
        this.executor = Executors.newSingleThreadExecutor(builder.build());
    }

    @SuppressWarnings("unused")
    public static String getProviderName() {
        return "leveldb";
    }

    @SuppressWarnings("unused")
    public static boolean usesChunkSection() {
        return true;
    }

    public static boolean isValid(String path) {
        Path worldPath = Paths.get(path);
        return Files.exists(worldPath.resolve("level.dat")) && Files.exists(worldPath.resolve("db"));
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator) throws IOException {
        generate(path, name, seed, generator, new HashMap<>());
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator, Map<String, String> options) throws IOException {
        Path worldPath = Paths.get(path);
        Path dbPath = worldPath.resolve("db");

        if (!Files.isDirectory(dbPath)) {
            Files.createDirectories(dbPath);
        }

        CompoundTag levelData = new CompoundTag()
                .putInt("PM1EGen", (generator == cn.nukkit.level.generator.Void.class ? 0 : 2)) // Don't set for converted worlds
                .putLong("DayTime", 0)
                .putInt("GameType", 0)
                .putInt("Generator", Generator.getGeneratorType(generator))
                .putString("generatorName", Generator.getGeneratorName(generator))
                .putString("generatorOptions", options.getOrDefault("preset", ""))
                .putString("LevelName", name)
                .putBoolean("raining", false)
                .putInt("rainTime", 0)
                .putLong("RandomSeed", seed)
                .putInt("SpawnX", 128)
                .putInt("SpawnY", 70)
                .putInt("SpawnZ", 128)
                .putBoolean("thundering", false)
                .putInt("thunderTime", 0)
                .putLong("Time", 0);

        saveLevelData(levelData, worldPath);
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ, boolean create) {
        // BaseFullChunk tmp = lastChunk.get();
        // if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
        // return tmp;
        // }
        long index = Level.chunkHash(chunkX, chunkZ);
        BaseFullChunk chunk = this.chunks.get(index);
        if (chunk == null) {
            chunk = this.readOrCreateChunk(chunkX, chunkZ, create);
        }
        return chunk;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof LevelDBChunk)) throw new IllegalArgumentException("Only LevelDB chunks are supported");
        chunk.setProvider(this);
        chunk.setPosition(chunkX, chunkZ);
        long index = Level.chunkHash(chunkX, chunkZ);

        FullChunk oldChunk = this.chunks.get(index);
        if (oldChunk != null && !oldChunk.equals(chunk)) {
            this.unloadChunk(chunkX, chunkZ, false);
        }
        this.chunks.put(index, (BaseFullChunk) chunk);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ) {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index)) {
            return true;
        }

        return this.readOrCreateChunk(chunkX, chunkZ, create) != null;
    }

    @Override
    public boolean unloadChunk(int chunkX, int chunkZ) {
        return this.unloadChunk(chunkX, chunkZ, true);
    }

    @Override
    public boolean unloadChunk(int chunkX, int chunkZ, boolean safe) {
        long index = Level.chunkHash(chunkX, chunkZ);
        BaseFullChunk chunk = this.chunks.get(index);
        if (chunk == null || !chunk.unload(false, safe)) {
            return false;
        }
        // TODO: this.lastChunk.set(null);
        this.chunks.remove(index, chunk); // TODO: Do this after saveChunkFuture to prevent loading of old copy
        return true;
    }

    @Override
    public BaseFullChunk getLoadedChunk(int chunkX, int chunkZ) {
        // BaseFullChunk tmp = lastChunk.get();
        // if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
        // return tmp;
        // }
        long index = Level.chunkHash(chunkX, chunkZ);
        return this.chunks.get(index);
    }

    @Override
    public BaseFullChunk getLoadedChunk(long hash) {
        // BaseFullChunk tmp = lastChunk.get();
        // if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
        // return tmp;
        // }
        return this.chunks.get(hash);
    }

    @Override
    public boolean isChunkLoaded(int X, int Z) {
        return this.isChunkLoaded(Level.chunkHash(X, Z));
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        return this.chunks.containsKey(hash);
    }

    private synchronized BaseFullChunk readOrCreateChunk(int chunkX, int chunkZ, boolean create) {
        BaseFullChunk chunk = null;
        try {
            chunk = this.readChunk(chunkX, chunkZ);
        } catch (Exception ex) {
            Server.getInstance().getLogger().error("Failed to read chunk " + chunkX + ", " + chunkZ, ex);
        }

        if (chunk == null && create) {
            chunk = this.getEmptyChunk(chunkX, chunkZ);
        } else if (chunk == null) {
            return null;
        }

        this.chunks.put(Level.chunkHash(chunkX, chunkZ), chunk);
        return chunk;
    }

    private BaseFullChunk readChunk(int chunkX, int chunkZ) {
        byte[] versionValue = this.db.get(LevelDBKey.VERSION.getKey(chunkX, chunkZ, this.level.getDimension()));
        if (versionValue == null || versionValue.length != 1) {
            versionValue = this.db.get(LevelDBKey.VERSION_OLD.getKey(chunkX, chunkZ, this.level.getDimension()));
            if (versionValue == null || versionValue.length != 1) {
                return null;
            }
        }

        ChunkBuilder chunkBuilder = new ChunkBuilder(chunkX, chunkZ, this);
        byte[] finalizationState = this.db.get(LevelDBKey.STATE_FINALIZATION.getKey(chunkX, chunkZ, this.level.getDimension()));
        if (finalizationState == null) {
            chunkBuilder.state(ChunkBuilder.STATE_FINISHED);
        } else {
            chunkBuilder.state(Unpooled.wrappedBuffer(finalizationState).readIntLE() + 1);
        }

        byte chunkVersion = versionValue[0];
        if (chunkVersion < 7) {
            chunkBuilder.dirty();
        }

        ChunkSerializers.deserializeChunk(this.db, chunkBuilder, chunkVersion);

        Data3dSerializer.deserialize(this.db, chunkBuilder);
        if (!chunkBuilder.has3dBiomes()) {
            Data2dSerializer.deserialize(this.db, chunkBuilder);
        }

        BlockEntitySerializer.loadBlockEntities(this.db, chunkBuilder);
        EntitySerializer.loadEntities(this.db, chunkBuilder);

        byte[] pendingBlockUpdates = this.db.get(LevelDBKey.PENDING_TICKS.getKey(chunkX, chunkZ, this.level.getDimension()));
        if (pendingBlockUpdates != null && pendingBlockUpdates.length > 0) {
            loadPendingBlockUpdates(pendingBlockUpdates);
        }

        return chunkBuilder.build();
    }

    private void loadPendingBlockUpdates(byte[] data) {
        NbtMap ticks;
        try (ByteBufInputStream stream = new ByteBufInputStream(Unpooled.wrappedBuffer(data))) {
            ticks = (NbtMap) NbtUtils.createReaderLE(stream).readTag();
        } catch (IOException ex) {
            throw new ChunkException("Corrupted block ticking data", ex);
        }

        int currentTick = ticks.getInt("currentTick");

        for (NbtMap nbtMap : ticks.getList("tickList", NbtType.COMPOUND)) {
            Block block = null;

            NbtMap state = nbtMap.getCompound("blockState");
            //noinspection ResultOfMethodCallIgnored
            state.hashCode();

            if (state.containsKey("name")) {
                BlockStateSnapshot blockState = BlockStateMapping.get().getStateUnsafe(state);
                if (blockState == null) {
                    NbtMap updatedState = BlockStateMapping.get().updateVanillaState(state);
                    blockState = BlockStateMapping.get().getUpdatedOrCustom(state, updatedState);
                }
                block = Block.get(blockState.getLegacyId(), blockState.getLegacyData());
            } else if (nbtMap.containsKey("tileID")) {
                block = Block.get(nbtMap.getByte("tileID") & 0xff);
            }

            if (block == null) {
                if (Nukkit.DEBUG > 1) {
                    Server.getInstance().getLogger().debug("Invalid block ticking entry: " + nbtMap);
                }
                continue;
            }

            block.x = nbtMap.getInt("x");
            block.y = nbtMap.getInt("y");
            block.z = nbtMap.getInt("z");
            block.level = level;

            int delay = (int) (nbtMap.getLong("time") - currentTick);
            int priority = nbtMap.getInt("p");

            level.scheduleUpdate(block, block, delay, priority, false);
        }
    }

    @Override
    public void saveChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = this.getChunk(chunkX, chunkZ);
        if (chunk != null) {
            this.saveChunk(chunkX, chunkZ, chunk);
        }
    }

    @Override
    public void saveChunk(int chunkX, int chunkZ, FullChunk chunk0) {
        this.saveChunkFuture(chunkX, chunkZ, chunk0);
    }

    public CompletableFuture<Void> saveChunkFuture(int chunkX, int chunkZ, FullChunk chunk0) {
        if (!(chunk0 instanceof LevelDBChunk)) throw new IllegalArgumentException("Only LevelDB chunks are supported");
        LevelDBChunk chunk = (LevelDBChunk) chunk0;
        chunk.setX(chunkX);
        chunk.setZ(chunkZ);
        if (!chunk.isGenerated()) {
            return CompletableFuture.completedFuture(null);
        }
        chunk.setChanged(false);

        WriteBatch batch = save0(chunkX, chunkZ, chunk);
        return CompletableFuture.runAsync(() -> this.saveChunkCallback(batch, chunk), this.executor);
    }

    public void saveChunkSync(int chunkX, int chunkZ, FullChunk chunk0) {
        if (!(chunk0 instanceof LevelDBChunk)) throw new IllegalArgumentException("Only LevelDB chunks are supported");
        LevelDBChunk chunk = (LevelDBChunk) chunk0;
        chunk.setX(chunkX);
        chunk.setZ(chunkZ);
        if (!chunk.isGenerated()) {
            return;
        }
        chunk.setChanged(false);

        WriteBatch batch = save0(chunkX, chunkZ, chunk);
        this.saveChunkCallback(batch, chunk);
    }

    private WriteBatch save0(int chunkX, int chunkZ, LevelDBChunk chunk) {
        WriteBatch batch = this.db.createWriteBatch();
        ChunkSerializers.serializeChunk(batch, chunk, LATEST_CHUNK_VERSION);
        if (chunk.has3dBiomes()) {
            Data3dSerializer.serialize(batch, chunk);
        } else {
            Data2dSerializer.serialize(batch, chunk);
        }

        batch.put(LevelDBKey.VERSION.getKey(chunkX, chunkZ, this.level.getDimension()), new byte[]{LATEST_CHUNK_VERSION});
        batch.put(LevelDBKey.STATE_FINALIZATION.getKey(chunkX, chunkZ, this.level.getDimension()),
                Unpooled.buffer(4).writeIntLE(chunk.getState() - 1).array());

        BlockEntitySerializer.saveBlockEntities(batch, chunk);
        EntitySerializer.saveEntities(batch, chunk);

        long currentTick = 0;
        Set<BlockUpdateEntry> pendingBlockUpdates = null;

        LevelProvider provider;
        if ((provider = chunk.getProvider()) != null) {
            Level level = provider.getLevel();
            currentTick = level.getCurrentTick();
            pendingBlockUpdates = level.getPendingBlockUpdates(chunk);
        }

        byte[] pendingBlockUpdatesKey = LevelDBKey.PENDING_TICKS.getKey(chunkX, chunkZ, this.level.getDimension());
        if (pendingBlockUpdates != null && !pendingBlockUpdates.isEmpty()) {
            NbtMap ticks = savePendingBlockUpdates(pendingBlockUpdates, currentTick);

            if (ticks != null) {
                ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
                try (org.cloudburstmc.nbt.NBTOutputStream outputStream = NbtUtils.createWriterLE(new ByteBufOutputStream(byteBuf))) {
                    outputStream.writeTag(ticks);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                byteBuf.release();
                batch.put(pendingBlockUpdatesKey, bytes);
            } else {
                batch.delete(pendingBlockUpdatesKey);
            }
        } else {
            batch.delete(pendingBlockUpdatesKey);
        }

        return batch;
    }

    private NbtMap savePendingBlockUpdates(Set<BlockUpdateEntry> entries, long currentTick) {
        ObjectArrayList<NbtMap> list = new ObjectArrayList<>();

        for (BlockUpdateEntry entry : entries) {
            NbtMap blockTag = BlockStateMapping.get().getState(entry.block.getId(), entry.block.getDamage()).getVanillaState();

            NbtMapBuilder tag = NbtMap.builder()
                    .putInt("x", entry.pos.getFloorX())
                    .putInt("y", entry.pos.getFloorY())
                    .putInt("z", entry.pos.getFloorZ())
                    .putCompound("blockState", blockTag)
                    .putLong("time", entry.delay - currentTick);

            if (entry.priority != 0) {
                tag.putInt("p", entry.priority);
            }

            list.add(tag.build());
        }

        return list.isEmpty() ? null : NbtMap.builder()
                .putInt("currentTick", 0)
                .putList("tickList", NbtType.COMPOUND, list).build();
    }

    private void saveChunkCallback(WriteBatch batch, LevelDBChunk chunk) {
        chunk.writeLock().lock();
        try {
            this.db.write(batch);
            batch.close();
        } catch (Exception e) {
            MainLogger.getLogger().error("Exception in saveChunkCallback for " + this.getName(), e);
        } finally {
            chunk.writeLock().unlock();
        }
    }

    @Override
    public void saveChunks() {
        for (BaseFullChunk chunk : this.chunks.values()) {
            if (chunk.hasChanged()) {
                chunk.setChanged(false);
                this.saveChunk(chunk.getX(), chunk.getZ(), chunk);
            }
        }
    }

    @Override
    public void unloadChunks() {
        this.unloadChunksUnsafe(false);
    }

    private void unloadChunksUnsafe(boolean wait) {
        Iterator<BaseFullChunk> iterator = this.chunks.values().iterator();
        while (iterator.hasNext()) {
            LevelDBChunk chunk = (LevelDBChunk) iterator.next();
            chunk.unload(level.isSaveOnUnloadEnabled(), false);
            if (wait) {
                if (!chunk.writeLock().tryLock()) {
                    chunk.writeLock().lock();
                }
                chunk.writeLock().unlock();
            }
            iterator.remove();
        }
    }

    @Override
    public boolean isChunkGenerated(int chunkX, int chunkZ) {
        BaseFullChunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.isGenerated();
    }

    @Override
    public boolean isChunkPopulated(int chunkX, int chunkZ) {
        BaseFullChunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.isPopulated();
    }

    @Override
    public LevelDBChunk getEmptyChunk(int x, int z) {
        LevelDBChunk chunk = new LevelDBChunk(this, new LevelDBChunkSection[0]);
        chunk.setPosition(x, z);
        return chunk;
    }

    @SuppressWarnings("unused")
    public static LevelDBChunkSection createChunkSection(int y) {
        return new LevelDBChunkSection(y);
    }

    @Override
    public Map<Long, ? extends FullChunk> getLoadedChunks() {
        return ImmutableMap.copyOf(this.chunks);
    }

    @Override
    public void requestChunkTask(int chunkX, int chunkZ) {
        LevelDBChunk chunk = (LevelDBChunk) this.getChunk(chunkX, chunkZ, false);
        if (chunk == null) {
            throw new ChunkException("Invalid chunk");
        }

        long timestamp = chunk.getChanges();

        level.asyncChunk(chunk.cloneForChunkSending(), timestamp, chunkX, chunkZ);
    }

    @Override
    public String getPath() {
        return this.path.toString();
    }

    @Override
    public String getGenerator() {
        return this.levelData.getString("generatorName");
    }

    @Override
    public Map<String, Object> getGeneratorOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("preset", this.levelData.getString("generatorOptions"));
        options.put("__LevelDB", true);
        options.put("__Version", this.levelData.getInt("PM1EGen"));
        return options;
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
        if (this.cachedSeed == null) {
            this.cachedSeed = this.levelData.getLong("RandomSeed");
        }
        return this.cachedSeed;
    }

    @Override
    public void setSeed(long value) {
        this.cachedSeed = null;
        this.levelData.putLong("RandomSeed", value);
    }

    @Override
    public Vector3 getSpawn() {
        return this.spawn;
    }

    @Override
    public void setSpawn(Vector3 spawn) {
        this.levelData.putInt("SpawnX", (int) spawn.getX());
        this.levelData.putInt("SpawnY", (int) spawn.getY());
        this.levelData.putInt("SpawnZ", (int) spawn.getZ());
        this.spawn = spawn;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public void updateLevelName(String name) {
        this.levelData.putString("LevelName", name);
    }

    @Override
    public GameRules getGamerules() {
        GameRules rules = GameRules.getDefault();
        rules.readNBT(this.levelData);
        return rules;
    }

    public void setLevelData(CompoundTag levelData, GameRules gameRules) {
        this.levelData = levelData;

        this.setGameRules(gameRules);
    }

    @Override
    public void setGameRules(GameRules rules) {
        //noinspection rawtypes
        for (Map.Entry<GameRule, GameRules.Value> entry : rules.getGameRules().entrySet()) {
            String name = entry.getKey().getName().toLowerCase(Locale.ROOT);

            if (entry.getValue().getType() == GameRules.Type.BOOLEAN) {
                this.levelData.putBoolean(name, rules.getBoolean(entry.getKey()));
            } else if (entry.getValue().getType() == GameRules.Type.INTEGER) {
                this.levelData.putInt(name, rules.getInteger(entry.getKey()));
            } else if (entry.getValue().getType() == GameRules.Type.FLOAT) {
                this.levelData.putFloat(name, rules.getFloat(entry.getKey()));
            }
        }
    }

    private static CompoundTag loadLevelData(Path path) {
        Path levelDat = path.resolve("level.dat");

        try (NBTInputStream stream = new NBTInputStream(new DataInputStream(Files.newInputStream(levelDat)), ByteOrder.LITTLE_ENDIAN)) {
            int version = stream.readInt();
            if (version != 8 && version != 9 && version != 10) {
                throw new LevelException("Incompatible level.dat version: " + version);
            }

            stream.readInt();
            return (CompoundTag) Tag.readNamedTag(stream);
        } catch (Exception ex1) {
            Server.getInstance().getLogger().error("Failed to load level.dat in " + path, ex1);

            Path backup = path.resolve("level.dat_old");
            if (Files.exists(backup)) {
                Server.getInstance().getLogger().warning("Attempting to load level.dat_old in " + path);

                try {
                    // Save a copy of the corrupted one
                    Files.copy(levelDat, path.resolve("level.dat_invalid"), StandardCopyOption.REPLACE_EXISTING);

                    // Replace the corrupted one with a backup
                    Files.copy(backup, levelDat, StandardCopyOption.REPLACE_EXISTING);

                    try (NBTInputStream stream = new NBTInputStream(new DataInputStream(Files.newInputStream(levelDat)), ByteOrder.LITTLE_ENDIAN)) {
                        int version = stream.readInt();
                        if (version != 8 && version != 9 && version != 10) {
                            throw new LevelException("Incompatible level.dat_old version: " + version);
                        }

                        stream.readInt();
                        return (CompoundTag) Tag.readNamedTag(stream);
                    }
                } catch (Exception ex2) {
                    Server.getInstance().getLogger().error("Failed to load level.dat_old in " + path, ex1);
                }
            }
        }

        throw new LevelException("Invalid level.dat");
    }

    private static void saveLevelData(CompoundTag levelData, Path path) {
        // Update storage version values to latest supported
        levelData.putInt("NetworkVersion", PALETTE_VERSION);
        levelData.putInt("StorageVersion", LATEST_SUBCHUNK_VERSION);

        Path savePath = path.resolve("level.dat");
        try {
            if (Files.exists(savePath)) {
                Files.copy(savePath, path.resolve("level.dat_old"), StandardCopyOption.REPLACE_EXISTING);
            }

            byte[] tagBytes;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 NBTOutputStream stream = new NBTOutputStream(outputStream, ByteOrder.LITTLE_ENDIAN)) {
                Tag.writeNamedTag(levelData, stream);
                tagBytes = outputStream.toByteArray();
            }

            try (NBTOutputStream stream = new NBTOutputStream(Files.newOutputStream(savePath), ByteOrder.LITTLE_ENDIAN)) {
                stream.writeInt(8);
                stream.writeInt(tagBytes.length);
                stream.write(tagBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveLevelData() {
        saveLevelData(this.levelData, this.path);
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }

        this.unloadChunksUnsafe(true);
        this.closed = true;
        this.level = null;
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            Server.getInstance().getLogger().error("Stopping LevelDB Executor interrupted", e);
        }

        try {
            this.db.close();
        } catch (IOException e) {
            Server.getInstance().getLogger().error("Can not close LevelDB database", e);
        }
    }

    @Override
    public void doGarbageCollection() {
        // Noop
    }

    @Override
    public void doGarbageCollection(long time) {
        long start = System.currentTimeMillis();
        int maxIterations = this.chunks.size();
        if (this.lastGcPosition > maxIterations) {
            this.lastGcPosition = 0;
        }

        ObjectIterator<BaseFullChunk> iterator = chunks.values().iterator();
        if (this.lastGcPosition != 0) {
            iterator.skip(lastGcPosition);
        }

        int iterations;
        for (iterations = 0; iterations < maxIterations; iterations++) {
            if (!iterator.hasNext()) {
                iterator = this.chunks.values().iterator();
            }

            if (!iterator.hasNext()) {
                break;
            }

            BaseFullChunk chunk = iterator.next();
            if (chunk instanceof LevelDBChunk && chunk.isGenerated() && chunk.isPopulated()) {
                chunk.compress();
                if (System.currentTimeMillis() - start >= time) {
                    break;
                }
            }
        }
        this.lastGcPosition += iterations;
    }
}
