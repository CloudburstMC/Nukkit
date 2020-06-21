package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ExtraDataKey extends BaseKey {

    protected ExtraDataKey(final int chunkX, final int chunkZ) {
        super(chunkX, chunkZ, BaseKey.DATA_EXTRA_DATA);
    }

    public static ExtraDataKey create(final int chunkX, final int chunkZ) {
        return new ExtraDataKey(chunkX, chunkZ);
    }

}
