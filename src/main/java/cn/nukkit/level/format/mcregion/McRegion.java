package cn.nukkit.level.format.mcregion;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class McRegion extends BaseLevelProvider {

    public McRegion(Level level, String path) throws IOException {
        super(level, path);
    }

    public static String getProviderName() {
        return "mcregion";
    }

    public static byte getProviderOrder() {
        return ORDER_ZXY;
    }

    public static boolean usesChunkSection() {
        return false;
    }

    public static boolean isValid(String path) {
        boolean isValid = (new File(path + "/level.dat").exists()) && new File(path + "/region/").isDirectory();
        if (isValid) {
            for (File file : new File(path + "/region/").listFiles((dir, name) -> Pattern.matches("^.+\\.mc[r|a]$", name))) {
                if (!file.getName().endsWith(".mcr")) {
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
                .putString("generatorOptions", options.containsKey("preset") ? options.get("preset") : "")
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
    public void encodeChunkForSending(int x, int z, BiConsumer<Long, byte[]> callback) throws ChunkException {
        BaseFullChunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Sent");
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
                tiles = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Map<Integer, Integer> extra = chunk.getBlockExtraDataArray();
        BinaryStream extraData;
        if (!extra.isEmpty()) {
            extraData = new BinaryStream();
            extraData.putLInt(extra.size());
            for (Map.Entry<Integer, Integer> entry : extra.entrySet()) {
                extraData.putLInt(entry.getKey());
                extraData.putLShort(entry.getValue());
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
        for (int color : chunk.getBiomeColorArray()) {
            stream.put(Binary.writeInt(color));
        }
        if (extraData != null) {
            stream.put(extraData.getBuffer());
        } else {
            stream.putLInt(0);
        }
        stream.put(tiles);

        callback.accept(timestamp, stream.getBuffer());
    }

    @Override
    public BaseFullChunk loadChunk(long index, int chunkX, int chunkZ, boolean create) {
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

    public Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
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
        this.loadRegion(x >> 5, z >> 5);
        chunk.setPosition(x, z);
        try {
            this.getRegion(x >> 5, z >> 5).writeChunk(chunk);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ChunkSection createChunkSection(int y) {
        return null;
    }

    protected BaseRegionLoader loadRegion(int x, int z) {
        BaseRegionLoader tmp = lastRegion;
        if (tmp != null && x == tmp.getX() && z == tmp.getZ()) {
            return tmp;
        }
        long index = Level.chunkHash(x, z);
        BaseRegionLoader region = this.regions.get(index);
        if (region == null) {
            region = new RegionLoader(this, x, z);
            this.regions.put(index, region);
            return lastRegion = region;
        } else {
            return lastRegion = region;
        }
    }
}
