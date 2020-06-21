package cn.nukkit.level.format.mcregion;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
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
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class McRegion extends BaseLevelProvider {

    public McRegion(final Level level, final String path, final String name) throws IOException {
        super(level, path, name);
    }

    public static String getProviderName() {
        return "mcregion";
    }

    public static byte getProviderOrder() {
        return LevelProvider.ORDER_ZXY;
    }

    public static boolean usesChunkSection() {
        return false;
    }

    public static boolean isValid(final String path) {
        boolean isValid = new File(path + "/level.dat").exists() && new File(path + "/region/").isDirectory();
        if (isValid) {
            for (final File file : new File(path + "/region/").listFiles((dir, name) -> Pattern.matches("^.+\\.mc[r|a]$", name))) {
                if (!file.getName().endsWith(".mcr")) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    public static void generate(final String path, final String name, final long seed, final Class<? extends Generator> generator) throws IOException {
        McRegion.generate(path, name, seed, generator, new HashMap<>());
    }

    public static void generate(final String path, final String name, final long seed, final Class<? extends Generator> generator, final Map<String, String> options) throws IOException {
        if (!new File(path + "/region").exists()) {
            new File(path + "/region").mkdirs();
        }

        final CompoundTag levelData = new CompoundTag("Data")
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

    public static ChunkSection createChunkSection(final int y) {
        return null;
    }

    @Override
    public AsyncTask requestChunkTask(final int x, final int z) throws ChunkException {
        final BaseFullChunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid WoolChunk Sent");
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

        return null;
    }

    @Override
    public Chunk getEmptyChunk(final int chunkX, final int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public void saveChunk(final int x, final int z) {
        if (this.isChunkLoaded(x, z)) {
            try {
                this.getRegion(x >> 5, z >> 5).writeChunk(this.getChunk(x, z));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void saveChunk(final int x, final int z, final FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid WoolChunk class");
        }
        this.loadRegion(x >> 5, z >> 5);
        chunk.setPosition(x, z);
        try {
            this.getRegion(x >> 5, z >> 5).writeChunk(chunk);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseFullChunk loadChunk(final long index, final int chunkX, final int chunkZ, final boolean create) {
        final int regionX = BaseLevelProvider.getRegionIndexX(chunkX);
        final int regionZ = BaseLevelProvider.getRegionIndexZ(chunkZ);
        final BaseRegionLoader region = this.loadRegion(regionX, regionZ);
        this.level.timings.syncChunkLoadDataTimer.startTiming();
        BaseFullChunk chunk;
        try {
            chunk = region.readChunk(chunkX - regionX * 32, chunkZ - regionZ * 32);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        if (chunk == null) {
            if (create) {
                chunk = this.getEmptyChunk(chunkX, chunkZ);
                this.putChunk(index, chunk);
            }
        } else {
            this.putChunk(index, chunk);
        }
        this.level.timings.syncChunkLoadDataTimer.stopTiming();
        return chunk;
    }

    protected BaseRegionLoader loadRegion(final int x, final int z) {
        final BaseRegionLoader tmp = this.lastRegion.get();
        if (tmp != null && x == tmp.getX() && z == tmp.getZ()) {
            return tmp;
        }
        final long index = Level.chunkHash(x, z);
        synchronized (this.regions) {
            BaseRegionLoader region = this.regions.get(index);
            if (region == null) {
                region = new RegionLoader(this, x, z);
                this.regions.put(index, region);
            }
            this.lastRegion.set(region);
            return region;
        }
    }

}
