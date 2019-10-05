package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.BlockStorage;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.chunk.ChunkSection;
import cn.nukkit.level.provider.leveldb.LevelDBKey;
import cn.nukkit.utils.ChunkException;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.hash.TIntShortHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.DB;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChunkSerializerV3 extends ChunkSerializerV1 {

    public static ChunkSerializer INSTANCE = new ChunkSerializerV3();

    @Override
    public void serialize(DB db, Chunk chunk) {
        for (int ySection = 0; ySection < Chunk.SECTION_COUNT; ySection++) {
            ChunkSection section = chunk.getSection(ySection);
            if (section == null) {
                continue;
            }

            ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
            try {
                buffer.writeByte(ChunkSection.CHUNK_SECTION_VERSION);
                ChunkSectionSerializers.serialize(buffer, section.getBlockStorageArray(), ChunkSection.CHUNK_SECTION_VERSION);

                byte[] payload = new byte[buffer.writerIndex()];
                buffer.readBytes(payload);

                db.put(LevelDBKey.SUBCHUNK_PREFIX.getKey(chunk.getX(), chunk.getZ(), ySection), payload);
            } finally {
                buffer.release();
            }
        }
    }

    @Override
    public void deserialize(DB db, ChunkBuilder chunkBuilder) {
        int chunkX = chunkBuilder.getX();
        int chunkZ = chunkBuilder.getZ();

        TIntShortMap extraDataMap = null;

        byte[] extraData = db.get(LevelDBKey.BLOCK_EXTRA_DATA.getKey(chunkX, chunkZ));
        if (extraData != null) {
            extraDataMap = new TIntShortHashMap(16, 0.5f, 0, (short) 0);
            ByteBuf extraDataBuf = Unpooled.wrappedBuffer(extraData);

            int count = extraDataBuf.readIntLE();
            for (int i = 0; i < count; i++) {
                int key = extraDataBuf.readIntLE();
                short value = extraDataBuf.readShortLE();

                extraDataMap.put(key, value);
            }
        }

        ChunkSection[] sections = new ChunkSection[Chunk.SECTION_COUNT];

        for (int ySection = 0; ySection < Chunk.SECTION_COUNT; ySection++) {
            byte[] sectionData = db.get(LevelDBKey.SUBCHUNK_PREFIX.getKey(chunkX, chunkZ, ySection));
            if (sectionData == null) {
                continue;
            }
            ByteBuf buf = Unpooled.wrappedBuffer(sectionData);
            if (!buf.isReadable()) {
                throw new ChunkException("Empty sub-chunk " + ySection);
            }

            int subChunkVersion = buf.readUnsignedByte();
            if (subChunkVersion < ChunkSection.CHUNK_SECTION_VERSION) {
                chunkBuilder.dirty();
            }
            BlockStorage[] blockStorage = ChunkSectionSerializers.deserialize(buf, chunkBuilder, subChunkVersion);

            if (blockStorage[1] == null) {
                blockStorage[1] = new BlockStorage();
                if (extraDataMap != null) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = ySection * 16, lim = y + 16; y < lim; y++) {
                                int key = Level.chunkBlockKey(x, y, z);
                                if (extraDataMap.containsKey(key)) {
                                    short value = extraDataMap.get(Level.chunkBlockKey(x, y, z));
                                    int blockId = value & 0xff;
                                    int blockData = (value >> 8) & 0xf;
                                    blockStorage[1].setFullBlock(ChunkSection.blockIndex(x, y, z), (blockId << 4) | blockData);
                                }
                            }
                        }
                    }
                }
            }
            sections[ySection] = new ChunkSection(blockStorage);
        }

        chunkBuilder.sections(sections);

        // Height map & Biomes

        byte[] data2d = db.get(LevelDBKey.DATA_2D.getKey(chunkX, chunkZ));
        int[] heightMap = new int[512];
        byte[] biomes = new byte[256];

        if (data2d != null) {
            ByteBuf buffer = Unpooled.wrappedBuffer(data2d);

            for (int i = 0; i < 256; i++) {
                heightMap[i] = buffer.readUnsignedShortLE();
            }
            buffer.readBytes(biomes);
        }

        chunkBuilder.heightMap(heightMap);
        chunkBuilder.biomes(biomes);

        chunkBuilder.generated();
        chunkBuilder.populated();
    }

    @Override
    protected int deserializeExtraDataKey(int key) {
        return key;
    }
}
