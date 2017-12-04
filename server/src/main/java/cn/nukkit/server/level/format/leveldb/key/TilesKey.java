package cn.nukkit.server.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TilesKey extends BaseKey {
    protected TilesKey(int chunkX, int chunkZ) {
        super(chunkX, chunkZ, DATA_TILES);
    }

    public static TilesKey create(int chunkX, int chunkZ) {
        return new TilesKey(chunkX, chunkZ);
    }
}
