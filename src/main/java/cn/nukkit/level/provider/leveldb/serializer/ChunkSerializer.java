package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

interface ChunkSerializer {

    void serialize(WriteBatch db, Chunk chunk);

    void deserialize(DB db, ChunkBuilder chunkBuilder);
}
