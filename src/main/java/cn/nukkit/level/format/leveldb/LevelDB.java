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
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelDB implements LevelProvider {

    protected Long2ObjectOpenHashMap<Chunk> chunks = new Long2ObjectOpenHashMap<>();

    protected DB db;

    protected Level level;

    protected final String path;

    protected CompoundTag levelData;

    public LevelDB(Level level, String path) {
        this.level = level;
        this.path = path;
        File file_path = new File(this.path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }

        try (FileInputStream stream = new FileInputStream(this.getPath() + "level.dat")) {
            stream.skip(8);
            CompoundTag levelData = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
            if (levelData != null) {
                this.levelData = levelData;
            } else {
                throw new IOException("LevelData can not be null");
            }
        } catch (IOException e) {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProviderName() {
        return "leveldb";
    }

    public static byte getProviderOrder() {
        return ORDER_ZXY;
    }

    public static boolean usesChunkSection() {
        return false;
    }

    public static boolean isValid(String path) {
        return new File(path + "/level.dat").exists() && new File(path + "/db").isDirectory();
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator) throws IOException {
        generate(path, name, seed, generator, new HashMap<>());
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator, Map<String, String> options) throws IOException {
        if (!new File(path + "/db").exists()) {
            new File(path + "/db").mkdirs();
        }

        CompoundTag levelData = new CompoundTag("")
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
                .putLong("worldStartCount", ((long) Integer.MAX_VALUE) & 0xffffffffL);

        byte[] data = NBTIO.write(levelData, ByteOrder.LITTLE_ENDIAN);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(Binary.writeLInt(3));
        outputStream.write(Binary.writeLInt(data.length));
        outputStream.write(data);

        Utils.writeFile(path + "level.dat", new ByteArrayInputStream(outputStream.toByteArray()));

        DB db = Iq80DBFactory.factory.open(new File(path + "/db"), new Options().createIfMissing(true));
        db.close();
    }

    @Override
    public void saveLevelData() {
        try {
            byte[] data = NBTIO.write(levelData, ByteOrder.LITTLE_ENDIAN);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(Binary.writeLInt(3));
            outputStream.write(Binary.writeLInt(data.length));
            outputStream.write(data);

            Utils.writeFile(path + "level.dat", new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AsyncTask requestChunkTask(int x, int z) {
        Chunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk sent");
        }

        long timestamp = chunk.getChanges();

        byte[] tiles = new byte[0];

        if (!chunk.getBlockEntities().isEmpty()) {
            List<CompoundTag> tagList = new ArrayList<>();

            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof BlockEntitySpawnable) {
                    tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
                }
            }

            try {
                tiles = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Map<Integer, Integer> extra = chunk.getBlockExtraDataArray();
        BinaryStream extraData;
        if (!extra.isEmpty()) {
            extraData = new BinaryStream();
            extraData.putLInt(extra.size());
            for (Integer key : extra.values()) {
                extraData.putLInt(key);
                extraData.putLShort(extra.get(key));
            }
        } else {
            extraData = null;
        }

        BinaryStream stream = new BinaryStream();
        stream.put(chunk.getBlockIdArray());
        stream.put(chunk.getBlockDataArray());
        stream.put(chunk.getBlockSkyLightArray());
        stream.put(chunk.getBlockLightArray());
        stream.put(chunk.getHeightMapArray());
        stream.put(chunk.getBiomeIdArray());
        if (extraData != null) {
            stream.put(extraData.getBuffer());
        } else {
            stream.putLInt(0);
        }
        stream.put(tiles);

        this.getLevel().chunkRequestCallback(timestamp, x, z, stream.getBuffer());

        return null;
    }

    @Override
    public void unloadChunks() {
        for (Chunk chunk : new ArrayList<>(this.chunks.values())) {
            this.unloadChunk(chunk.getX(), chunk.getZ(), false);
        }
        this.chunks = new Long2ObjectOpenHashMap<>();
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
    public BaseFullChunk getLoadedChunk(int X, int Z) {
        return this.getLoadedChunk(Level.chunkHash(X, Z));
    }

    @Override
    public BaseFullChunk getLoadedChunk(long hash) {
        return this.chunks.get(hash);
    }

    @Override
    public Long2ObjectOpenHashMap<Chunk> getLoadedChunks() {
        return this.chunks;
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return this.isChunkLoaded(Level.chunkHash(x, z));
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        return this.chunks.containsKey(hash);
    }

    @Override
    public void saveChunks() {
        for (Chunk chunk : this.chunks.values()) {
            this.saveChunk(chunk.getX(), chunk.getZ());
        }
    }

    @Override
    public boolean loadChunk(int x, int z) {
        return this.loadChunk(x, z, false);
    }

    @Override
    public boolean loadChunk(int x, int z, boolean create) {
        long index = Level.chunkHash(x, z);
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

    public Chunk readChunk(int chunkX, int chunkZ) {
        byte[] data;
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

    private void writeChunk(Chunk chunk) {
        byte[] binary = chunk.toBinary(true);
        this.db.put(TerrainKey.create(chunk.getX(), chunk.getZ()).toArray(), Binary.subBytes(binary, 8, binary.length - 1));
        this.db.put(FlagsKey.create(chunk.getX(), chunk.getZ()).toArray(), Binary.subBytes(binary, binary.length - 1));
        this.db.put(VersionKey.create(chunk.getX(), chunk.getZ()).toArray(), new byte[]{0x02});
    }

    @Override
    public boolean unloadChunk(int x, int z) {
        return this.unloadChunk(x, z, true);
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean safe) {
        long index = Level.chunkHash(x, z);
        Chunk chunk = this.chunks.containsKey(index) ? this.chunks.get(index) : null;
        if (chunk != null && chunk.unload(false, safe)) {
            this.chunks.remove(index);
            return true;
        }

        return false;
    }

    @Override
    public void saveChunk(int x, int z) {
        if (this.isChunkLoaded(x, z)) {
            this.writeChunk(this.getChunk(x, z));
        }
    }

    @Override
    public void saveChunk(int x, int z, FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        this.writeChunk((Chunk) chunk);
    }

    @Override
    public Chunk getChunk(int x, int z) {
        return this.getChunk(x, z, false);
    }

    @Override
    public Chunk getChunk(int x, int z, boolean create) {
        long index = Level.chunkHash(x, z);
        if (this.chunks.containsKey(index)) {
            return this.chunks.get(index);
        } else {
            this.loadChunk(x, z, create);
            return this.chunks.containsKey(index) ? this.chunks.get(index) : null;
        }
    }

    public DB getDatabase() {
        return db;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        chunk.setProvider(this);

        chunk.setPosition(chunkX, chunkZ);
        long index = Level.chunkHash(chunkX, chunkZ);

        if (this.chunks.containsKey(index) && !this.chunks.get(index).equals(chunk)) {
            this.unloadChunk(chunkX, chunkZ, false);
        }

        this.chunks.put(index, (Chunk) chunk);
    }

    public static ChunkSection createChunkSection(int y) {
        return null;
    }

    private boolean chunkExists(int chunkX, int chunkZ) {
        return this.db.get(VersionKey.create(chunkX, chunkZ).toArray()) != null;
    }

    @Override
    public boolean isChunkGenerated(int x, int z) {
        return this.chunkExists(x, z) && this.getChunk(x, z, false) != null;

    }

    @Override
    public boolean isChunkPopulated(int x, int z) {
        return this.getChunk(x, z) != null;
    }

    @Override
    public void close() {
        this.unloadChunks();
        try {
            this.db.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.level = null;
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
        return this.levelData.getFloat("rainLevel") > 0;
    }

    @Override
    public void setRaining(boolean raining) {
        this.levelData.putFloat("rainLevel", raining ? 1.0f : 0);
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
        return this.levelData.getFloat("lightningLevel") > 0;
    }

    @Override
    public void setThundering(boolean thundering) {
        this.levelData.putFloat("lightningLevel", thundering ? 1.0f : 0);
    }

    @Override
    public int getThunderTime() {
        return this.levelData.getInt("lightningTime");
    }

    @Override
    public void setThunderTime(int thunderTime) {
        this.levelData.putInt("lightningTime", thunderTime);
    }

    @Override
    public long getCurrentTick() {
        return this.levelData.getLong("currentTick");
    }

    @Override
    public void setCurrentTick(long currentTick) {
        this.levelData.putLong("currentTick", currentTick);
    }

    @Override
    public long getTime() {
        return this.levelData.getLong("Time");
    }

    @Override
    public void setTime(long value) {
        this.levelData.putLong("Time", value);
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
        return new Vector3(this.levelData.getInt("SpawnX"), this.levelData.getInt("SpawnY"), this.levelData.getInt("SpawnZ"));
    }

    @Override
    public void setSpawn(Vector3 pos) {
        this.levelData.putInt("SpawnX", (int) pos.x);
        this.levelData.putInt("SpawnY", (int) pos.y);
        this.levelData.putInt("SpawnZ", (int) pos.z);
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

    }

    public CompoundTag getLevelData() {
        return levelData;
    }

    public void updateLevelName(String name) {
        if (!this.getName().equals(name)) {
            this.levelData.putString("LevelName", name);
        }
    }

    public byte[][] getTerrainKeys() {
        List<byte[]> result = new ArrayList<>();
        this.db.forEach((entry) -> {
            byte[] key = entry.getKey();
            if (key.length > 8 && key[8] == BaseKey.DATA_TERRAIN) {
                result.add(key);
            }
        });
        return result.stream().toArray(byte[][]::new);
    }
}
