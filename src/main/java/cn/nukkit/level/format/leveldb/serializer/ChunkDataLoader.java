package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;

public interface ChunkDataLoader {

    void initChunk(LevelDBChunk chunk, LevelDBProvider provider);
}
