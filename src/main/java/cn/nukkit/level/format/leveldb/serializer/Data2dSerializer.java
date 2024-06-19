package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.level.format.leveldb.LevelDBKey;
import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

public class Data2dSerializer {

    public static void serialize(WriteBatch db, LevelDBChunk chunk) {
        // Write height map and biomes.
        byte[] data2d = new byte[768];
        ByteBuf buffer = Unpooled.wrappedBuffer(data2d);
        buffer.writerIndex(0);
        byte[] heightMap = chunk.getHeightMapArray();
        byte[] biomes = chunk.getBiomeIdArray();
        for (int height : heightMap) {
            buffer.writeShortLE(height);
        }
        buffer.writeBytes(biomes);

        db.put(LevelDBKey.DATA_2D.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getLevel().getDimension()), data2d);
    }

    public static void deserialize(DB db, ChunkBuilder builder) {
        byte[] data2d = db.get(LevelDBKey.DATA_2D.getKey(builder.getX(), builder.getZ(), builder.getProvider().getLevel().getDimension()));
        int[] heightMap = new int[512];
        byte[] biomes = new byte[256];

        if (data2d != null) {
            ByteBuf buffer = Unpooled.wrappedBuffer(data2d);

            for (int i = 0; i < 256; i++) {
                heightMap[i] = buffer.readUnsignedShortLE();
            }
            buffer.readBytes(biomes);
        }

        builder.heightMap(heightMap);
        builder.biomes(biomes);
    }
}
