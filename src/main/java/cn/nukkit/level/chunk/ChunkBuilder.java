package cn.nukkit.level.chunk;

import cn.nukkit.level.BlockUpdate;
import cn.nukkit.level.Level;
import com.google.common.base.Preconditions;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.hash.TIntShortHashMap;

import java.util.ArrayList;
import java.util.List;

public class ChunkBuilder {

    private final int x;
    private final int z;
    private final Level level;
    private final TIntShortMap extraData = new TIntShortHashMap();
    private final List<BlockUpdate> blockUpdates = new ArrayList<>();
    private final List<ChunkDataLoader> chunkDataLoaders = new ArrayList<>();
    private ChunkSection[] sections;
    private byte[] biomes;
    private int[] heightMap;
    private boolean dirty;
    private boolean generated;
    private boolean populated;

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

    public ChunkBuilder generated() {
        this.generated = true;
        return this;
    }

    public ChunkBuilder populated() {
        this.populated = true;
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
        chunk.setGenerated(this.generated);
        chunk.setPopulated(this.populated);
        chunk.setDirty(this.dirty);
        return chunk;
    }

    public interface Factory {

        ChunkBuilder create(int x, int z);
    }
}
