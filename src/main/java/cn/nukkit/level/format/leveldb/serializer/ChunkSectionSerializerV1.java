package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.StateBlockStorage;
import io.netty.buffer.ByteBuf;

public class ChunkSectionSerializerV1 implements ChunkSectionSerializer {

    public static final ChunkSectionSerializer INSTANCE = new ChunkSectionSerializerV1();

    @Override
    public void serialize(ByteBuf buf, StateBlockStorage[] storage, int ySection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StateBlockStorage[] deserialize(ByteBuf buf, ChunkBuilder builder) {
        StateBlockStorage[] storage = new StateBlockStorage[2];
        storage[0] = new StateBlockStorage();
        storage[0].readFromStorage(buf, builder);
        return storage;
    }
}
