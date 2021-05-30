package cn.nukkit.level.format;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ParametersAreNonnullByDefault
public interface ChunkSection {
    int getY();

    int getBlockId(int x, int y, int z);

    @PowerNukkitOnly
    int getBlockId(int x, int y, int z, int layer);

    void setBlockId(int x, int y, int z, int id);

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    int getBlockData(int x, int y, int z);

    @PowerNukkitOnly
    int getBlockData(int x, int y, int z, int layer);

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    void setBlockData(int x, int y, int z, int data);

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    void setBlockData(int x, int y, int z, int layer, int data);

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    int getFullBlock(int x, int y, int z);

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    int getFullBlock(int x, int y, int z, int layer);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default BlockState getBlockState(int x, int y, int z) {
        return getBlockState(x, y, z, 0);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default BlockState getBlockState(int x, int y, int z, int layer) {
        return BlockState.of(getBlockId(x, y, z, layer), getBlockData(x, y, z, layer));
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "If the stored state is invalid, returns a BlockUnknown",
            replaceWith = "getAndSetBlockState")
    @PowerNukkitOnly
    @Nonnull
    Block getAndSetBlock(int x, int y, int z, int layer, Block block);

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "If the stored state is invalid, returns a BlockUnknown",
            replaceWith = "getAndSetBlockState")
    @Nonnull
    Block getAndSetBlock(int x, int y, int z, Block block);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default BlockState getAndSetBlockState(int x, int y, int z, BlockState state) {
        return getAndSetBlockState(x, y, z, 0, state);
    }

    @PowerNukkitOnly
    void setBlockId(int x, int y, int z, int layer, int id);

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "setBlockState(int x, int y, int z, BlockState state)")
    boolean setFullBlockId(int x, int y, int z, int fullId);

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state)")
    boolean setFullBlockId(int x, int y, int z, int layer, int fullId);

    @PowerNukkitOnly
    boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId);

    boolean setBlock(int x, int y, int z, int blockId);

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    boolean setBlock(int x, int y, int z, int blockId, int meta);

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int meta);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);
    
    byte[] getSkyLightArray();

    byte[] getLightArray();

    boolean isEmpty();

    @Since("1.4.0.0-PN")
    void writeTo(BinaryStream stream);

    @PowerNukkitOnly
    int getMaximumLayer();

    @PowerNukkitOnly
    @Nonnull
    CompoundTag toNBT();

    @Nonnull
    ChunkSection copy();
    
    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    default int getContentVersion() {
        return 0;
    }

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.1.0-PN")
    default void setContentVersion(int contentVersion) {
        // Does nothing
    }
    
    @PowerNukkitOnly()
    @Since("1.4.0.0-PN")
    default boolean hasBlocks() {
        return !isEmpty();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean setBlockState(int x, int y, int z, BlockState state) {
        return setBlockStateAtLayer(x, y, z, 0, state);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int getBlockChangeStateAbove(int x, int y, int z);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default void delayPaletteUpdates() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default List<Block> scanBlocks(LevelProvider provider, int offsetX, int offsetZ, BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        int offsetY = getY() << 4;
        List<Block> results = new ArrayList<>();

        BlockVector3 current = new BlockVector3();
        
        int minX = Math.max(0, min.x - offsetX);
        int minY = Math.max(0, min.y - offsetY);
        int minZ = Math.max(0, min.z - offsetZ);

        for (int x = Math.min(max.x - offsetX, 15); x >= minX; x--) {
            current.x = offsetX + x;
            for (int z = Math.min(max.z - offsetZ, 15); z >= minZ; z--) {
                current.z = offsetZ + z;
                for (int y = Math.min(max.y - offsetY, 15); y >= minY; y--) {
                    current.y = offsetY + y;
                    BlockState state = getBlockState(x, y, z);
                    if (condition.test(current, state)) {
                        results.add(state.getBlockRepairing(provider.getLevel(), current, 0));
                    }
                }
            }
        }
        
        return results;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default void compressStorageLayers() {
    }
}
