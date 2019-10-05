package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.level.chunk.BlockStorage;
import cn.nukkit.level.chunk.ChunkBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChunkSectionSerializerV1 implements ChunkSectionSerializer {

    public static final ChunkSectionSerializer INSTANCE = new ChunkSectionSerializerV1();

    @Override
    public void serialize(ByteBuf buf, BlockStorage[] storage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockStorage[] deserialize(ByteBuf buf, ChunkBuilder builder) {
        BlockStorage[] storage = new BlockStorage[2];
        storage[0] = new BlockStorage();
        storage[0].readFromStorage(buf);
        return storage;
    }
}
