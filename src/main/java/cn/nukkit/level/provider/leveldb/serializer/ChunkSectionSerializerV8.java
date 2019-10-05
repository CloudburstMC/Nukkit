package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.level.chunk.BlockStorage;
import cn.nukkit.level.chunk.ChunkBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChunkSectionSerializerV8 implements ChunkSectionSerializer {

    public static final ChunkSectionSerializer INSTANCE = new ChunkSectionSerializerV8();

    @Override
    public void serialize(ByteBuf buf, BlockStorage[] storage) {
        buf.writeByte(storage.length);

        for (BlockStorage blockStorage : storage) {
            blockStorage.writeToStorage(buf);
        }
    }

    @Override
    public BlockStorage[] deserialize(ByteBuf buf, ChunkBuilder builder) {
        int storageCount = buf.readUnsignedByte();
        BlockStorage[] storage = new BlockStorage[Math.max(storageCount, 2)];

        for (int i = 0; i < storageCount; i++) {
            storage[i] = new BlockStorage();
            storage[i].readFromStorage(buf);
        }
        return storage;
    }
}
