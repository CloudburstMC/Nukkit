package cn.nukkit.level.chunk;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 * A very fast chunk implementation, used during initial terrain generation.
 * <p>
 * This class is not thread-safe.
 *
 * @author DaPorkchop_
 */
public final class ChunkPrimer {
    private static final int SECTION_SIZE = 16 * 16 * 16;
    private static final int LAYER_SIZE   = SECTION_SIZE * Chunk.SECTION_COUNT;
    private static final int LAYER_COUNT = 2;
    private static final int CHUNK_SIZE   = LAYER_SIZE * LAYER_COUNT;

    private static int index(int x, int y, int z, int layer)    {
        ChunkSection.checkBounds(x, y, z);
        Preconditions.checkArgument(layer >= 0 && layer < LAYER_COUNT, "layer (%s) is not between 0 and %s", layer, LAYER_COUNT);

        return ChunkSection.blockIndex(x, y, z) | layer * LAYER_SIZE;
    }

    private final char[] data = new char[CHUNK_SIZE];

    public void setBlock(int x, int y, int z, Block block)  {
        this.setBlock(x, y, z, 0, block);
    }

    public void setBlock(int x, int y, int z, int layer, Block block)  {
        this.data[index(x, y, z, layer)] = (char) BlockRegistry.get().getRuntimeId(block);
    }

    public Block getBlock(int x, int y, int z)   {
        return this.getBlock(x, y, z, 0);
    }

    public Block getBlock(int x, int y, int z, int layer)   {
        return BlockRegistry.get().getBlock(this.data[index(x, y, z, layer)]);
    }

    public void setBlockId(int x, int y, int z, Identifier id)  {
        this.setBlockId(x, y, z, 0, id);
    }

    public void setBlockId(int x, int y, int z, int layer, Identifier id)  {
        this.data[index(x, y, z, layer)] = (char) BlockRegistry.get().getRuntimeId(id, 0);
    }

    public Identifier getBlockId(int x, int y, int z)   {
        return this.getBlockId(x, y, z, 0);
    }

    public Identifier getBlockId(int x, int y, int z, int layer)   {
        return BlockRegistry.get().getBlock(this.data[index(x, y, z, layer)]).getId();
    }

    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int runtimeId)  {
        this.setBlockRuntimeIdUnsafe(x, y, z, 0, runtimeId);
    }

    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId)  {
        this.data[index(x, y, z, layer)] = (char) runtimeId;
    }

    public int getBlockRuntimeIdUnsafe(int x, int y, int z)   {
        return this.getBlockRuntimeIdUnsafe(x, y, z, 0);
    }

    public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer)   {
        return this.data[index(x, y, z, layer)];
    }

    public void setBlockData(int x, int y, int z, int data)  {
        this.setBlockData(x, y, z, 0, data);
    }

    public void setBlockData(int x, int y, int z, int layer, int data)  {
        int index = index(x, y, z, layer);
        int oldId = this.data[index];
        this.data[index] = (char) BlockRegistry.get().getRuntimeId(BlockRegistry.get().getBlock(oldId).getId(), data);
    }

    public int getBlockData(int x, int y, int z)   {
        return this.getBlockData(x, y, z, 0);
    }

    public int getBlockData(int x, int y, int z, int layer)   {
        return BlockRegistry.get().getBlock(this.data[index(x, y, z, layer)]).getMeta();
    }

    /**
     * Clears this chunk primer, resetting all blocks to air.
     */
    public void clear() {
        Arrays.fill(this.data, (char) 0);
    }
}
