package cn.nukkit.level.format.leveldb.key;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BaseKey {
    private final int chunkX;
    private final int chunkZ;
    private final byte type;

    public static final byte DATA_VERSION = 0x76;
    public static final byte DATA_FLAGS = 0x66;
    public static final byte DATA_EXTRA_DATA = 0x34;
    public static final byte DATA_TICKS = 0x33;
    public static final byte DATA_ENTITIES = 0x32;
    public static final byte DATA_TILES = 0x31;
    public static final byte DATA_TERRAIN = 0x30;

    protected BaseKey(int chunkX, int chunkZ, byte type) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.type = type;
    }

    public byte[] toArray() {
        return new byte[]{
                (byte) (this.chunkX & 0xff),
                (byte) ((this.chunkX >>> 8) & 0xff),
                (byte) ((this.chunkX >>> 16) & 0xff),
                (byte) ((this.chunkX >>> 24) & 0xff),
                (byte) (this.chunkZ & 0xff),
                (byte) ((this.chunkZ >>> 8) & 0xff),
                (byte) ((this.chunkZ >>> 16) & 0xff),
                (byte) ((this.chunkZ >>> 24) & 0xff),
                this.type
        };
    }
}
