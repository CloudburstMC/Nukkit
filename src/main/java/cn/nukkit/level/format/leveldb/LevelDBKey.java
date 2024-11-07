package cn.nukkit.level.format.leveldb;

public enum LevelDBKey {
    DATA_3D('+'),
    VERSION(','),
    DATA_2D('-'),
    DATA_2D_LEGACY('.'),
    SUB_CHUNK_PREFIX('/'),
    LEGACY_TERRAIN('0'),
    BLOCK_ENTITIES('1'),
    ENTITIES('2'),
    PENDING_TICKS('3'),
    BLOCK_EXTRA_DATA('4'),
    BIOME_STATE('5'),
    STATE_FINALIZATION('6'),
    CONVERSION_DATA('7'),
    BORDER_BLOCKS('8'),
    HARDCODED_SPAWNERS('9'),
    RANDOM_TICKS(':'),
    CHECKSUMS(';'),
    GENERATION_SEED('<'),
    META_DATA_HASH('='),
    GENERATED_PRE_CAVES_AND_CLIFFS_BLENDING('='),
    BLENDING_BIOME_HEIGHT('>'),
    METADATA_HASH('?'),
    BLENDING_DATA('@'),
    ACTOR_DIGEST_VERSION('A'),
    FLAGS('f'),
    VERSION_OLD('v'),
    NUKKIT_BLOCK_LIGHT((char) 1000),
    NUKKIT_SKY_LIGHT((char) 1001);

    private final byte encoded;

    LevelDBKey(char encoded) {
        this.encoded = (byte) encoded;
    }

    public byte[] getKey(int chunkX, int chunkZ, int dimension) {
        if (dimension == 0) {
            return this.getKey(chunkX, chunkZ, false, 0);
        } else {
            return this.getKey(chunkX, chunkZ, dimension, false, 0);
        }
    }

    public byte[] getKey(int chunkX, int chunkZ, int y, int dimension) {
        if (dimension == 0) {
            return this.getKey(chunkX, chunkZ, true, y);
        } else {
            return this.getKey(chunkX, chunkZ, dimension, true, y);
        }
    }

    private byte[] getKey(int chunkX, int chunkZ, boolean extend, int y) {
        byte[] bytes = new byte[extend ? 10 : 9];
        bytes[0] = (byte) (chunkX & 0xff);
        bytes[1] = (byte) ((chunkX >>> 8) & 0xff);
        bytes[2] = (byte) ((chunkX >>> 16) & 0xff);
        bytes[3] = (byte) ((chunkX >>> 24) & 0xff);
        bytes[4] = (byte) (chunkZ & 0xff);
        bytes[5] = (byte) ((chunkZ >>> 8) & 0xff);
        bytes[6] = (byte) ((chunkZ >>> 16) & 0xff);
        bytes[7] = (byte) ((chunkZ >>> 24) & 0xff);
        bytes[8] = this.encoded;
        if (extend) {
            bytes[9] = (byte) y;
        }
        return bytes;
    }

    private byte[] getKey(int chunkX, int chunkZ, int dimension, boolean extend, int y) {
        byte[] bytes = new byte[extend ? 14 : 13];
        bytes[0] = (byte) (chunkX & 0xff);
        bytes[1] = (byte) ((chunkX >>> 8) & 0xff);
        bytes[2] = (byte) ((chunkX >>> 16) & 0xff);
        bytes[3] = (byte) ((chunkX >>> 24) & 0xff);
        bytes[4] = (byte) (chunkZ & 0xff);
        bytes[5] = (byte) ((chunkZ >>> 8) & 0xff);
        bytes[6] = (byte) ((chunkZ >>> 16) & 0xff);
        bytes[7] = (byte) ((chunkZ >>> 24) & 0xff);
        bytes[8] = (byte) (dimension & 0xff);
        bytes[9] = (byte) ((dimension >>> 8) & 0xff);
        bytes[10] = (byte) ((dimension >>> 16) & 0xff);
        bytes[11] = (byte) ((dimension >>> 24) & 0xff);
        bytes[12] = this.encoded;
        if (extend) {
            bytes[13] = (byte) y;
        }
        return bytes;
    }
}
