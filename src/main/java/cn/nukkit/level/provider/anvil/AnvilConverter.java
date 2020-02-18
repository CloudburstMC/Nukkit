package cn.nukkit.level.provider.anvil;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLog;
import cn.nukkit.block.BlockLog2;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.BlockUpdate;
import cn.nukkit.level.chunk.*;
import cn.nukkit.level.provider.LegacyBlockConverter;
import cn.nukkit.level.provider.anvil.palette.BiomePalette;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.NibbleArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

public class AnvilConverter {

    public static void convertToNukkit(ChunkBuilder chunkBuilder, ByteBuf chunkBuf) throws IOException {

        CompoundTag nbt;

        try (ByteBufInputStream stream = new ByteBufInputStream(chunkBuf)) {
            nbt = NBTIO.read(stream, ByteOrder.BIG_ENDIAN);

            if (!nbt.contains("Level") || !(nbt.get("Level") instanceof CompoundTag)) {
                throw new IllegalArgumentException("No level tag found in chunk data");
            }
            nbt = nbt.getCompound("Level");
        }

        ChunkSection[] sections = new ChunkSection[Chunk.SECTION_COUNT];

        // Reusable array for performance
        final int[] blockState = new int[2];
        BlockRegistry blockRegistry = BlockRegistry.get();
        LegacyBlockConverter legacyBlockConverter = LegacyBlockConverter.get();

        // Chunk sections
        for (Tag tag : nbt.getList("Sections").getAll()) {
            if (tag instanceof CompoundTag) {
                CompoundTag sectionTag = (CompoundTag) tag;
                int y = sectionTag.getByte("Y");
                if (y >= 16) {
                    continue;
                }

                byte[] blocks = sectionTag.getByteArray("Blocks");
                NibbleArray data = new NibbleArray(sectionTag.getByteArray("Data"));
                byte[] blockLight = sectionTag.getByteArray("BlockLight");
                byte[] skyLight = sectionTag.getByteArray("SkyLight");

                BlockStorage blockStorage = new BlockStorage();
                // Convert YZX to XZY
                for (int blockX = 0; blockX < 16; blockX++) {
                    for (int blockZ = 0; blockZ < 16; blockZ++) {
                        for (int blockY = 0; blockY < 16; blockY++) {
                            int anvilIndex = getAnvilIndex(blockX, blockY, blockZ);
                            int nukkitIndex = ChunkSection.blockIndex(blockX, blockY, blockZ);
                            blockState[0] = blocks[anvilIndex] & 0xff;
                            blockState[1] = data.get(anvilIndex);
                            legacyBlockConverter.convertBlockState(blockState);
                            blockStorage.setBlock(nukkitIndex, blockRegistry.getBlock(blockState[0], blockState[1]));
                        }
                    }
                }

                sections[y] = new ChunkSection(new BlockStorage[]{blockStorage, new BlockStorage()}, blockLight, skyLight);
            }
        }
        chunkBuilder.sections(sections);

        // Extra data
        Tag extra = nbt.get("ExtraData");
        if (extra instanceof ByteArrayTag) {
            ByteBuf buffer = Unpooled.wrappedBuffer(((ByteArrayTag) extra).data);
            for (int i = 0; i < buffer.readInt(); i++) {
                int index = buffer.readInt();
                short data = buffer.readShort();
                chunkBuilder.extraData(index, data);

            }
        }

        byte[] biomes;
        if (nbt.contains("BiomeColors")) {
            int[] biomeColors = nbt.getIntArray("BiomeColors");
            biomes = new byte[256];
            if (biomeColors != null && biomeColors.length == 256) {
                BiomePalette palette = new BiomePalette(biomeColors);
                for (int i = 0; i < 256; i++) {
                    biomes[i] = (byte) (palette.get(i) >> 24);
                }
            }
        } else {
            biomes = nbt.getByteArray("Biomes");
        }
        chunkBuilder.biomes(biomes);

        int[] anvilHeightMap = nbt.getIntArray("HeightMap");
        int[] heightMap = new int[256];
        if (anvilHeightMap.length != 256) {
            Arrays.fill(heightMap, (byte) 255);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                heightMap[i] = (byte) anvilHeightMap[i];
            }
        }
        chunkBuilder.heightMap(heightMap);


        chunkBuilder.dataLoader(new DataLoader(nbt.getList("Entities", CompoundTag.class).getAll()));
        chunkBuilder.dataLoader(new TileLoader(nbt.getList("TileEntities", CompoundTag.class).getAll()));

        ListTag<CompoundTag> updateEntries = nbt.getList("TileTicks", CompoundTag.class);

        if (updateEntries != null && updateEntries.size() > 0) {
            for (CompoundTag entryTag : updateEntries.getAll()) {
                Block block = null;

                try {
                    Tag tag = entryTag.get("i");
                    if (tag instanceof StringTag) {
                        String name = ((StringTag) tag).data;

                        @SuppressWarnings("unchecked")
                        Class<? extends Block> clazz = (Class<? extends Block>) Class.forName("cn.nukkit.block." + name);

                        Constructor constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        block = (Block) constructor.newInstance();
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                        IllegalAccessException | InvocationTargetException e) {
                    continue;
                }

                if (block == null) {
                    continue;
                }

                block.x = entryTag.getInt("x");
                block.y = entryTag.getInt("y");
                block.z = entryTag.getInt("z");

                chunkBuilder.blockUpdate(BlockUpdate.of(block, block, entryTag.getInt("t"), entryTag.getInt("p"),
                        false));
            }
        }

        if (nbt.getBoolean("TerrainGenerated")) {
            chunkBuilder.generated();
        }
        if (nbt.getBoolean("TerrainPopulated")) {
            chunkBuilder.populated();
        }
    }

    public static CompoundTag convertToAnvil(Chunk chunk) {
        return null;
    }

    private static int getAnvilIndex(int x, int y, int z) {
        return (y << 8) + (z << 4) + x;
    }

    @RequiredArgsConstructor
    private static class DataLoader implements ChunkDataLoader {
        private final List<CompoundTag> entityTags;

        @Override
        public boolean load(Chunk chunk) {
            boolean dirty = false;
            for (CompoundTag entityTag : entityTags) {
                if (!entityTag.contains("id")) {
                    dirty = true;
                    continue;
                }
                ListTag pos = entityTag.getList("Pos");
                if ((((NumberTag) pos.get(0)).getData().intValue() >> 4) != chunk.getX() ||
                        ((((NumberTag) pos.get(2)).getData().intValue() >> 4) != chunk.getZ())) {
                    dirty = true;
                    continue;
                }
                EntityRegistry registry = EntityRegistry.get();
                Identifier identifier = registry.getIdentifier(entityTag.getString("id"));
                if (identifier == null) {
                    dirty = true;
                    continue;
                }
                EntityType<?> type = registry.getEntityType(identifier);
                Entity entity = registry.newEntity(type, chunk, entityTag);
                if (entity != null) {
                    dirty = true;
                }
            }
            return dirty;
        }
    }

    @RequiredArgsConstructor
    private static class TileLoader implements ChunkDataLoader {
        private final List<CompoundTag> tileTags;

        @Override
        public boolean load(Chunk chunk) {
            boolean dirty = false;
            for (CompoundTag tag : tileTags) {
                if (tag != null) {
                    if (!tag.contains("id")) {
                        dirty = true;
                        continue;
                    }
                    if ((tag.getInt("x") >> 4) != chunk.getX() || ((tag.getInt("z") >> 4) != chunk.getZ())) {
                        dirty = true;
                        continue;
                    }
                    BlockEntity blockEntity = BlockEntity.createBlockEntity(tag.getString("id"), chunk, tag);
                    if (blockEntity == null) {
                        dirty = true;
                    }
                }
            }
            return dirty;
        }
    }
}
