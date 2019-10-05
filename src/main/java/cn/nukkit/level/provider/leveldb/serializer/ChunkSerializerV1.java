package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.provider.leveldb.LevelDBKey;
import cn.nukkit.utils.ChunkException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.iq80.leveldb.DB;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChunkSerializerV1 implements ChunkSerializer {

    public static final ChunkSerializer INSTANCE = new ChunkSerializerV1();

    @Override
    public void serialize(DB db, Chunk chunk) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deserialize(DB db, ChunkBuilder chunkBuilder) {
        this.deserializeExtraData(db, chunkBuilder);

        this.deserializeTerrain(db, chunkBuilder);
    }

    protected void deserializeTerrain(DB db, ChunkBuilder chunkBuilder) {
        byte[] terrain = db.get(LevelDBKey.LEGACY_TERRAIN.getKey(chunkBuilder.getX(), chunkBuilder.getZ()));
        if (terrain == null) {
            throw new ChunkException("No terrain found in chunk");
        }
    }

    protected void deserializeExtraData(DB db, ChunkBuilder chunkBuilder) {
        byte[] extraData = db.get(LevelDBKey.BLOCK_EXTRA_DATA.getKey(chunkBuilder.getX(), chunkBuilder.getZ()));
        if (extraData == null) {
            return;
        }
        ByteBuf buf = Unpooled.wrappedBuffer(extraData);

        int count = buf.readIntLE();
        for (int i = 0; i < count; i++) {
            int key = deserializeExtraDataKey(buf.readIntLE());
            short value = buf.readShortLE();

            chunkBuilder.extraData(key, value);
        }
    }

    protected int deserializeExtraDataKey(int key) {
        return ((key & ~0x7f) << 1) | (key & 0x7f); // max world height was only 128
    }
}
