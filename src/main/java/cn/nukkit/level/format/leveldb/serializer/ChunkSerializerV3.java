package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.format.leveldb.BlockStateMapping;
import cn.nukkit.level.format.leveldb.LevelDBKey;
import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunkSection;
import cn.nukkit.level.format.leveldb.structure.StateBlockStorage;
import cn.nukkit.utils.ChunkException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import static cn.nukkit.level.format.leveldb.LevelDBConstants.*;

public class ChunkSerializerV3 implements ChunkSerializer {
    public static final ChunkSerializer INSTANCE = new ChunkSerializerV3();

    @Override
    public void serialize(WriteBatch db, Chunk chunk) {
        // Write chunk sections
        DimensionData dimensionData = chunk.getProvider().getLevel().getDimensionData();
        int lowestSection = dimensionData.getMinHeight() >> 4;
        int highestSection = dimensionData.getMaxHeight() >> 4;

        for (int ySection = lowestSection; ySection <= highestSection; ySection++) {
            LevelDBChunkSection section = (LevelDBChunkSection) chunk.getSection(ySection);
            if (section == null) {
                continue;
            }

            ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
            try {
                buffer.writeByte(LATEST_SUBCHUNK_VERSION);
                ChunkSectionSerializers.serialize(buffer, section.getStorages(), ySection, LATEST_SUBCHUNK_VERSION);

                byte[] payload = new byte[buffer.readableBytes()];
                buffer.readBytes(payload);
                db.put(LevelDBKey.SUB_CHUNK_PREFIX.getKey(chunk.getX(), chunk.getZ(), ySection, chunk.getProvider().getLevel().getDimension()), payload);
            } finally {
                buffer.release();
            }

            buffer = ByteBufAllocator.DEFAULT.ioBuffer();
            try {
                byte[] blockLight = section.getLightArray();
                if (blockLight != EmptyChunkSection.EMPTY_LIGHT_ARR) {
                    db.put(LevelDBKey.NUKKIT_BLOCK_LIGHT.getKey(chunk.getX(), chunk.getZ(), ySection, chunk.getProvider().getLevel().getDimension()), blockLight);
                }
            } finally {
                buffer.release();
            }
        }
    }

    @Override
    public void deserialize(DB db, ChunkBuilder chunkBuilder) {
        int chunkX = chunkBuilder.getX();
        int chunkZ = chunkBuilder.getZ();

        Int2ShortMap extraDataMap = null;

        byte[] extraData = db.get(LevelDBKey.BLOCK_EXTRA_DATA.getKey(chunkX, chunkZ, chunkBuilder.getProvider().getLevel().getDimension()));
        if (extraData != null) {
            extraDataMap = new Int2ShortOpenHashMap();
            ByteBuf extraDataBuf = Unpooled.wrappedBuffer(extraData);

            int count = extraDataBuf.readIntLE();
            for (int i = 0; i < count; i++) {
                int key = extraDataBuf.readIntLE();
                short value = extraDataBuf.readShortLE();
                extraDataMap.put(key, value);
            }
        }

        DimensionData dimensionData = chunkBuilder.getProvider().getLevel().getDimensionData();
        int offset = dimensionData.getSectionOffset();
        int lowestSection = dimensionData.getMinHeight() >> 4;
        int highestSection = dimensionData.getMaxHeight() >> 4;

        LevelDBChunkSection[] sections = new LevelDBChunkSection[dimensionData.getHeight() >> 4];

        for (int ySection = lowestSection; ySection <= highestSection; ySection++) {
            byte[] sectionData = db.get(LevelDBKey.SUB_CHUNK_PREFIX.getKey(chunkX, chunkZ, ySection, chunkBuilder.getProvider().getLevel().getDimension()));
            if (sectionData == null) {
                continue;
            }
            ByteBuf buf = Unpooled.wrappedBuffer(sectionData);
            if (!buf.isReadable()) {
                throw new ChunkException("Empty sub-chunk " + ySection);
            }

            int subChunkVersion = buf.readUnsignedByte();
            StateBlockStorage[] blockStorage = ChunkSectionSerializers.deserialize(buf, chunkBuilder, subChunkVersion);

            if (blockStorage[1] == null) {
                blockStorage[1] = new StateBlockStorage();
                if (extraDataMap != null) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = ySection << 4, lim = y + 16; y < lim; y++) {
                                int key = ChunkBuilder.blockKey(x, y, z);
                                if (extraDataMap.containsKey(key)) {
                                    short value = extraDataMap.get(ChunkBuilder.blockKey(x, y, z));
                                    int blockId = value & 0xff;
                                    int blockData = (value >> 8) & 0xf;
                                    blockStorage[1].setBlockStateUnsafe(ChunkBuilder.getSectionIndex(x, y, z), BlockStateMapping.get().getState(blockId, blockData));
                                }
                            }
                        }
                    }
                }
            }

            byte[] blockLight = db.get(LevelDBKey.NUKKIT_BLOCK_LIGHT.getKey(chunkX, chunkZ, ySection, chunkBuilder.getProvider().getLevel().getDimension()));

            sections[ySection + offset] = new LevelDBChunkSection(ySection, blockStorage, blockLight, null, null, false, false);
        }

        chunkBuilder.sections(sections);
    }
}
