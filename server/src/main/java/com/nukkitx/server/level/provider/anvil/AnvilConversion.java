package com.nukkitx.server.level.provider.anvil;

import com.google.common.base.Preconditions;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.*;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.level.chunk.ChunkSection;
import com.nukkitx.server.level.chunk.SectionedChunk;
import com.nukkitx.server.level.util.NibbleArray;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@UtilityClass
@SuppressWarnings("unchecked")
public class AnvilConversion {
    private static final int SECTION_SIZE = 4096;

    public static Chunk convertChunkToNukkit(Map<String, Tag<?>> levelData, Level level) {
        TIntObjectHashMap<Map<String, Tag<?>>> sectionMap = generateSectionsMap(levelData);

        // Translate block data
        int cx = ((IntTag) levelData.get("xPos")).getValue();
        int cz = ((IntTag) levelData.get("zPos")).getValue();
        SectionedChunk chunk = new SectionedChunk(cx, cz, level);
        ListTag<CompoundTag> sectionsList = (ListTag<CompoundTag>) levelData.get("Sections");
        for (CompoundTag tag : sectionsList.getValue()) {
            int ySec = ((ByteTag) tag.getValue().get("Y")).getValue();

            byte[] blockIds = ((ByteArrayTag) tag.get("Blocks")).getValue();
            NibbleArray data = new NibbleArray(((ByteArrayTag) tag.get("Data")).getValue());
            NibbleArray skyLight = new NibbleArray(((ByteArrayTag) tag.get("SkyLight")).getValue());
            NibbleArray blockLight = new NibbleArray(((ByteArrayTag) tag.get("BlockLight")).getValue());

            // Block IDs and data require remapping.
            ChunkSection section = chunk.getOrCreateSection(ySec);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int pos = anvilBlockPosition(x, y, z);
                        section.setBlockId(x, y, z, 0, NukkitLevel.getPaletteManager().fromLegacy(blockIds[pos], data.get(pos)));
                        section.setSkyLight(x, y, z, skyLight.get(pos));
                        section.setBlockLight(x, y, z, blockLight.get(pos));
                    }
                }
            }
        }

        chunk.recalculateHeightMap();
        return chunk;
    }

    public static Map<String, Tag<?>> convertChunkToAnvil(Chunk chunk) {
        Preconditions.checkArgument(chunk instanceof SectionedChunk, "Chunk must be of SectionedChunk");
        SectionedChunk sectionedChunk = (SectionedChunk) chunk;

        CompoundTagBuilder builder = CompoundTagBuilder.builder();
        builder.intTag("xPos", sectionedChunk.getX());
        builder.intTag("zPos", sectionedChunk.getZ());

        List<CompoundTag> sections = new ArrayList<>();
        for (byte ySec = 0; ySec < 16; ySec++) {
            Optional<ChunkSection> sectionOptional = sectionedChunk.getSection(ySec);
            if (!sectionOptional.isPresent()) {
                continue;
            }
            ChunkSection section = sectionOptional.get();
            CompoundTagBuilder sectionTag = CompoundTagBuilder.builder();

            sectionTag.byteTag("Y", ySec);

            byte[] blockIds = new byte[SECTION_SIZE];
            NibbleArray data = new NibbleArray(SECTION_SIZE / 2);
            NibbleArray skyLight = new NibbleArray(SECTION_SIZE / 2);
            NibbleArray blockLight = new NibbleArray(SECTION_SIZE / 2);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        int pos = anvilBlockPosition(x, y, z);
                        blockIds[pos] = (byte) section.getBlockId(x, y, z, 0);
                        //data.set(pos, section.getBlockData(x, y, z)); TODO
                        skyLight.set(pos, section.getSkyLight(x, y, z));
                        blockLight.set(pos, section.getBlockLight(x, y, z));
                    }
                }
            }
            sectionTag.byteArrayTag("BlockIds", blockIds);
            sectionTag.byteArrayTag("Data", data.getData());
            sectionTag.byteArrayTag("SkyLight", skyLight.getData());
            sectionTag.byteArrayTag("BlockLight", blockLight.getData());

            sections.add(sectionTag.buildRootTag());
        }

        return builder.listTag("Sections", CompoundTag.class, sections).buildRootTag().getValue();
    }

    private static TIntObjectHashMap<Map<String, Tag<?>>> generateSectionsMap(Map<String, Tag<?>> levelData) {
        ListTag<CompoundTag> sectionsList = (ListTag<CompoundTag>) levelData.get("Sections");
        TIntObjectHashMap<Map<String, Tag<?>>> map = new TIntObjectHashMap<>();
        for (CompoundTag tag : sectionsList.getValue()) {
            int y = ((ByteTag) tag.getValue().get("Y")).getValue();
            map.put(y, tag.getValue());
        }
        return map;
    }

    private static int anvilBlockPosition(int x, int y, int z) {
        return (y << 8) + (z << 4) + x;
    }
}
