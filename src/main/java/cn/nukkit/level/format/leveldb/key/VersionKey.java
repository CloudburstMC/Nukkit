package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class VersionKey extends BaseKey {
    protected VersionKey(int chunkX, int chunkZ) {
        super(chunkX, chunkZ, DATA_VERSION);
    }

    public static VersionKey create(int chunkX, int chunkZ) {
        return new VersionKey(chunkX, chunkZ);
    }
}
