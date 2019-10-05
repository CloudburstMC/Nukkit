package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.level.chunk.BlockStorage;
import cn.nukkit.level.chunk.ChunkBuilder;
import io.netty.buffer.ByteBuf;

public interface ChunkSectionSerializer {

    void serialize(ByteBuf buf, BlockStorage[] storage);

    BlockStorage[] deserialize(ByteBuf buf, ChunkBuilder builder);
}
