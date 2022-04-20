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
        // Build up 4 SubChunks for the extended negative height
        BinaryStream stream = new BinaryStream();
        for (int i = 0; i < EXTENDED_NEGATIVE_SUB_CHUNKS; i++) {
            stream.putByte((byte) 8); // SubChunk version
            stream.putByte((byte) 0); // 0 layers
        }
        negativeSubChunks = stream.getBuffer();
    }

    public static void serialize(BaseChunk chunk, BiConsumer<BinaryStream, Integer> callback, DimensionData dimensionData) {
        byte[] blockEntities;
        if (chunk.getBlockEntities().isEmpty()) {
            blockEntities = new byte[0];
        } else {
            blockEntities = serializeEntities(chunk);
        }

        int subChunkCount = 0;
        ChunkSection[] sections = chunk.getSections();
        for (int i = sections.length - 1; i >= 0; i--) {
            if (!sections[i].isEmpty()) {
                subChunkCount = i + 1;
                break;
            }
        }

        int maxDimensionSections = dimensionData.getHeight() >> 4;
        subChunkCount = Math.min(maxDimensionSections, subChunkCount);

        // In 1.18 3D biome palettes were introduced. However, current world format
        // used internally doesn't support them, so we need to convert from legacy 2D
        byte[] biomePalettes = convert2DBiomesTo3D(chunk, maxDimensionSections);
        BinaryStream stream = ThreadCache.binaryStream.get().reset();

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
        stream.put(blockEntities);
        callback.accept(stream, writtenSections);
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

    private static byte[] convert2DBiomesTo3D(BaseFullChunk chunk, int sections) {
        PalettedBlockStorage palette = PalettedBlockStorage.createWithDefaultState(Biome.getBiomeIdOrCorrect(chunk.getBiomeId(0, 0)));
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int biomeId = Biome.getBiomeIdOrCorrect(chunk.getBiomeId(x, z));
                for (int y = 0; y < 16; y++) {
                    palette.setBlock(x, y, z, biomeId);
                }
            }
        }

        BinaryStream stream = ThreadCache.binaryStream.get().reset();
        palette.writeTo(stream);
        byte[] bytes = stream.getBuffer();
        stream.reset();

        for (int i = 0; i < sections; i++) {
            stream.put(bytes);
        }
        return stream.getBuffer();
    }
}
