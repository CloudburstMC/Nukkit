package cn.nukkit.server.level.format.anvil;

import cn.nukkit.server.blockentity.BlockEntity;
import cn.nukkit.server.blockentity.BlockEntitySpawnable;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.level.format.generic.BaseLevelProvider;
import cn.nukkit.server.level.generator.Generator;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.scheduler.AsyncTask;
import cn.nukkit.server.utils.BinaryStream;
import cn.nukkit.server.utils.ChunkException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Anvil extends BaseLevelProvider {

    protected final Map<Long, RegionLoader> regions = new HashMap<>();

    protected Map<Long, Chunk> chunks = new HashMap<>();

    public Anvil(Level level, String path) throws IOException {
        super(level, path);
    }

    public static String getProviderName() {
        return "anvil";
    }

    public static byte getProviderOrder() {
        return ORDER_YZX;
    }

    public static boolean usesChunkSection() {
        return true;
    }

    public static boolean isValid(String path) {
        boolean isValid = (new File(path + "/level.dat").exists()) && new File(path + "/region/").isDirectory();
        if (isValid) {
            for (File file : new File(path + "/region/").listFiles((dir, name) -> Pattern.matches("^.+\\.mc[r|a]$", name))) {
                if (!file.getName().endsWith(".mca")) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator) throws IOException {
        generate(path, name, seed, generator, new HashMap<>());
    }

    public static void generate(String path, String name, long seed, Class<? extends Generator> generator, Map<String, String> options) throws IOException {
        if (!new File(path + "/region").exists()) {
            new File(path + "/region").mkdirs();
        }

        CompoundTag levelData = new CompoundTag("Data")
                .putCompound("GameRules", new CompoundTag())

                .putLong("DayTime", 0)
                .putInt("GameType", 0)
                .putString("generatorName", Generator.getGeneratorName(generator))
                .putString("generatorOptions", options.getOrDefault("preset", ""))
                .putInt("generatorVersion", 1)
                .putBoolean("hardcore", false)
                .putBoolean("initialized", true)
                .putLong("LastPlayed", System.currentTimeMillis() / 1000)
                .putString("LevelName", name)
                .putBoolean("raining", false)
                .putInt("rainTime", 0)
                .putLong("RandomSeed", seed)
                .putInt("SpawnX", 128)
                .putInt("SpawnY", 70)
                .putInt("SpawnZ", 128)
                .putBoolean("thundering", false)
                .putInt("thunderTime", 0)
                .putInt("version", 19133)
                .putLong("Time", 0)
                .putLong("SizeOnDisk", 0);

        NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), new FileOutputStream(path + "level.dat"), ByteOrder.BIG_ENDIAN);
    }

    @Override
    public AsyncTask requestChunkTask(int x, int z) throws ChunkException {
        Chunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Set");
        }

        byte[] blockEntities = new byte[0];

        if (!chunk.getBlockEntities().isEmpty()) {
            List<CompoundTag> tagList = new ArrayList<>();

            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof BlockEntitySpawnable) {
                    tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
                }
            }

            try {
                blockEntities = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Map<Integer, Integer> extra = chunk.getBlockExtraDataArray();
        BinaryStream extraData;
        if (!extra.isEmpty()) {
            extraData = new BinaryStream();
            extraData.putVarInt(extra.size());
            for (Map.Entry<Integer, Integer> entry : extra.entrySet()) {
                extraData.putVarInt(entry.getKey());
                extraData.putLShort(entry.getValue());
            }
        } else {
            extraData = null;
        }

        BinaryStream stream = new BinaryStream();
        int count = 0;
        cn.nukkit.server.level.format.ChunkSection[] sections = chunk.getSections();
        for (int i = sections.length - 1; i >= 0; i--) {
            if (!sections[i].isEmpty()) {
                count = i + 1;
                break;
            }
        }
        stream.putByte((byte) count);
        for (int i = 0; i < count; i++) {
            stream.putByte((byte) 0);
            stream.put(sections[i].getBytes());
        }
        for (int height : chunk.getHeightMapArray()) {
            stream.putByte((byte) height);
        }
        stream.put(new byte[256]);
        stream.put(chunk.getBiomeIdArray());
        stream.putByte((byte) 0);
        if (extraData != null) {
            stream.put(extraData.getBuffer());
        } else {
            stream.putVarInt(0);
        }
        stream.put(blockEntities);

        this.getLevel().chunkRequestCallback(x, z, stream.getBuffer());

        return null;
    }

    @Override
    public void unloadChunks() {
        for (Chunk chunk : new ArrayList<>(this.chunks.values())) {
            this.unloadChunk(chunk.getX(), chunk.getZ(), false);
        }
        this.chunks = new HashMap<>();
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
    public Map<Long, Chunk> getLoadedChunks() {
        return this.chunks;
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return this.chunks.containsKey(Level.chunkHash(x, z));
    }

    @Override
    public void saveChunks() {
        for (Chunk chunk : this.chunks.values()) {
            this.saveChunk(chunk.getX(), chunk.getZ());
        }
    }

    @Override
    public void doGarbageCollection() {
        int limit = (int) (System.currentTimeMillis() - 50);
        for (Map.Entry<Long, RegionLoader> entry : this.regions.entrySet()) {
            long index = entry.getKey();
            RegionLoader region = entry.getValue();
            if (region.lastUsed <= limit) {
                try {
                    region.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.regions.remove(index);
            }
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
        int regionX = x >> 5;
        int regionZ = z >> 5;
        this.loadRegion(regionX, regionZ);
        Chunk chunk;
        try {
            chunk = this.getRegion(regionX, regionZ).readChunk(x & 0x1f, z & 0x1f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (chunk == null && create) {
            chunk = this.getEmptyChunk(x, z);
        }
        if (chunk != null) {
            this.chunks.put(index, chunk);
            return true;
        }
        return false;
    }

    public Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public boolean unloadChunk(int x, int z) {
        return this.unloadChunk(x, z, true);
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean safe) {
        long index = Level.chunkHash(x, z);
        Chunk chunk = this.chunks.getOrDefault(index, null);
        if (chunk != null && chunk.unload(false, safe)) {
            this.chunks.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public void saveChunk(int x, int z) {
        if (this.isChunkLoaded(x, z)) {
            try {
                this.getRegion(x >> 5, z >> 5).writeChunk(this.getChunk(x, z));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void saveChunk(int x, int z, FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        int regionX = x >> 5;
        int regionZ = z >> 5;
        this.loadRegion(regionX, regionZ);
        chunk.setX(x);
        chunk.setZ(z);
        try {
            this.getRegion(regionX, regionZ).writeChunk(chunk);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected RegionLoader getRegion(int x, int z) {
        long index = Level.chunkHash(x, z);
        return this.regions.getOrDefault(index, null);
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
            return this.chunks.getOrDefault(index, null);
        }
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        chunk.setProvider(this);
        int regionX = chunkX >> 5;
        int regionZ = chunkZ >> 5;
        this.loadRegion(regionX, regionZ);

        chunk.setX(chunkX);
        chunk.setZ(chunkZ);
        long index = Level.chunkHash(chunkX, chunkZ);
        this.chunks.put(index, (Chunk) chunk);
    }

    public static ChunkSection createChunkSection(int y) {
        CompoundTag nbt = new CompoundTag();
        nbt.putByte("Y", y);
        nbt.putByteArray("BlockTypes", new byte[4096]);
        nbt.putByteArray("Data", new byte[2048]);
        byte[] sl = new byte[2048];
        Arrays.fill(sl, (byte) 0xff);
        nbt.putByteArray("SkyLight", sl);
        nbt.putByteArray("BlockLight", new byte[2048]);
        return new ChunkSection(nbt);
    }

    @Override
    public boolean isChunkGenerated(int x, int z) {
        RegionLoader region = this.getRegion(x >> 5, z >> 5);
        return region != null && region.chunkExists(x & 0x1f, z & 0x1f) && this.getChunk(x & 0x1f, z & 0x1f, true).isGenerated();
    }

    @Override
    public boolean isChunkPopulated(int x, int z) {
        Chunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isPopulated();
    }

    protected void loadRegion(int x, int z) {
        long index = Level.chunkHash(x, z);
        if (!this.regions.containsKey(index)) {
            try {
                this.regions.put(index, new RegionLoader(this, x, z));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() {
        this.unloadChunks();
        for (long index : new ArrayList<>(this.regions.keySet())) {
            RegionLoader region = this.regions.get(index);
            try {
                region.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.regions.remove(index);
        }
        this.level = null;
    }
}
