package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TerrainKey extends BaseKey {

    protected TerrainKey(final int chunkX, final int chunkZ) {
        super(chunkX, chunkZ, BaseKey.DATA_TERRAIN);
    }

    public static TerrainKey create(final int chunkX, final int chunkZ) {
        return new TerrainKey(chunkX, chunkZ);
    }

}
