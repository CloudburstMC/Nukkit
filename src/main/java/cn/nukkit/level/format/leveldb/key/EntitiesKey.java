package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntitiesKey extends BaseKey {

    protected EntitiesKey(final int chunkX, final int chunkZ) {
        super(chunkX, chunkZ, BaseKey.DATA_ENTITIES);
    }

    public static EntitiesKey create(final int chunkX, final int chunkZ) {
        return new EntitiesKey(chunkX, chunkZ);
    }

}
