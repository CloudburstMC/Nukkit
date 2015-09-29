package cn.nukkit.level.format.mcregion;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.nbt.NbtIo;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.tile.Spawnable;
import cn.nukkit.tile.Tile;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ChunkException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
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
                if (!file.getName().endsWith(".mca")) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    public static void generate(String path, String name, int seed, Class generator) throws IOException {
        generate(path, name, seed, generator, new HashMap<>());
    }

    public static void generate(String path, String name, int seed, Class generator, Map<String, String> options) throws IOException {
        if (!new File(path + "/region").exists()) {
            new File(path + "/region").mkdirs();
        }

        CompoundTag levelData = new CompoundTag("Data");
        levelData.putBoolean("hardcore", false);
        levelData.putBoolean("initialized", true);
        levelData.putInt("GameType", 0);
        levelData.putInt("generatorVersion", 1);
        levelData.putInt("SpawnX", 128);
        levelData.putInt("SpawnY", 70);
        levelData.putInt("SpawnZ", 128);
        levelData.putInt("version", 19133);
        levelData.putLong("Time", 0);
        levelData.putLong("LastPlayed", System.currentTimeMillis());
        levelData.putLong("RandomSeed", seed);
        levelData.putLong("SizeOnDisk", 0);
        levelData.putLong("Time", 0);
        levelData.putString("generatorName", Generator.getGeneratorName(generator));
        levelData.putString("generatorOptions", options.containsKey("preset") ? options.get("preset") : "");
        levelData.putString("LevelName", name);
        levelData.putCompound("GameRules", new CompoundTag());
        NbtIo.writeCompressed(levelData, new FileOutputStream(path + "level.dat"));
    }

    public static int getRegionIndexX(int chunkX) {
        return chunkX >> 5;
    }

    public static int getRegionIndexZ(int chunkZ) {
        return chunkZ >> 5;
    }

    @Override
    public AsyncTask requestChunkTask(int x, int z) throws ChunkException, IOException {
        Chunk chunk = this.getChunk(x, z, false);
        if (chunk == null) {
            throw new ChunkException("Invalid Chunk Sent");
        }
        byte[][] tiles = new byte[0][];
        int tilesLength = 0;
        int i = 0;
        if (!chunk.getTiles().isEmpty()) {
            int length = chunk.getTiles().size();
            tiles = new byte[length][];
            for (Tile tile : chunk.getTiles().values()) {
                if (tile instanceof Spawnable) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream outputStream = new DataOutputStream(baos);
                    NbtIo.write(((Spawnable) tile).getSpawnCompound(), outputStream);
                    tiles[i] = baos.toByteArray();
                    tilesLength += tiles[i].length;
                    i++;
                }
            }
        }
        byte[] blockIdArray = chunk.getBlockIdArray();
        byte[] blockDataArray = chunk.getBlockDataArray();
        byte[] blockSkyLightArray = chunk.getBlockSkyLightArray();
        byte[] blockLightArray = chunk.getBlockLightArray();
        int[] heightMapArray = chunk.getHeightMapArray();
        int[] biomeColorArray = chunk.getBiomeColorArray();
        ByteBuffer buffer = ByteBuffer.allocate(blockIdArray.length + blockDataArray.length + blockSkyLightArray.length + blockLightArray.length + heightMapArray.length + biomeColorArray.length * 4 + tilesLength);
        buffer.put(blockIdArray);
        buffer.put(blockDataArray);
        buffer.put(blockSkyLightArray);
        buffer.put(blockLightArray);
        for (int aHeightMapArray : heightMapArray) {
            buffer.put((byte) (aHeightMapArray & 0xff));
        }
        for (int aBiomeColorArray : biomeColorArray) {
            buffer.put(Binary.writeInt(aBiomeColorArray));
        }
        for (int j = 0; j < i; j++) {
            buffer.put(tiles[j]);
        }

        this.getLevel().chunkRequestCallback(x, z, buffer.array());
        return null;
    }

    @Override
    public void unloadChunks() throws Exception {
        for (Chunk chunk : this.chunks.values()) {
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
    public void saveChunks() throws Exception {
        for (Chunk chunk : this.chunks.values()) {
            this.saveChunk(chunk.getX(), chunk.getZ());
        }
    }

    @Override
    public void doGarbageCollection() throws IOException {
        int limit = (int) (System.currentTimeMillis() - 300);
        for (Map.Entry entry : this.regions.entrySet()) {
            String index = (String) entry.getKey();
            RegionLoader region = (RegionLoader) entry.getValue();
            if (region.lastUsed <= limit) {
                region.close();
                this.regions.remove(index);
            }
        }
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ) throws IOException {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) throws IOException {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index)) {
            return true;
        }
        int regionX = getRegionIndexX(chunkX);
        int regionZ = getRegionIndexZ(chunkZ);
        this.loadRegion(regionX, regionZ);
        Chunk chunk = this.getRegion(regionX, regionZ).readChunk(chunkX - regionX * 32, chunkZ - regionZ * 32);
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
        return Chunk.getEmptyChunk(chunkX, chunkZ);
    }

    @Override
    public boolean unloadChunk(int X, int Z) throws Exception {
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
    public void saveChunk(int X, int Z) throws Exception {
        if (this.isChunkLoaded(X, Z)) {
            this.getRegion(X >> 5, Z >> 5).writeChunk(this.getChunk(X, Z));
        }
    }

    protected RegionLoader getRegion(int x, int z) {
        String index = Level.chunkHash(x, z);
        return this.regions.containsKey(index) ? this.regions.get(index) : null;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) throws IOException {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, boolean create) throws IOException {
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

    public static ChunkSection createChunkSection(byte Y) {
        return null;
    }

    @Override
    public boolean isChunkGenerated(int chunkX, int chunkZ) throws IOException {
        RegionLoader region = this.getRegion(chunkX >> 5, chunkZ >> 5);
        return region != null && region.chunkExists(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32) && this.getChunk(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32, true).isGenerated();
    }

    @Override
    public boolean isChunkPopulated(int chunkX, int chunkZ) throws IOException {
        Chunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.isPopulated();
    }

    protected void loadRegion(int x, int z) throws IOException {
        String index = Level.chunkHash(x, z);
        if (!this.regions.containsKey(index)) {
            this.regions.put(index, new RegionLoader(this, x, z));
        }
    }

    @Override
    public void close() throws Exception {
        this.unloadChunks();
        for (Map.Entry entry : this.regions.entrySet()) {
            String index = (String) entry.getKey();
            RegionLoader region = (RegionLoader) entry.getValue();
            region.close();
            this.regions.remove(index);
        }
        this.level = null;
    }
}
