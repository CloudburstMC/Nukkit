package cn.nukkit.level.format.leveldb;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.leveldb.key.BaseKey;
import cn.nukkit.level.format.leveldb.key.FlagsKey;
import cn.nukkit.level.format.leveldb.key.TerrainKey;
import cn.nukkit.level.format.leveldb.key.VersionKey;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.*;
import java.io.*;
import java.nio.ByteOrder;
import java.util.*;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelDB implements LevelProvider {

    protected final String path;

    protected Map<Long, Chunk> chunks = new HashMap<>();

    protected DB db;

    protected Level level;

    protected CompoundTag levelData;

    public LevelDB(final Level level, final String path) {
        this.level = level;
        this.path = path;
        final File file_path = new File(this.path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }

        try (final FileInputStream stream = new FileInputStream(this.getPath() + "level.dat")) {
            stream.skip(8);
            final CompoundTag levelData = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
            if (levelData != null) {
                this.levelData = levelData;
            } else {
                throw new IOException("LevelData can not be null");
            }
        } catch (final IOException e) {
            throw new LevelException("Invalid level.dat");
        }

        if (!this.levelData.contains("generatorName")) {
            this.levelData.putString("generatorName", Generator.getGenerator("DEFAULT").getSimpleName().toLowerCase());
        }

        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorOptions", "");
        }

        try {
            this.db = Iq80DBFactory.factory.open(new File(this.getPath() + "/db"), new Options().createIfMissing(true));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProviderName() {
        return "leveldb";
    }

    public static byte getProviderOrder() {
        return LevelProvider.ORDER_ZXY;
    }

    public static boolean usesChunkSection() {
        return false;
    }

    public static boolean isValid(final String path) {
        return new File(path + "/level.dat").exists() && new File(path + "/db").isDirectory();
    }

    public static void generate(final String path, final String name, final long seed, final Class<? extends Generator> generator) throws IOException {
        LevelDB.generate(path, name, seed, generator, new HashMap<>());
    }

    public static void generate(final String path, final String name, final long seed, final Class<? extends Generator> generator, final Map<String, String> options) throws IOException {
        if (!new File(path + "/db").exists()) {
            new File(path + "/db").mkdirs();
        }

        final CompoundTag levelData = new CompoundTag("")
            .putLong("currentTick", 0)
            .putInt("DayCycleStopTime", -1)
            .putInt("GameType", 0)
            .putInt("Generator", Generator.getGeneratorType(generator))
            .putBoolean("hasBeenLoadedInCreative", false)
            .putLong("LastPlayed", System.currentTimeMillis() / 1000)
            .putString("LevelName", name)
            .putFloat("lightningLevel", 0)
            .putInt("lightningTime", new Random().nextInt())
            .putInt("limitedWorldOriginX", 128)
            .putInt("limitedWorldOriginY", 70)
            .putInt("limitedWorldOriginZ", 128)
            .putInt("Platform", 0)
            .putFloat("rainLevel", 0)
            .putInt("rainTime", new Random().nextInt())
            .putLong("RandomSeed", seed)
            .putByte("spawnMobs", 0)
            .putInt("SpawnX", 128)
            .putInt("SpawnY", 70)
            .putInt("SpawnZ", 128)
            .putInt("storageVersion", 4)
            .putLong("Time", 0)
            .putLong("worldStartCount", (long) Integer.MAX_VALUE & 0xffffffffL);

        final byte[] data = NBTIO.write(levelData, ByteOrder.LITTLE_ENDIAN);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(Binary.writeLInt(3));
        outputStream.write(Binary.writeLInt(data.length));
        outputStream.write(data);

        Utils.writeFile(path + "level.dat", new ByteArrayInputStream(outputStream.toByteArray()));

        final DB db = Iq80DBFactory.factory.open(new File(path + "/db"), new Options().createIfMissing(true));
        db.close();
    }

    public static ChunkSection createChunkSection(final int y) {
        return null;
    }

    @Override
    public AsyncTask requestChunkTask(final int x, final int z) {
        final Chunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid WoolChunk sent");
        }

        final long timestamp = chunk.getChanges();

        final BinaryStream stream = new BinaryStream();
        stream.putByte((byte) 0); // subchunk version

        stream.put(chunk.getBlockIdArray());
        stream.put(chunk.getBlockDataArray());
        stream.put(chunk.getBlockSkyLightArray());
        stream.put(chunk.getBlockLightArray());
        stream.put(chunk.getHeightMapArray());
        stream.put(chunk.getBiomeIdArray());

        final Map<Integer, Integer> extra = chunk.getBlockExtraDataArray();
        stream.putLInt(extra.size());
        if (!extra.isEmpty()) {
            for (final Integer key : extra.values()) {
                stream.putLInt(key);
                stream.putLShort(extra.get(key));
            }
        }

        if (!chunk.getBlockEntities().isEmpty()) {
            final List<CompoundTag> tagList = new ArrayList<>();

            for (final BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof BlockEntitySpawnable) {
                    tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
                }
            }

            try {
                stream.put(NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.getLevel().chunkRequestCallback(timestamp, x, z, 16, stream.getBuffer());

        return null;
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
                this.put("preset", LevelDB.this.levelData.getString("generatorOptions"));
            }
        };
    }

    @Override
    public BaseFullChunk getLoadedChunk(final int X, final int Z) {
        return this.getLoadedChunk(Level.chunkHash(X, Z));
    }

    @Override
    public BaseFullChunk getLoadedChunk(final long hash) {
        return this.chunks.get(hash);
    }

    @Override
    public Chunk getChunk(final int x, final int z) {
        return this.getChunk(x, z, false);
    }

    @Override
    public Chunk getChunk(final int x, final int z, final boolean create) {
        final long index = Level.chunkHash(x, z);
        if (this.chunks.containsKey(index)) {
            return this.chunks.get(index);
        } else {
            this.loadChunk(x, z, create);
            return this.chunks.getOrDefault(index, null);
        }
    }

    @Override
    public Chunk getEmptyChunk(final int chunkX, final int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public void saveChunks() {
        for (final Chunk chunk : this.chunks.values()) {
            this.saveChunk(chunk.getX(), chunk.getZ());
        }
    }

    @Override
    public void saveChunk(final int x, final int z) {
        if (this.isChunkLoaded(x, z)) {
            this.writeChunk(this.getChunk(x, z));
        }
    }

    @Override
    public void saveChunk(final int x, final int z, final FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid WoolChunk class");
        }
        this.writeChunk((Chunk) chunk);
    }

    @Override
    public void unloadChunks() {
        for (final Chunk chunk : new ArrayList<>(this.chunks.values())) {
            this.unloadChunk(chunk.getX(), chunk.getZ(), false);
        }
        this.chunks = new HashMap<>();
    }

    @Override
    public boolean loadChunk(final int x, final int z) {
        return this.loadChunk(x, z, false);
    }

    @Override
    public boolean loadChunk(final int x, final int z, final boolean create) {
        final long index = Level.chunkHash(x, z);
        if (this.chunks.containsKey(index)) {
            return true;
        }

        this.level.timings.syncChunkLoadDataTimer.startTiming();
        Chunk chunk = this.readChunk(x, z);
        if (chunk == null && create) {
            chunk = Chunk.getEmptyChunk(x, z, this);
        }
        this.level.timings.syncChunkLoadDataTimer.stopTiming();
        if (chunk != null) {
            this.chunks.put(index, chunk);
            return true;
        }

        return false;
    }

    @Override
    public boolean unloadChunk(final int x, final int z) {
        return this.unloadChunk(x, z, true);
    }

    @Override
    public boolean unloadChunk(final int x, final int z, final boolean safe) {
        final long index = Level.chunkHash(x, z);
        final Chunk chunk = this.chunks.getOrDefault(index, null);
        if (chunk != null && chunk.unload(false, safe)) {
            this.chunks.remove(index);
            return true;
        }

        return false;
    }

    @Override
    public boolean isChunkGenerated(final int x, final int z) {
        return this.chunkExists(x, z) && this.getChunk(x, z, false) != null;

    }

    @Override
    public boolean isChunkPopulated(final int x, final int z) {
        return this.getChunk(x, z) != null;
    }

    @Override
    public boolean isChunkLoaded(final int x, final int z) {
        return this.isChunkLoaded(Level.chunkHash(x, z));
    }

    @Override
    public boolean isChunkLoaded(final long hash) {
        return this.chunks.containsKey(hash);
    }

    @Override
    public void setChunk(final int chunkX, final int chunkZ, final FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        chunk.setProvider(this);

        chunk.setPosition(chunkX, chunkZ);
        final long index = Level.chunkHash(chunkX, chunkZ);

        if (this.chunks.containsKey(index) && !this.chunks.get(index).equals(chunk)) {
            this.unloadChunk(chunkX, chunkZ, false);
        }

        this.chunks.put(index, (Chunk) chunk);
    }

    @Override
    public String getName() {
        return this.levelData.getString("LevelName");
    }

    @Override
    public boolean isRaining() {
        return this.levelData.getFloat("rainLevel") > 0;
    }

    @Override
    public void setRaining(final boolean raining) {
        this.levelData.putFloat("rainLevel", raining ? 1.0f : 0);
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
        return this.levelData.getFloat("lightningLevel") > 0;
    }

    @Override
    public void setThundering(final boolean thundering) {
        this.levelData.putFloat("lightningLevel", thundering ? 1.0f : 0);
    }

    @Override
    public int getThunderTime() {
        return this.levelData.getInt("lightningTime");
    }

    @Override
    public void setThunderTime(final int thunderTime) {
        this.levelData.putInt("lightningTime", thunderTime);
    }

    @Override
    public long getCurrentTick() {
        return this.levelData.getLong("currentTick");
    }

    @Override
    public void setCurrentTick(final long currentTick) {
        this.levelData.putLong("currentTick", currentTick);
    }

    @Override
    public long getTime() {
        return this.levelData.getLong("Time");
    }

    @Override
    public void setTime(final long value) {
        this.levelData.putLong("Time", value);
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
        return new Vector3(this.levelData.getInt("SpawnX"), this.levelData.getInt("SpawnY"), this.levelData.getInt("SpawnZ"));
    }

    @Override
    public void setSpawn(final Vector3 pos) {
        this.levelData.putInt("SpawnX", (int) pos.x);
        this.levelData.putInt("SpawnY", (int) pos.y);
        this.levelData.putInt("SpawnZ", (int) pos.z);
    }

    @Override
    public Map<Long, Chunk> getLoadedChunks() {
        return this.chunks;
    }

    @Override
    public void doGarbageCollection() {

    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public void close() {
        this.unloadChunks();
        try {
            this.db.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        this.level = null;
    }

    @Override
    public void saveLevelData() {
        try {
            final byte[] data = NBTIO.write(this.levelData, ByteOrder.LITTLE_ENDIAN);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Binary.writeLInt(3));
            outputStream.write(Binary.writeLInt(data.length));
            outputStream.write(data);

            Utils.writeFile(this.path + "level.dat", new ByteArrayInputStream(outputStream.toByteArray()));
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

    public Chunk readChunk(final int chunkX, final int chunkZ) {
        final byte[] data;
        if (!this.chunkExists(chunkX, chunkZ) || (data = this.db.get(TerrainKey.create(chunkX, chunkZ).toArray())) == null) {
            return null;
        }

        byte[] flags = this.db.get(FlagsKey.create(chunkX, chunkZ).toArray());
        if (flags == null) {
            flags = new byte[]{0x03};
        }

        return Chunk.fromBinary(
            Binary.appendBytes(
                Binary.writeLInt(chunkX),
                Binary.writeLInt(chunkZ),
                data,
                flags)
            , this);
    }

    public DB getDatabase() {
        return this.db;
    }

    public Server getServer() {
        return this.level.getServer();
    }

    public CompoundTag getLevelData() {
        return this.levelData;
    }

    public byte[][] getTerrainKeys() {
        final List<byte[]> result = new ArrayList<>();
        this.db.forEach(entry -> {
            final byte[] key = entry.getKey();
            if (key.length > 8 && key[8] == BaseKey.DATA_TERRAIN) {
                result.add(key);
            }
        });
        return result.toArray(new byte[0][]);
    }

    private void writeChunk(final Chunk chunk) {
        final byte[] binary = chunk.toBinary(true);
        this.db.put(TerrainKey.create(chunk.getX(), chunk.getZ()).toArray(), Binary.subBytes(binary, 8, binary.length - 1));
        this.db.put(FlagsKey.create(chunk.getX(), chunk.getZ()).toArray(), Binary.subBytes(binary, binary.length - 1));
        this.db.put(VersionKey.create(chunk.getX(), chunk.getZ()).toArray(), new byte[]{0x02});
    }

    private boolean chunkExists(final int chunkX, final int chunkZ) {
        return this.db.get(VersionKey.create(chunkX, chunkZ).toArray()) != null;
    }

}
