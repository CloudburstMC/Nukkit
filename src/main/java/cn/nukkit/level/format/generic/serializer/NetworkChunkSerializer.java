package cn.nukkit.level.format.generic.serializer;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ThreadCache;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;
import java.util.function.BiConsumer;

public class NetworkChunkSerializer {

    private static final int EXTENDED_NEGATIVE_SUB_CHUNKS = 4;

    private static final byte[] negativeSubChunks;

    static {
        BinaryStream stream = new BinaryStream();
        // Build up 4 SubChunks for the extended negative height
        for (int i = 0; i < EXTENDED_NEGATIVE_SUB_CHUNKS; i++) {
            stream.putByte((byte) 9); // SubChunk version
            stream.putByte((byte) 0); // 0 layers
            stream.putByte((byte) ((-64 + (i * 16)) / 16)); // section y
        }
        negativeSubChunks = stream.getBuffer();
    }

    public static void serialize(BaseChunk chunk, BiConsumer<BinaryStream, NetworkChunkData> callback, DimensionData dimensionData) {
        int subChunkCount = 0;
        ChunkSection[] sections = chunk.getSections();
        for (int i = sections.length - 1; i >= 0; i--) {
            if (!sections[i].isEmpty()) {
                subChunkCount = i + 1;
                break;
            }
        }

        BinaryStream stream = ThreadCache.binaryStream.get().reset();
        NetworkChunkData chunkData = new NetworkChunkData(subChunkCount, dimensionData);
        serialize1_18_30(stream, chunk, sections, chunkData);

        byte[] blockEntities;
        if (chunk.getBlockEntities().isEmpty()) {
            blockEntities = new byte[0];
        } else {
            blockEntities = serializeEntities(chunk);
        }
        stream.put(blockEntities);

        callback.accept(stream, chunkData);
    }

    private static void serialize1_18_30(BinaryStream stream, BaseFullChunk chunk, ChunkSection[] sections, NetworkChunkData chunkData) {
        DimensionData dimensionData = chunkData.getDimensionData();
        int maxDimensionSections = dimensionData.getHeight() >> 4;
        int subChunkCount = Math.min(maxDimensionSections, chunkData.getChunkSections());

        byte[] biomePalettes = serialize3DBiomes(chunk, maxDimensionSections);
        stream.reset();

        // Overworld has negative coordinates, but we currently do not support them
        int writtenSections = subChunkCount;
        if (dimensionData.getDimensionId() == Level.DIMENSION_OVERWORLD && subChunkCount < maxDimensionSections) {
            stream.put(negativeSubChunks);
            writtenSections += EXTENDED_NEGATIVE_SUB_CHUNKS;
        }

        for (int i = 0; i < subChunkCount; i++) {
            sections[i].writeTo(stream);
        }

        stream.put(biomePalettes);
        stream.putByte((byte) 0); // Border blocks

        chunkData.setChunkSections(writtenSections);
    }

    private static byte[] serializeEntities(BaseChunk chunk) {
        List<CompoundTag> tagList = new ObjectArrayList<>();
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (blockEntity instanceof BlockEntitySpawnable) {
                tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
            }
        }

        try {
            return NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] serialize3DBiomes(BaseFullChunk chunk, int sections) {
        if (!chunk.has3dBiomes()) { // Convert 2D biomes to 3D
            PalettedBlockStorage palette = PalettedBlockStorage.createWithDefaultState(Biome.getBiomeIdOrCorrect(chunk.getBiomeId(0, 0)));
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int biomeId = chunk.getBiomeId(x, z);
                    for (int y = 0; y < 16; y++) {
                        palette.setBlock(x, y, z, biomeId);
                    }
                }
            }

            BinaryStream stream = ThreadCache.binaryStream.get().reset();
            palette.writeTo(stream, Biome::getBiomeIdOrCorrect);
            byte[] bytes = stream.getBuffer();
            stream.reset();

            for (int i = 0; i < sections; i++) {
                stream.put(bytes);
            }
            return stream.getBuffer();
        }

        BinaryStream stream = ThreadCache.binaryStream.get().reset();
        for (int i = 0; i < sections; i++) {
            PalettedBlockStorage storage = chunk.getBiomeStorage(i);
            storage.writeTo(stream, Biome::getBiomeIdOrCorrect);

        }
        return stream.getBuffer();
    }
}
