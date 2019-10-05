package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.level.chunk.BlockStorage;
import cn.nukkit.level.chunk.ChunkBuilder;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

public class ChunkSectionSerializers {
    private static final ChunkSectionSerializer[] SERIALIZERS = new ChunkSectionSerializer[9];

    static {
        SERIALIZERS[0] = ChunkSectionSerializerV7.INSTANCE;
        SERIALIZERS[1] = ChunkSectionSerializerV1.INSTANCE;
        SERIALIZERS[2] = ChunkSectionSerializerV7.INSTANCE;
        SERIALIZERS[3] = ChunkSectionSerializerV7.INSTANCE;
        SERIALIZERS[4] = ChunkSectionSerializerV7.INSTANCE;
        SERIALIZERS[5] = ChunkSectionSerializerV7.INSTANCE;
        SERIALIZERS[6] = ChunkSectionSerializerV7.INSTANCE;
        SERIALIZERS[7] = ChunkSectionSerializerV7.INSTANCE;
        SERIALIZERS[8] = ChunkSectionSerializerV8.INSTANCE;
    }

    public static void serialize(ByteBuf buf, BlockStorage[] storage, int version) {
        getSerializer(version).serialize(buf, storage);
    }

    public static BlockStorage[] deserialize(ByteBuf buf, ChunkBuilder builder, int version) {
        return getSerializer(version).deserialize(buf, builder);
    }

    public static ChunkSectionSerializer getSerializer(int version) {
        Preconditions.checkElementIndex(version, SERIALIZERS.length, "Invalid sub-chunk version " + version);
        return SERIALIZERS[version];
    }
}
