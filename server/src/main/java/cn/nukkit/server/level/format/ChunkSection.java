package cn.nukkit.server.level.format;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkSection extends Cloneable {
    int getY();

    int getBlockId(int x, int y, int z);

    void setBlockId(int x, int y, int z, int id);

    int getBlockData(int x, int y, int z);

    void setBlockData(int x, int y, int z, int data);

    int getFullBlock(int x, int y, int z);

    boolean setBlock(int x, int y, int z);

    boolean setBlock(int x, int y, int z, Integer blockId);

    boolean setBlock(int x, int y, int z, Integer blockId, Integer meta);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    byte[] getBlockIdColumn(int x, int z);

    byte[] getBlockDataColumn(int x, int z);

    byte[] getBlockSkyLightColumn(int x, int z);

    byte[] getBlockLightColumn(int x, int z);

    byte[] getIdArray();

    byte[] getDataArray();

    byte[] getSkyLightArray();

    byte[] getLightArray();

    boolean isEmpty();

    byte[] getBytes();

    ChunkSection clone();
}
