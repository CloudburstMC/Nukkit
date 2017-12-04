package cn.nukkit.server.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TicksKey extends BaseKey {
    protected TicksKey(int chunkX, int chunkZ) {
        super(chunkX, chunkZ, DATA_TICKS);
    }

    public static TicksKey create(int chunkX, int chunkZ) {
        return new TicksKey(chunkX, chunkZ);
    }
}
