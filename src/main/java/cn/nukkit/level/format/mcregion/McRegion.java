package cn.nukkit.level.format.mcregion;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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

    protected Map<String, RegionLoader> regions = new HashMap<>();

    protected Map<String, Chunk> chunks = new HashMap<>();

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
            for (File file : new File(path + "/region/").listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return Pattern.matches("^.+\\.mc[r|a]$", name);
                }
            })) {
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

    public static int getRegionIndexX(int chunkX) {
        return chunkX >> 5;
    }

    public static int getRegionIndexZ(int chunkZ) {
        return chunkZ >> 5;
    }

    @Override
    public AsyncTask requestChunkTask(int x, int z) throws ChunkException {
        BaseFullChunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Sent");
        }

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

        BinaryStream extraData = new BinaryStream();
        extraData.putLInt(chunk.getBlockExtraDataArray().size());
        for (Integer key : chunk.getBlockExtraDataArray().keySet()) {
            extraData.putLInt(key);
            extraData.putLShort(chunk.getBlockExtraDataArray().get(key));
        }

        BinaryStream stream = new BinaryStream();
        stream.put(chunk.getBlockIdArray());
        stream.put(chunk.getBlockDataArray());
        stream.put(chunk.getBlockSkyLightArray());
        stream.put(chunk.getBlockLightArray());
        for (int height : chunk.getHeightMapArray()) {
            stream.putByte((byte) (height & 0xff));
        }
        for (int color : chunk.getBiomeColorArray()) {
            stream.put(Binary.writeInt(color));
        }
        stream.put(extraData.getBuffer());
        stream.put(tiles);

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
    public Map<String, Chunk> getLoadedChunks() {
        return this.chunks;
    }

    @Override
    public boolean isChunkLoaded(int X, int Z) {
        return this.chunks.containsKey(Level.chunkHash(X, Z));
    }

    @Override
    public void saveChunks() {
        for (Chunk chunk : this.chunks.values()) {
            this.saveChunk(chunk.getX(), chunk.getZ());
        }
    }

    @Override
    public void doGarbageCollection() {
        int limit = (int) (System.currentTimeMillis() - 300);
        for (Map.Entry entry : this.regions.entrySet()) {
            String index = (String) entry.getKey();
            RegionLoader region = (RegionLoader) entry.getValue();
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
    public boolean loadChunk(int chunkX, int chunkZ) {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index)) {
            return true;
        }
        int regionX = getRegionIndexX(chunkX);
        int regionZ = getRegionIndexZ(chunkZ);
        this.loadRegion(regionX, regionZ);
        Chunk chunk;
        try {
            chunk = this.getRegion(regionX, regionZ).readChunk(chunkX - regionX * 32, chunkZ - regionZ * 32);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (chunk == null && create) {
            chunk = this.getEmptyChunk(chunkX, chunkZ);
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
    public boolean unloadChunk(int X, int Z) {
        return this.unloadChunk(X, Z, true);
    }

    @Override
    public boolean unloadChunk(int X, int Z, boolean safe) {
        String index = Level.chunkHash(X, Z);
        Chunk chunk = this.chunks.containsKey(index) ? this.chunks.get(index) : null;
        if (chunk != null && chunk.unload(false, safe)) {
            this.chunks.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public void saveChunk(int X, int Z) {
        if (this.isChunkLoaded(X, Z)) {
            try {
                this.getRegion(X >> 5, Z >> 5).writeChunk(this.getChunk(X, Z));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected RegionLoader getRegion(int x, int z) {
        String index = Level.chunkHash(x, z);
        return this.regions.containsKey(index) ? this.regions.get(index) : null;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, boolean create) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index)) {
            return this.chunks.get(index);
        } else {
            this.loadChunk(chunkX, chunkZ, create);
            return this.chunks.containsKey(index) ? this.chunks.get(index) : null;
        }
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof Chunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        chunk.setProvider(this);
        int regionX = getRegionIndexX(chunkX);
        int regionZ = getRegionIndexZ(chunkZ);
        this.loadRegion(regionX, regionZ);
        chunk.setX(chunkX);
        chunk.setZ(chunkZ);
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index) && !this.chunks.get(index).equals(chunk)) {
            this.unloadChunk(chunkX, chunkZ, false);
        }
        this.chunks.put(index, (Chunk) chunk);
    }

    public static ChunkSection createChunkSection(int Y) {
        return null;
    }

    @Override
    public boolean isChunkGenerated(int chunkX, int chunkZ) {
        RegionLoader region = this.getRegion(chunkX >> 5, chunkZ >> 5);
        return region != null && region.chunkExists(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32) && this.getChunk(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32, true).isGenerated();
    }

    @Override
    public boolean isChunkPopulated(int chunkX, int chunkZ) {
        Chunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.isPopulated();
    }

    protected void loadRegion(int x, int z) {
        String index = Level.chunkHash(x, z);
        if (!this.regions.containsKey(index)) {
            this.regions.put(index, new RegionLoader(this, x, z));
        }
    }

    @Override
    public void close() {
        this.unloadChunks();
        for (String index : new ArrayList<>(this.regions.keySet())) {
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
