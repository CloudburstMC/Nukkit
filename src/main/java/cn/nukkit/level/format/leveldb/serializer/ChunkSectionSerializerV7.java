package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.level.format.anvil.util.NibbleArray;
import cn.nukkit.level.format.leveldb.BlockStateMapping;
import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.StateBlockStorage;
import io.netty.buffer.ByteBuf;

public class ChunkSectionSerializerV7 implements ChunkSectionSerializer {

    public static final ChunkSectionSerializer INSTANCE = new ChunkSectionSerializerV7();

    @Override
    public void serialize(ByteBuf buf, StateBlockStorage[] storage, int ySection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StateBlockStorage[] deserialize(ByteBuf buf, ChunkBuilder builder) {
        byte[] blockIds = new byte[4096];
        buf.readBytes(blockIds);
        byte[] blockData = new byte[2048];
        buf.readBytes(blockData);
        if (buf.isReadable(4096)) {
            buf.skipBytes(4096); // light
        }

        StateBlockStorage[] blockStorage = new StateBlockStorage[2];
        blockStorage[0] = fromXZY(blockIds, blockData);
        return blockStorage;
    }

    private static StateBlockStorage fromXZY(byte[] blockIds, byte[] blockData) {
        NibbleArray data = new NibbleArray(blockData);
        StateBlockStorage storage = new StateBlockStorage();
        for (int i = 0; i < 4096; i++) {
            storage.setBlockStateUnsafe(i, BlockStateMapping.get().getState(blockIds[i], data.get(i)));
        }
        return storage;
    }
}
