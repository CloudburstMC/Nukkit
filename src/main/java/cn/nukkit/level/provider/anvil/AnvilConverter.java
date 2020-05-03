package cn.nukkit.level.provider.anvil;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.BlockUpdate;
import cn.nukkit.level.Location;
import cn.nukkit.level.chunk.*;
import cn.nukkit.level.provider.LegacyBlockConverter;
import cn.nukkit.level.provider.anvil.palette.BiomePalette;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.NibbleArray;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.NumberTag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class AnvilConverter {

    public static void convertToNukkit(ChunkBuilder chunkBuilder, ByteBuf chunkBuf) throws IOException {

        CompoundTag tag;

        try (ByteBufInputStream stream = new ByteBufInputStream(chunkBuf);
             NBTInputStream nbtInputStream = NbtUtils.createReader(stream)) {
            tag = (CompoundTag) nbtInputStream.readTag();

            if (!tag.contains("Level") || !(tag.get("Level") instanceof CompoundTag)) {
                throw new IllegalArgumentException("No level tag found in chunk data");
            }
            tag = tag.getCompound("Level");
        }

        ChunkSection[] sections = new ChunkSection[Chunk.SECTION_COUNT];

        // Reusable array for performance
        final int[] blockState = new int[2];
        BlockRegistry blockRegistry = BlockRegistry.get();
        LegacyBlockConverter legacyBlockConverter = LegacyBlockConverter.get();

        // Chunk sections
        for (CompoundTag sectionTag : tag.getList("Sections", CompoundTag.class)) {
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
        chunkBuilder.sections(sections);

        byte[] biomes;
        if (tag.contains("BiomeColors")) {
            int[] biomeColors = tag.getIntArray("BiomeColors");
            biomes = new byte[256];
            if (biomeColors != null && biomeColors.length == 256) {
                BiomePalette palette = new BiomePalette(biomeColors);
                for (int i = 0; i < 256; i++) {
                    biomes[i] = (byte) (palette.get(i) >> 24);
                }
            }
        } else {
            biomes = tag.getByteArray("Biomes");
        }
        chunkBuilder.biomes(biomes);

        int[] anvilHeightMap = tag.getIntArray("HeightMap");
        int[] heightMap = new int[256];
        if (anvilHeightMap.length != 256) {
            Arrays.fill(heightMap, (byte) 255);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                heightMap[i] = (byte) anvilHeightMap[i];
            }
        }
        chunkBuilder.heightMap(heightMap);


        chunkBuilder.dataLoader(new DataLoader(tag.getList("Entities", CompoundTag.class)));
        chunkBuilder.dataLoader(new TileLoader(tag.getList("TileEntities", CompoundTag.class)));

        List<CompoundTag> updateEntries = tag.getList("TileTicks", CompoundTag.class);

        if (updateEntries != null && updateEntries.size() > 0) {
            for (CompoundTag entryTag : updateEntries) {
                Block block;

                try {
                    String name = entryTag.getString("i");


                    @SuppressWarnings("unchecked")
                    Class<? extends Block> clazz = (Class<? extends Block>) Class.forName("cn.nukkit.block." + name);

                    Constructor<? extends Block> constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    block = constructor.newInstance();
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                        IllegalAccessException | InvocationTargetException e) {
                    continue;
                }

                block.setPosition(Vector3i.from(
                        entryTag.getInt("x"),
                        entryTag.getInt("y"),
                        entryTag.getInt("z")
                ));

                chunkBuilder.blockUpdate(BlockUpdate.of(block, block.getPosition(), entryTag.getInt("t"),
                        entryTag.getInt("p"), false));
            }
        }

        if (tag.getBoolean("TerrainGenerated")) {
            chunkBuilder.state(IChunk.STATE_GENERATED);
        }
        if (tag.getBoolean("TerrainPopulated")) {
            chunkBuilder.state(IChunk.STATE_POPULATED);
        }
    }

    public static CompoundTag convertToAnvil(Chunk chunk) {
        throw new UnsupportedOperationException();
    }

    private static int getAnvilIndex(int x, int y, int z) {
        return (y << 8) + (z << 4) + x;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Location getLocation(CompoundTag tag, Chunk chunk) {
        List<NumberTag<?>> pos = (List) tag.getList("Pos", NumberTag.class);
        Vector3f position = Vector3f.from(pos.get(0).getValue().floatValue(), pos.get(1).getValue().floatValue(),
                pos.get(2).getValue().floatValue());

        List<NumberTag<?>> rotation = (List) tag.getList("Rotation", NumberTag.class);
        float yaw = rotation.get(0).getValue().floatValue();
        float pitch = rotation.get(1).getValue().floatValue();

        checkArgument(position.getFloorX() >> 4 == chunk.getX() && position.getFloorZ() >> 4 == chunk.getZ(),
                "Entity is not in chunk of origin");

        return Location.from(position, yaw, pitch, chunk.getLevel());
    }

    @RequiredArgsConstructor
    private static class DataLoader implements ChunkDataLoader {
        private final List<CompoundTag> entityTags;

        @Override
        public boolean load(Chunk chunk) {
            EntityRegistry registry = EntityRegistry.get();
            boolean dirty = false;
            for (CompoundTag entityTag : entityTags) {
                if (!entityTag.contains("id")) {
                    dirty = true;
                    continue;
                }
                Location location = getLocation(entityTag, chunk);
                Vector3f position = location.getPosition();
                if ((position.getFloorX() >> 4) != chunk.getX() || ((position.getFloorZ() >> 4) != chunk.getZ())) {
                    dirty = true;
                    continue;
                }
                Identifier identifier = registry.getIdentifier(entityTag.getString("id"));
                if (identifier == null) {
                    dirty = true;
                    continue;
                }
                EntityType<?> type = registry.getEntityType(identifier);
                Entity entity = registry.newEntity(type, location);
                entity.loadAdditionalData(entityTag);
            }
            return dirty;
        }
    }

    @RequiredArgsConstructor
    private static class TileLoader implements ChunkDataLoader {
        private static final BlockEntityRegistry REGISTRY = BlockEntityRegistry.get();
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
                    Vector3i position = Vector3i.from(tag.getInt("x"), tag.getInt("y"), tag.getInt("y"));
                    if ((position.getX() >> 4) != chunk.getX() || ((position.getZ() >> 4) != chunk.getZ())) {
                        dirty = true;
                        continue;
                    }
                    BlockEntityType<?> type = REGISTRY.getBlockEntityType(tag.getString("id"));

                    BlockEntity blockEntity = REGISTRY.newEntity(type, chunk, position);
                    if (blockEntity == null) {
                        dirty = true;
                    }
                }
            }
            return dirty;
        }
    }
}
