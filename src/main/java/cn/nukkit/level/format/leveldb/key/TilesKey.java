package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TilesKey extends BaseKey {

    protected TilesKey(final int chunkX, final int chunkZ) {
        super(chunkX, chunkZ, BaseKey.DATA_TILES);
    }

    public static TilesKey create(final int chunkX, final int chunkZ) {
        return new TilesKey(chunkX, chunkZ);
    }

}
