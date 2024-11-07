package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.util.PalettedBlockStorage;

import java.io.IOException;
import java.util.Map;

/**
 * @author MagicDroidX
 * Nukkit Project
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

    default int getFullBlock(int x, int y, int z) {
        return this.getFullBlock(x, y, z, Block.LAYER_NORMAL);
    }

    int getFullBlock(int x, int y, int z, BlockLayer layer);

    default Block getAndSetBlock(int x, int y, int z, Block block) {
        return this.getAndSetBlock(x, y, z, Block.LAYER_NORMAL, block);
    }

    Block getAndSetBlock(int x, int y, int z, BlockLayer layer, Block block);

    default boolean setFullBlockId(int x, int y, int z, int fullId) {
        return setFullBlockId(x, y, z, Block.LAYER_NORMAL, fullId);
    }

    default boolean setFullBlockId(int x, int y, int z, BlockLayer layer, int fullId) {
        return setBlockAtLayer(x, y, z, layer, fullId >> Block.DATA_BITS, fullId & Block.DATA_MASK);
    }

    boolean setBlock(int x, int y, int z, int blockId);
    
    boolean setBlock(int x, int y, int z, int blockId, int meta);

    boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id);

    boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id, int data);

    default int getBlockId(int x, int y, int z) {
        return this.getBlockId(x, y, z, Block.LAYER_NORMAL);
    }

    int getBlockId(int x, int y, int z, BlockLayer layer);

    default void setBlockId(int x, int y, int z, int id) {
        this.setBlockId(x, y, z, Block.LAYER_NORMAL, id);
    }

    void setBlockId(int x, int y, int z, BlockLayer layer, int id);

    default int getBlockData(int x, int y, int z) {
        return this.getBlockData(x, y, z, Block.LAYER_NORMAL);
    }

    int getBlockData(int x, int y, int z, BlockLayer layer);

    default void setBlockData(int x, int y, int z, int data) {
        this.setBlockData(x, y, z, Block.LAYER_NORMAL, data);
    }

    void setBlockData(int x, int y, int z, BlockLayer layer, int data);

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

    void populateSkyLight();

    default boolean has3dBiomes() {
        return false;
    }

    default PalettedBlockStorage getBiomeStorage(int y) {
        return null;
    }

    int getBiomeId(int x, int z);

    default int getBiomeId(int x, int y, int z) {
        return this.getBiomeId(x, z);
    }

    default void setBiomeId(int x, int y, int z, int biomeId)  {
        this.setBiomeId(x, y, z, (byte) biomeId);
    }

    default void setBiomeId(int x, int z, int biomeId)  {
        setBiomeId(x, z, (byte) biomeId);
    }

    default void setBiomeId(int x, int y, int z, byte biomeId) {
        this.setBiomeId(x, z, biomeId);
    }

    void setBiomeId(int x, int z, byte biomeId);

    default void setBiome(int x, int z, cn.nukkit.level.biome.Biome biome) {
        setBiomeId(x, z, biome.getId());
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

    void setBiomeIdArray(byte[] biomeIdArray);

    byte[] getHeightMapArray();

    @Deprecated
    byte[] getBlockIdArray();

    @Deprecated
    byte[] getBlockDataArray();

    Map<Integer, Integer> getBlockExtraDataArray();

    @Deprecated
    byte[] getBlockSkyLightArray();

    @Deprecated
    byte[] getBlockLightArray();

    byte[] toBinary();

    @Deprecated
    byte[] toFastBinary();

    boolean hasChanged();

    void setChanged();

    void setChanged(boolean changed);
}