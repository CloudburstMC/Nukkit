package cn.nukkit.level.format.leveldb;

public class LevelDBConstants {
    // This is protocol version if block palette used in storage
    public static final int PALETTE_VERSION = 729;
    // By combining this versions we can get block state version
    public static final int STATE_MAYOR_VERSION = 1;
    public static final int STATE_MINOR_VERSION = 21;
    public static final int STATE_PATCH_VERSION = 30;
    public static final int STATE_VERSION = 18161159;
    // Chunk version that will be currently used as default
    // NOTE: This is not necessary bumped everytime with PALETTE_VERSION
    // Last time this was bumped in 1.18.10 (TODO: 41 for 1.21.40+)
    public static final int LATEST_CHUNK_VERSION = 40;
    // SubChunk version used for serializing chunks into storage
    public static final int LATEST_SUBCHUNK_VERSION = 8;
    // Lowest SubChunk index
    public static final int MIN_SUBCHUNK_INDEX = -4;
    // Highest SubChunk index
    public static final int MAX_SUBCHUNK_INDEX = 19;
    // SubChunks count
    public static final int MAX_SUBCHUNK_COUNT = MAX_SUBCHUNK_INDEX - MIN_SUBCHUNK_INDEX + 1;
}
