package cn.nukkit.level.chunk;

import cn.nukkit.level.BlockUpdate;
import cn.nukkit.level.Level;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;

import java.util.ArrayList;
import java.util.List;

public class ChunkBuilder {

    private final int x;
    private final int z;
    private final Level level;
    private final Int2ShortMap extraData = new Int2ShortOpenHashMap();
    private final List<BlockUpdate> blockUpdates = new ArrayList<>();
    private final List<ChunkDataLoader> chunkDataLoaders = new ArrayList<>();
    private ChunkSection[] sections;
    private byte[] biomes;
    private int[] heightMap;
    private boolean dirty;
    private int state = IChunk.STATE_NEW;

    public ChunkBuilder(int x, int z, Level level) {
        this.x = x;
        this.z = z;
        this.level = Preconditions.checkNotNull(level, "level");
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public ChunkBuilder sections(ChunkSection[] sections) {
        this.sections = Preconditions.checkNotNull(sections, "sections");
        return this;
    }

    public ChunkBuilder extraData(int key, short value) {
        this.extraData.put(key, value);
        return this;
    }

    public ChunkBuilder biomes(byte[] biomes) {
        this.biomes = Preconditions.checkNotNull(biomes, "biomes");
        return this;
    }

    public ChunkBuilder heightMap(int[] heightMap) {
        this.heightMap = Preconditions.checkNotNull(heightMap, "heightMap");
        return this;
    }

    public ChunkBuilder blockUpdate(BlockUpdate blockUpdate) {
        Preconditions.checkNotNull(blockUpdate, "blockUpdate");
        this.blockUpdates.add(blockUpdate);
        return this;
    }

    public ChunkBuilder dataLoader(ChunkDataLoader chunkDataLoader) {
        Preconditions.checkNotNull(chunkDataLoader, "chunkDataLoader");
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

    public Chunk build() {
        Preconditions.checkNotNull(this.sections, "sections");
        Preconditions.checkNotNull(this.biomes, "biomes");
        Preconditions.checkNotNull(this.heightMap, "heightMap");
        Chunk chunk = new Chunk(new UnsafeChunk(this.x, this.z, this.level, this.sections, this.biomes,
                this.heightMap), this.chunkDataLoaders, this.blockUpdates);
        if (this.state != IChunk.STATE_NEW)  {
            chunk.setState(this.state);
        }
        chunk.setDirty(this.dirty);
        return chunk;
    }

    public interface Factory {

        ChunkBuilder create(int x, int z);
    }
}
