package cn.nukkit.level.format.leveldb.structure;

import cn.nukkit.level.format.leveldb.LevelDBConstants;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.level.format.leveldb.serializer.ChunkDataLoader;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class ChunkBuilder {

    public static final int STATE_NEW = 0;
    public static final int STATE_GENERATED = 1;
    public static final int STATE_POPULATED = 2;
    public static final int STATE_FINISHED = 3;

    private final int x;
    private final int z;
    private final LevelDBProvider provider;

    private Int2IntMap extraData;
    private final List<ChunkDataLoader> chunkDataLoaders = new ObjectArrayList<>();
    private LevelDBChunkSection[] sections;
    private byte[] biomes;
    private PalettedBlockStorage[] biomes3d;
    private boolean has3dBiomes;
    private int[] heightMap;
    private int state = STATE_NEW;
    private int chunkVersion = LevelDBConstants.LATEST_CHUNK_VERSION;

    private boolean dirty;

    public ChunkBuilder(int x, int z, LevelDBProvider provider) {
        this.x = x;
        this.z = z;
        if (provider == null) throw new NullPointerException();
        this.provider = provider;
    }

    public static short blockKey(Vector3 vector) {
        return blockKey((int) vector.getX(), (int) vector.getY(), (int) vector.getZ());
    }

    public static short blockKey(int x, int y, int z) {
        return (short) ((x & 0xf) | ((z & 0xf) << 4) | ((y & 0xff) << 9));
    }

    public static Vector3 fromKey(long chunkKey, short blockKey) {
        int x = (blockKey & 0xf) | (fromKeyX(chunkKey) << 4);
        int z = ((blockKey >>> 4) & 0xf) | (fromKeyZ(chunkKey) << 4);
        int y = (blockKey >>> 8) & 0xff;
        return new Vector3(x, y, z);
    }

    public static long key(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public static int fromKeyX(long key) {
        return (int) (key >> 32);
    }

    public static int fromKeyZ(long key) {
        return (int) key;
    }

    public static int getSectionIndex(int x, int y, int z) {
        return (x << 8) + (z << 4) + y;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public ChunkBuilder sections(LevelDBChunkSection[] sections) {
        if (sections == null) throw new NullPointerException();
        this.sections = sections;
        return this;
    }

    public ChunkBuilder extraData(int key, short value) {
        if (this.extraData == null) {
            this.extraData = new Int2IntOpenHashMap();
        }
        this.extraData.put(key, value);
        return this;
    }

    public ChunkBuilder biomes(byte[] biomes) {
        if (biomes == null) throw new NullPointerException();
        this.biomes = biomes;
        return this;
    }

    public ChunkBuilder biomes3d(PalettedBlockStorage[] biomes) {
        if (biomes == null) throw new NullPointerException();
        this.biomes3d = biomes;
        this.has3dBiomes = true;
        return this;
    }

    public ChunkBuilder heightMap(int[] heightMap) {
        if (heightMap == null) throw new NullPointerException();
        this.heightMap = heightMap;
        return this;
    }

    public ChunkBuilder dataLoader(ChunkDataLoader chunkDataLoader) {
        if (chunkDataLoader == null) throw new NullPointerException();
        this.chunkDataLoaders.add(chunkDataLoader);
        return this;
    }

    public ChunkBuilder state(int state) {
        this.state = state;
        return this;
    }

    public ChunkBuilder dirty() {
        this.dirty = true;
        return this;
    }

    public ChunkBuilder chunkVersion(int chunkVersion) {
        this.chunkVersion = chunkVersion;
        return this;
    }

    public LevelDBChunk build() {
        if (this.sections == null) throw new NullPointerException("sections");
        if (this.biomes == null && this.biomes3d == null) throw new NullPointerException("biomes");
        if (this.heightMap == null) throw new NullPointerException("sections");

        LevelDBChunk chunk = new LevelDBChunk(this.provider, this.sections, this.extraData, this.biomes, this.heightMap);
        chunk.setPosition(this.x, this.z);
        chunk.setState(this.state);
        if (this.has3dBiomes) {
            chunk.setBiomes3d(this.biomes3d);
        }

        this.chunkDataLoaders.forEach(loader -> loader.initChunk(chunk, this.provider));
        if (this.dirty) {
            chunk.setChanged();
        }
        return chunk;
    }

    public LevelDBProvider getProvider() {
        return this.provider;
    }

    public boolean has3dBiomes() {
        return this.has3dBiomes;
    }

    public int getChunkVersion() {
        return this.chunkVersion;
    }

    public String debugString() {
        return this.provider.getName() + "(x=" + this.x + ", z=" + this.z + ")";
    }
}
