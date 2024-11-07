package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.StateBlockStorage;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

public class ChunkSectionSerializers {
    private static final ChunkSectionSerializer[] SERIALIZERS = new ChunkSectionSerializer[10];

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
        SERIALIZERS[9] = ChunkSectionSerializerV9.INSTANCE;
    }

    public static void serialize(ByteBuf buf, StateBlockStorage[] storage, int ySection, int version) {
        getSerializer(version).serialize(buf, storage, ySection);
    }

    public static StateBlockStorage[] deserialize(ByteBuf buf, ChunkBuilder builder, int version) {
        return getSerializer(version).deserialize(buf, builder);
    }

    public static ChunkSectionSerializer getSerializer(int version) {
        Preconditions.checkElementIndex(version, SERIALIZERS.length, "Invalid sub-chunk version " + version);
        ChunkSectionSerializer serializer = SERIALIZERS[version];
        if (serializer == null) throw new NullPointerException("No ChunkSectionSerializer for version " + version);
        return serializer;
    }
}
