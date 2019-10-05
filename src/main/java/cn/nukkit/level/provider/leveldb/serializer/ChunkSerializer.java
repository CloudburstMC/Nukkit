package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import org.iq80.leveldb.DB;

public interface ChunkSerializer {

    void serialize(DB db, Chunk chunk);

    void deserialize(DB db, ChunkBuilder chunkBuilder);
}
