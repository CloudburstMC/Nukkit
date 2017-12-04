package cn.nukkit.server.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FlagsKey extends BaseKey {
    protected FlagsKey(int chunkX, int chunkZ) {
        super(chunkX, chunkZ, DATA_FLAGS);
    }

    public static FlagsKey create(int chunkX, int chunkZ) {
        return new FlagsKey(chunkX, chunkZ);
    }
}
