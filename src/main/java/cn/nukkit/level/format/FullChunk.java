package cn.nukkit.level.format;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.biome.Biome;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface FullChunk extends Cloneable {

    int getX();

    int getZ();

    default void setPosition(int x, int z) {
        setX(x);
        setZ(z);
    }

    void setX(int x);

    void setZ(int z);

    long getIndex();

    LevelProvider getProvider();

    void setProvider(LevelProvider provider);

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    int getFullBlock(int x, int y, int z);

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    int getFullBlock(int x, int y, int z, int layer);

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    default int getBlockRuntimeId(int x, int y, int z) {
        return getBlockRuntimeId(x, y, z, 0);
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    default int getBlockRuntimeId(int x, int y, int z, int layer) {
        return getBlockState(x, y, z, layer).getRuntimeId();
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default BlockState getBlockState(int x, int y, int z) {
        return getBlockState(x, y, z, 0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default BlockState getBlockState(int x, int y, int z, int layer) {
        int full = getFullBlock(x, y, z, layer);
        return BlockState.of(full >> Block.DATA_BITS, full & Block.DATA_MASK);
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "If the stored state is invalid, returns a BlockUnknown",
            replaceWith = "getAndSetBlockState")
    Block getAndSetBlock(int x, int y, int z, Block block);

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "If the stored state is invalid, returns a BlockUnknown",
            replaceWith = "getAndSetBlockState")
    @PowerNukkitOnly
    Block getAndSetBlock(int x, int y, int z, int layer, Block block);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default BlockState getAndSetBlockState(int x, int y, int z, BlockState state) {
        return getAndSetBlockState(x, y, z, 0, state);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "setBlock(int x, int y, int z, int  blockId, int  meta)")
    default boolean setFullBlockId(int x, int y, int z, int fullId) {
        return setFullBlockId(x, y, z, 0, fullId >> Block.DATA_BITS);
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "setBlockAtLayer(int x, int y, int z, int layer, int  blockId)")
    default boolean setFullBlockId(int x, int y, int z, int layer, int fullId) {
        return setBlockAtLayer(x, y, z, layer, fullId >> Block.DATA_BITS, fullId & Block.DATA_MASK);
    }

    boolean setBlock(int x, int y, int z, int blockId);
    
    @PowerNukkitOnly
    boolean setBlockAtLayer(int x, int y, int z, int layer, int  blockId);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default boolean setBlockState(int x, int y, int z, BlockState state) {
        return setBlockStateAtLayer(x, y, z, 0, state);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    boolean setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state);
    
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    boolean setBlock(int x, int y, int z, int  blockId, int  meta);

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    boolean setBlockAtLayer(int x, int y, int z, int layer, int blockId, int  meta);

    int getBlockId(int x, int y, int z);

    @PowerNukkitOnly
    int getBlockId(int x, int y, int z, int layer);

    void setBlockId(int x, int y, int z, int id);

    @PowerNukkitOnly
    void setBlockId(int x, int y, int z, int layer, int id);

    int getBlockData(int x, int y, int z);

    @PowerNukkitOnly
    int getBlockData(int x, int y, int z, int layer);

    void setBlockData(int x, int y, int z, int data);

    @PowerNukkitOnly
    void setBlockData(int x, int y, int z, int layer, int data);

    int getBlockExtraData(int x, int y, int z);

    void setBlockExtraData(int x, int y, int z, int data);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    int getHighestBlockAt(int x, int z);

    int getHighestBlockAt(int x, int z, boolean cache);

    int getHeightMap(int x, int z);

    void setHeightMap(int x, int z, int value);

    void recalculateHeightMap();

    int recalculateHeightMapColumn(int chunkX, int chunkZ);

    void populateSkyLight();

    int getBiomeId(int x, int z);

    void setBiomeId(int x, int z, byte biomeId);

    default void setBiomeId(int x, int z, int biomeId)  {
        setBiomeId(x, z, (byte) biomeId);
    }

    default void setBiome(int x, int z, Biome biome) {
        setBiomeId(x, z, (byte) biome.getId());
    }

    boolean isLightPopulated();

    void setLightPopulated();

    void setLightPopulated(boolean value);

    boolean isPopulated();

    void setPopulated();

    void setPopulated(boolean value);

    boolean isGenerated();

    void setGenerated();

    void setGenerated(boolean value);

    void addEntity(Entity entity);

    void removeEntity(Entity entity);

    void addBlockEntity(BlockEntity blockEntity);

    void removeBlockEntity(BlockEntity blockEntity);

    Map<Long, Entity> getEntities();

    Map<Long, BlockEntity> getBlockEntities();

    BlockEntity getTile(int x, int y, int z);

    boolean isLoaded();

    boolean load() throws IOException;

    boolean load(boolean generate) throws IOException;

    boolean unload() throws Exception;

    boolean unload(boolean save) throws Exception;

    boolean unload(boolean save, boolean safe) throws Exception;

    void initChunk();

    byte[] getBiomeIdArray();

    byte[] getHeightMapArray();

    Map<Integer, Integer> getBlockExtraDataArray();

    byte[] getBlockSkyLightArray();

    byte[] getBlockLightArray();

    byte[] toBinary();

    byte[] toFastBinary();

    boolean hasChanged();

    void setChanged();

    void setChanged(boolean changed);

    boolean isBlockChangeAllowed(int x, int y, int z);
    
    @Nonnull
    List<Block> findBorders(int x, int z);
    
    boolean isBlockedByBorder(int x, int z);
}
