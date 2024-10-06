package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.StateBlockStorage;
import io.netty.buffer.ByteBuf;

public class ChunkSectionSerializerV9 implements ChunkSectionSerializer {

    public static final ChunkSectionSerializer INSTANCE = new ChunkSectionSerializerV9();

    @Override
    public void serialize(ByteBuf buf, StateBlockStorage[] storage, int ySection) {
        buf.writeByte(storage.length);
        buf.writeByte(ySection);
        for (StateBlockStorage blockStorage : storage) {
            blockStorage.writeToStorage(buf);
        }
    }

    @Override
    public StateBlockStorage[] deserialize(ByteBuf buf, ChunkBuilder builder) {
        int storageCount = buf.readUnsignedByte();
        buf.readUnsignedByte(); // ySection

        StateBlockStorage[] storage = new StateBlockStorage[Math.max(storageCount, 2)];
        for (int i = 0; i < storageCount; i++) {
            storage[i] = new StateBlockStorage();
            storage[i].readFromStorage(buf, builder);
        }
        return storage;
    }
}
