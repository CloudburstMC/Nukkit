package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class VersionKey extends BaseKey {

    protected VersionKey(final int chunkX, final int chunkZ) {
        super(chunkX, chunkZ, BaseKey.DATA_VERSION);
    }

    public static VersionKey create(final int chunkX, final int chunkZ) {
        return new VersionKey(chunkX, chunkZ);
    }

}
