package cn.nukkit.level.format.leveldb;

import static org.cloudburstmc.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext.makeVersion;

public class LevelDBConstants {
    // This is protocol version if block palette used in storage
    public static final int PALETTE_VERSION = 594; //v1_20_10;
    // By combining this versions we can get block state version
    // NOTE: This is not necessary bumped everytime with PALETTE_VERSION
    // Last time this was bumped in 1.18.10
    public static final int STATE_MAYOR_VERSION = 1;
    public static final int STATE_MINOR_VERSION = 20;
    public static final int STATE_PATCH_VERSION = 10;
    public static final int STATE_VERSION = makeVersion(STATE_MAYOR_VERSION, STATE_MINOR_VERSION, STATE_PATCH_VERSION) + 32; // 32 updaters were added in 1.20.10
    // Chunk version that will be currently used as default
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
