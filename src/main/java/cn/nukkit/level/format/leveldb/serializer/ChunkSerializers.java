package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

public class ChunkSerializers {
    /*private static final IntObjectMap<ChunkSerializer> SERIALIZERS = new IntObjectHashMap<>();

    static {
        // SERIALIZERS.put(0, ChunkSerializerV1.INSTANCE);
        // SERIALIZERS.put(1, ChunkSerializerV1.INSTANCE);
        // SERIALIZERS.put(2, ChunkSerializerV1.INSTANCE);
        SERIALIZERS.put(3, ChunkSerializerV3.INSTANCE); // v1_0_0
        SERIALIZERS.put(4, ChunkSerializerV3.INSTANCE); // v1_1_0
        SERIALIZERS.put(6, ChunkSerializerV3.INSTANCE); // v1_2_0_2_beta
        SERIALIZERS.put(7, ChunkSerializerV3.INSTANCE); // v1_2_0
        SERIALIZERS.put(9, ChunkSerializerV3.INSTANCE); // v1_8_0
        SERIALIZERS.put(10, ChunkSerializerV3.INSTANCE); // v1_9_0
        SERIALIZERS.put(11, ChunkSerializerV3.INSTANCE); // v1_11_0_1_beta
        SERIALIZERS.put(12, ChunkSerializerV3.INSTANCE); // v1_11_0_3_beta
        SERIALIZERS.put(13, ChunkSerializerV3.INSTANCE); // v1_11_0_4_beta
        SERIALIZERS.put(14, ChunkSerializerV3.INSTANCE); // v1_11_1
        SERIALIZERS.put(15, ChunkSerializerV3.INSTANCE); // v1_12_0_4_beta
        SERIALIZERS.put(16, ChunkSerializerV3.INSTANCE); // v1_12_0_unused1
        SERIALIZERS.put(17, ChunkSerializerV3.INSTANCE); // v1_12_0_unused2
        SERIALIZERS.put(18, ChunkSerializerV3.INSTANCE); // v1_16_0_51_beta
        SERIALIZERS.put(19, ChunkSerializerV3.INSTANCE); // v1_16_0
        SERIALIZERS.put(22, ChunkSerializerV3.INSTANCE); // v1_16_210
        // Here are some experimental Caves & Cliffs versions from 1.16 betas
        SERIALIZERS.put(32, ChunkSerializerV3.INSTANCE); // v1_17_40_unused
        SERIALIZERS.put(33, ChunkSerializerV3.INSTANCE); // v1_18_0_20_beta
        SERIALIZERS.put(35, ChunkSerializerV3.INSTANCE); // v1_18_0_22_beta
        SERIALIZERS.put(37, ChunkSerializerV3.INSTANCE); // v1_18_0_24_beta
        SERIALIZERS.put(39, ChunkSerializerV3.INSTANCE); // v1_18_0_25_beta
        SERIALIZERS.put(40, ChunkSerializerV3.INSTANCE); // v1_18_30
    }*/

    private static ChunkSerializer getChunkSerializer(int version) {
        if (version < 3) {
            throw new IllegalArgumentException("Invalid chunk serializer version " + version + "! Serializers down to 1.0.0 (version 3) are supported");
        }
        if (version > 40) {
            throw new IllegalArgumentException("Invalid chunk serializer version " + version + "! Serializers up to 1.21.30 (version 40) are supported");
        }
        return ChunkSerializerV3.INSTANCE;
    }

    public static void serializeChunk(WriteBatch db, LevelDBChunk chunk, int version) {
        getChunkSerializer(version).serialize(db, chunk);
    }

    public static void deserializeChunk(DB db, ChunkBuilder chunkBuilder, int version) {
        getChunkSerializer(version).deserialize(db, chunkBuilder);
    }
}