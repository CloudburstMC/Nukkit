package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TicksKey extends BaseKey {

    protected TicksKey(final int chunkX, final int chunkZ) {
        super(chunkX, chunkZ, BaseKey.DATA_TICKS);
    }

    public static TicksKey create(final int chunkX, final int chunkZ) {
        return new TicksKey(chunkX, chunkZ);
    }

}
