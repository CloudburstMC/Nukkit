package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FlagsKey extends BaseKey {

    protected FlagsKey(final int chunkX, final int chunkZ) {
        super(chunkX, chunkZ, BaseKey.DATA_FLAGS);
    }

    public static FlagsKey create(final int chunkX, final int chunkZ) {
        return new FlagsKey(chunkX, chunkZ);
    }

}
