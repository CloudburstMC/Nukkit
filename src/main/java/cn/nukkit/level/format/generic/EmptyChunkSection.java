package cn.nukkit.level.format.generic;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.updater.ChunkUpdater;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ParametersAreNonnullByDefault
public class EmptyChunkSection implements ChunkSection {
    @SuppressWarnings("java:S2386")
    public static final EmptyChunkSection[] EMPTY = new EmptyChunkSection[16];
    private static final String MODIFICATION_ERROR_MESSAGE = "Tried to modify an empty Chunk";

    static {
        for (int y = 0; y < EMPTY.length; y++) {
            EMPTY[y] = new EmptyChunkSection(y);
        }
    }
    
    private static final byte[] EMPTY_2KB = new byte[2048];
    public static final byte[] EMPTY_LIGHT_ARR = EMPTY_2KB;
    public static final byte[] EMPTY_SKY_LIGHT_ARR = new byte[2048];
    static {
        Arrays.fill(EMPTY_SKY_LIGHT_ARR, (byte) 255);
    }
    
    @Since("1.4.0.0-PN") @PowerNukkitOnly public static final byte[] EMPTY_ID_ARRAY = new byte[4096];
    @Since("1.4.0.0-PN") @PowerNukkitOnly public static final byte[] EMPTY_DATA_ARRAY = EMPTY_2KB;
    private static final byte[] EMPTY_CHUNK_DATA;
    static {
        BinaryStream stream = new BinaryStream();
        stream.putByte((byte) cn.nukkit.level.format.anvil.ChunkSection.STREAM_STORAGE_VERSION);
        stream.putByte((byte) 0);
        EMPTY_CHUNK_DATA = stream.getBuffer();
    }

    private final int y;

    public EmptyChunkSection(int y) {
        this.y = y;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public final int getBlockId(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getBlockId(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return 0;
    }

    @Nonnull
    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        return BlockState.AIR;
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Nonnull
    @Override
    public Block getAndSetBlock(int x, int y, int z, int layer, Block block) {
        if (block.getId() != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return Block.get(0);
    }

    @Nonnull
    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        if (block.getId() != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return Block.get(0);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state) {
        if (!BlockState.AIR.equals(state)) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return BlockState.AIR;
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, int id) {
        if (id != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta) {
        if (blockId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state) {
        if (!state.equals(BlockState.AIR)) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Override
    public byte[] getSkyLightArray() {
        return EMPTY_SKY_LIGHT_ARR;
    }

    @Override
    public byte[] getLightArray() {
        return EMPTY_LIGHT_ARR;
    }

    @Override
    public final void setBlockId(int x, int y, int z, int id) {
        if (id != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public final int getBlockData(int x, int y, int z) {
        return 0;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public int getBlockData(int x, int y, int z, int layer) {
        return 0;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public void setBlockData(int x, int y, int z, int data) {
        if (data != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public void setBlockData(int x, int y, int z, int layer, int data) {
        if (data != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public boolean setFullBlockId(int x, int y, int z, int fullId) {
        if (fullId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        if (fullId != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        return false;
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    public int getFullBlock(int x, int y, int z, int layer) {
        return 0;
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return 0;
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        if (level != 0) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return 15;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        if (level != 15) throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void writeTo(@Nonnull BinaryStream stream) {
        stream.put(EMPTY_CHUNK_DATA);
    }
    
    @Override
    public int getMaximumLayer() {
        return 0;
    }
    
    @Nonnull
    @Override
    public CompoundTag toNBT() {
        return new CompoundTag();
    }

    @Nonnull
    @Override
    public EmptyChunkSection copy() {
        return this;
    }

    @PowerNukkitOnly
    @Since("1.3.1.0-PN")
    @Override
    public int getContentVersion() {
        return ChunkUpdater.getCurrentContentVersion();
    }

    @PowerNukkitOnly
    @Since("1.3.1.0-PN")
    @Override
    public void setContentVersion(int contentVersion) {
        if (contentVersion != getContentVersion()) {
            throw new ChunkException(MODIFICATION_ERROR_MESSAGE);
        }
    }

    @Override
    public int getBlockChangeStateAbove(int x, int y, int z) {
        return 0;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public List<Block> scanBlocks(LevelProvider provider, int offsetX, int offsetZ, BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        return Collections.emptyList();
    }
}
