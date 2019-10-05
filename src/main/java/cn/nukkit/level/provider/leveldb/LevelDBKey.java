package cn.nukkit.level.provider.leveldb;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LevelDBKey {

    public static byte[] getKey(int chunkX, int chunkZ, Type type) {
        return new byte[]{
                (byte) (chunkX & 0xff),
                (byte) ((chunkX >>> 8) & 0xff),
                (byte) ((chunkX >>> 16) & 0xff),
                (byte) ((chunkX >>> 24) & 0xff),
                (byte) (chunkZ & 0xff),
                (byte) ((chunkZ >>> 8) & 0xff),
                (byte) ((chunkZ >>> 16) & 0xff),
                (byte) ((chunkZ >>> 24) & 0xff),
                type.encoded
        };
    }

    public static byte[] getKey(int chunkX, int chunkZ, Type type, int y) {
        return new byte[]{
                (byte) (chunkX & 0xff),
                (byte) ((chunkX >>> 8) & 0xff),
                (byte) ((chunkX >>> 16) & 0xff),
                (byte) ((chunkX >>> 24) & 0xff),
                (byte) (chunkZ & 0xff),
                (byte) ((chunkZ >>> 8) & 0xff),
                (byte) ((chunkZ >>> 16) & 0xff),
                (byte) ((chunkZ >>> 24) & 0xff),
                type.encoded,
                (byte) y
        };
    }

    public enum Type {
        DATA_2D('-'),
        DATA_2D_LEGACY('.'),
        SUBCHUNK_PREFIX('/'),
        LEGACY_TERRAIN('0'),
        BLOCK_ENTITIES('1'),
        ENTITIES('2'),
        PENDING_TICKS('3'),
        BLOCK_EXTRA_DATA('4'),
        BIOME_STATE('5'),
        STATE_FINALIZATION('6'),

        BORDER_BLOCKS('8'),
        HARDCODED_SPAWNERS('9'),

        FLAGS('f'),

        VERSION('v');

        private final byte encoded;

        Type(char encoded) {
            this.encoded = (byte) encoded;
        }
    }
}
