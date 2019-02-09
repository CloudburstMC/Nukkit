package cn.nukkit.level.format.anvil;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.ThreadCache;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Anvil extends BaseLevelProvider {
    public static final int VERSION = 19133;
    static private final byte[] PAD_256 = new byte[256];

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
                .putInt("version", VERSION)
                .putLong("Time", 0)
                .putLong("SizeOnDisk", 0);

        NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), new FileOutputStream(path + "level.dat"), ByteOrder.BIG_ENDIAN);
    }

    @Override
    public Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public AsyncTask requestChunkTask(int x, int z) throws ChunkException {
        Chunk chunk = (Chunk) this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Set");
        }

        long timestamp = chunk.getChanges();

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

        BinaryStream stream = ThreadCache.binaryStream.get().reset();
        int count = 0;
        cn.nukkit.level.format.ChunkSection[] sections = chunk.getSections();
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
        for (byte height : chunk.getHeightMapArray()) {
            stream.putByte(height);
        }
        stream.put(PAD_256);
        stream.put(chunk.getBiomeIdArray());
        stream.putByte((byte) 0);
        if (extraData != null) {
            stream.put(extraData.getBuffer());
        } else {
            stream.putVarInt(0);
        }
        stream.put(blockEntities);

        this.getLevel().chunkRequestCallback(timestamp, x, z, stream.getBuffer());

        return null;
    }

    private int lastPosition = 0;

    @Override
    public void doGarbageCollection(long time) {
        long start = System.currentTimeMillis();
        int maxIterations = size();
        if (lastPosition > maxIterations) lastPosition = 0;
        int i;
        synchronized (chunks) {
            ObjectIterator<BaseFullChunk> iter = chunks.values().iterator();
            if (lastPosition != 0) iter.skip(lastPosition);
            for (i = 0; i < maxIterations; i++) {
                if (!iter.hasNext()) {
                    iter = chunks.values().iterator();
                }
                if (!iter.hasNext()) break;
                BaseFullChunk chunk = iter.next();
                if (chunk == null) continue;
                if (chunk.isGenerated() && chunk.isPopulated() && chunk instanceof Chunk) {
                    Chunk anvilChunk = (Chunk) chunk;
                    chunk.compress();
                    if (System.currentTimeMillis() - start >= time) break;
                }
            }
        }
        lastPosition += i;
    }

    @Override
    public synchronized BaseFullChunk loadChunk(long index, int chunkX, int chunkZ, boolean create) {
        int regionX = getRegionIndexX(chunkX);
        int regionZ = getRegionIndexZ(chunkZ);
        BaseRegionLoader region = this.loadRegion(regionX, regionZ);
        this.level.timings.syncChunkLoadDataTimer.startTiming();
        BaseFullChunk chunk;
        try {
            chunk = region.readChunk(chunkX - regionX * 32, chunkZ - regionZ * 32);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (chunk == null) {
            if (create) {
                chunk = this.getEmptyChunk(chunkX, chunkZ);
                putChunk(index, chunk);
            }
        } else {
            putChunk(index, chunk);
        }
        this.level.timings.syncChunkLoadDataTimer.stopTiming();
        return chunk;
    }

    @Override
    public synchronized void saveChunk(int X, int Z) {
        BaseFullChunk chunk = this.getChunk(X, Z);
        if (chunk != null) {
            try {
                this.loadRegion(X >> 5, Z >> 5).writeChunk(chunk);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public synchronized void saveChunk(int x, int z, FullChunk chunk) {
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

    public static ChunkSection createChunkSection(int y) {
        ChunkSection cs = new ChunkSection(y);
        cs.hasSkyLight = true;
        return cs;
    }

    protected synchronized BaseRegionLoader loadRegion(int x, int z) {
        BaseRegionLoader tmp = lastRegion.get();
        if (tmp != null && x == tmp.getX() && z == tmp.getZ()) {
            return tmp;
        }
        long index = Level.chunkHash(x, z);
        synchronized (regions) {
            BaseRegionLoader region = this.regions.get(index);
            if (region == null) {
                try {
                    region = new RegionLoader(this, x, z);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.regions.put(index, region);
            }
            lastRegion.set(region);
            return region;
        }
    }
}
