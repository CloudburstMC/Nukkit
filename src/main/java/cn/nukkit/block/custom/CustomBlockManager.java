package cn.nukkit.block.custom;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.custom.comparator.HashedPaletteComparator;
import cn.nukkit.block.custom.container.BlockContainer;
import cn.nukkit.block.custom.container.BlockContainerFactory;
import cn.nukkit.block.custom.container.BlockStorageContainer;
import cn.nukkit.block.custom.properties.BlockProperties;
import cn.nukkit.block.custom.properties.BlockProperty;
import cn.nukkit.block.custom.properties.EnumBlockProperty;
import cn.nukkit.block.custom.properties.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.level.BlockPalette;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.leveldb.BlockStateMapping;
import cn.nukkit.level.format.leveldb.LevelDBConstants;
import cn.nukkit.level.format.leveldb.NukkitLegacyMapper;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;
import org.cloudburstmc.nbt.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Handles custom block registry.
 * <p>
 * See <a href="https://github.com/PetteriM1/CustomBlockExample">CustomBlockExample</a> for example usage
 */
@Log4j2
public class CustomBlockManager {

    /**
     * Lowest allowed Nukkit save id for custom blocks
     */
    public static final int LOWEST_CUSTOM_BLOCK_ID = 5000;

    private static CustomBlockManager INSTANCE;

    /**
     * Internal: initialize CustomBlockManager
     */
    public static CustomBlockManager init(Server server) {
        if (INSTANCE == null) {
            return INSTANCE = new CustomBlockManager(server);
        }
        throw new IllegalStateException("CustomBlockManager was already initialized!");
    }

    /**
     * Get CustomBlockManager instance
     */
    public static CustomBlockManager get() {
        return INSTANCE;
    }

    private final Server server;

    private final Int2ObjectMap<CustomBlockDefinition> blockDefinitions = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<CustomBlockState> legacy2CustomState = new Int2ObjectOpenHashMap<>();

    private volatile boolean closed;

    private CustomBlockManager(Server server) {
        this.server = server;
    }

    public void registerCustomBlock(String identifier, int nukkitId, Supplier<BlockContainer> factory) {
        this.registerCustomBlock(identifier, nukkitId, NbtMap.EMPTY, factory);
    }

    public void registerCustomBlock(String identifier, int nukkitId, NbtMap networkData, Supplier<BlockContainer> factory) {
        this.registerCustomBlock(identifier, nukkitId, null, networkData, meta -> factory.get());
    }

    public void registerCustomBlock(String identifier, int nukkitId, BlockProperties properties, NbtMap networkData, BlockContainerFactory factory) {
        if (this.closed) {
            throw new IllegalStateException("Block registry was already closed");
        }

        if (nukkitId < LOWEST_CUSTOM_BLOCK_ID) {
            throw new IllegalArgumentException("Custom block ID can not be lower than " + LOWEST_CUSTOM_BLOCK_ID);
        }

        BlockContainer blockSample = factory.create(0);
        if (blockSample instanceof BlockStorageContainer && properties == null) {
            properties = ((BlockStorageContainer) blockSample).getBlockProperties();
            log.warn("Custom block {} was registered using wrong method! Trying to use sample properties!", identifier);
        }

        if (properties != null && networkData.isEmpty()) {
            throw new IllegalArgumentException("Block network data can not be empty for block with more permutations: " + identifier);
        }

        CustomBlockState defaultState = this.createBlockState(identifier, nukkitId << Block.DATA_BITS, properties, factory);
        this.legacy2CustomState.put(defaultState.getLegacyId(), defaultState);

        // TODO: unsure if this is per state or not
        CustomBlockDefinition definition = new CustomBlockDefinition(identifier, networkData, defaultState.getLegacyId(), blockSample.getClass());
        this.blockDefinitions.put(defaultState.getLegacyId(), definition);

        int itemId = 255 - nukkitId;
        RuntimeItems.getMapping().registerItem(identifier, nukkitId, itemId, 0);

        if (properties != null) {
            for (int meta = 1; meta < properties.getBitSize(); meta++) {
                CustomBlockState state;
                try {
                    state = this.createBlockState(identifier, (nukkitId << Block.DATA_BITS) | meta, properties, factory);
                } catch (InvalidBlockPropertyMetaException e) {
                    break; // Nukkit has more states than our block
                }
                this.legacy2CustomState.put(state.getLegacyId(), state);
            }
        }
    }

    private CustomBlockState createBlockState(String identifier, int legacyId, BlockProperties properties, BlockContainerFactory factory) {
        int meta = legacyId & Block.DATA_MASK;

        NbtMapBuilder statesBuilder = NbtMap.builder();
        if (properties != null) {
            for (String propertyName : properties.getNames()) {
                BlockProperty<?> property = properties.getBlockProperty(propertyName);
                if (property instanceof EnumBlockProperty) {
                    statesBuilder.put(property.getPersistenceName(), properties.getPersistenceValue(meta, propertyName));
                } else {
                    statesBuilder.put(property.getPersistenceName(), properties.getValue(meta, propertyName));
                }
            }
        }

        NbtMap state = NbtMap.builder()
                .putString("name", identifier)
                .putCompound("states", statesBuilder.build())
                .putInt("version", LevelDBConstants.STATE_VERSION)
                .build();
        return new CustomBlockState(identifier, legacyId, state, factory);
    }

    /**
     * Internal: close registry to prepare for data generation
     */
    public boolean closeRegistry() throws IOException {
        if (this.closed) {
            throw new IllegalStateException("Block registry was already closed");
        }

        this.closed = true;

        if (this.legacy2CustomState.isEmpty()) {
            return false;
        }

        this.createBin();

        long startTime = System.currentTimeMillis();

        BlockPalette storagePalette = GlobalBlockPalette.getLeveldbBlockPalette();
        BlockPalette palette = GlobalBlockPalette.getCurrentBlockPalette();

        if (palette.getProtocol() == storagePalette.getProtocol()) {
            this.recreateBlockPalette(palette, new ObjectArrayList<>(NukkitLegacyMapper.loadBlockPalette()));
        } else {
            Path path = this.getVanillaPalettePath(palette.getProtocol());
            if (!Files.exists(path)) {
                log.warn("bin/block_palette_" + palette.getProtocol() + ".nbt not found! Version won't support custom blocks");
                return false;
            }
            this.recreateBlockPalette(palette);
        }

        log.debug("Custom block registry closed in {} ms", (System.currentTimeMillis() - startTime));
        return true;
    }

    private void recreateBlockPalette(BlockPalette palette) throws IOException {
        List<NbtMap> vanillaPalette = new ObjectArrayList<>(this.loadVanillaPalette(palette.getProtocol()));
        this.recreateBlockPalette(palette, vanillaPalette);
    }

    private void recreateBlockPalette(BlockPalette palette, List<NbtMap> vanillaPalette) {
        List<NbtMap> vanillaPaletteList = new ObjectArrayList<>();
        for (NbtMap state : vanillaPalette) {
            if (state.containsKey("network_id") || state.containsKey("name_hash") || state.containsKey("block_id")) {
                NbtMapBuilder builder = NbtMapBuilder.from(state);
                builder.remove("network_id");
                builder.remove("name_hash");
                builder.remove("block_id");
                state = builder.build();
            }
            vanillaPaletteList.add(state);
        }

        Object2ObjectMap<NbtMap, IntSet> state2Legacy = new Object2ObjectLinkedOpenHashMap<>();

        int paletteVersion = vanillaPaletteList.get(0).getInt("version");

        for (Int2IntMap.Entry entry : palette.getLegacyToRuntimeIdMap().int2IntEntrySet()) {
            int runtimeId = entry.getIntValue();
            NbtMap state = vanillaPaletteList.get(runtimeId);
            if (state == null) {
                log.info("Unknown runtime ID {}! protocol={}", runtimeId, palette.getProtocol());
                continue;
            }
            IntSet legacyIds = state2Legacy.computeIfAbsent(state, s -> new IntOpenHashSet());
            legacyIds.add(entry.getIntKey());
        }

        for (CustomBlockState definition : this.legacy2CustomState.values()) {
            NbtMap state = definition.getBlockState();
            if (state.getInt("version") != paletteVersion) {
                state = state.toBuilder().putInt("version", paletteVersion).build();
            }
            state2Legacy.computeIfAbsent(state, s -> new IntOpenHashSet()).add(legacyToFullId(definition.getLegacyId()));
            vanillaPaletteList.add(state);
        }

        vanillaPaletteList.sort(HashedPaletteComparator.INSTANCE);

        palette.clearStates();
        boolean levelDb = palette.getProtocol() == GlobalBlockPalette.getLeveldbBlockPalette().getProtocol();
        if (levelDb) {
            BlockStateMapping.get().clearMapping();
        }

        for (int runtimeId = 0; runtimeId < vanillaPaletteList.size(); runtimeId++) {
            NbtMap state = vanillaPaletteList.get(runtimeId);
            if (levelDb) {
                BlockStateMapping.get().registerState(runtimeId, state);
            }

            IntSet legacyIds = state2Legacy.get(state);
            if (legacyIds == null) {
                continue;
            }

            CompoundTag nukkitState = convertNbtMap(state);
            for (Integer fullId : legacyIds) {
                palette.registerState(fullId >> Block.DATA_BITS, fullId & Block.DATA_MASK, runtimeId, nukkitState);
            }
        }
    }

    private List<NbtMap> loadVanillaPalette(int version) throws FileNotFoundException {
        Path path = this.getVanillaPalettePath(version);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Missing vanilla palette for version " + version);
        }

        try (InputStream stream = Files.newInputStream(path)) {
            return ((NbtMap) NbtUtils.createGZIPReader(stream).readTag()).getList("blocks", NbtType.COMPOUND);
        } catch (Exception e) {
            throw new AssertionError("Error while loading vanilla palette " + version, e);
        }
    }

    private Path getVanillaPalettePath(int version) {
        return this.getBinPath().resolve("block_palette_" + version + ".nbt");
    }

    public Block getBlock(int legacyId) {
        CustomBlockState state = this.legacy2CustomState.get(legacyId);
        if (state == null) {
            return Block.get(BlockID.INFO_UPDATE);
        }

        BlockContainer block = state.getFactory().create(legacyId & Block.DATA_MASK);
        if (block instanceof Block) {
            return (Block) block;
        }
        return null;
    }

    public Block getBlock(int id, int meta) {
        int legacyId = (id << Block.DATA_BITS) | meta;
        CustomBlockState state = this.legacy2CustomState.get(legacyId);
        if (state == null) {
            state = this.legacy2CustomState.get(id << Block.DATA_BITS);
            if (state == null) {
                return Block.get(BlockID.INFO_UPDATE);
            }
        }

        BlockContainer block = state.getFactory().create(meta);
        if (block instanceof Block) {
            return (Block) block;
        }
        return null;
    }

    public Class<?> getClassType(int blockId) {
        CustomBlockDefinition definition = this.blockDefinitions.get(blockId << Block.DATA_BITS);
        if (definition == null) {
            return null;
        }
        return definition.getTypeOf();
    }

    private void createBin() {
        Path filesPath = this.getBinPath();
        if (!Files.isDirectory(filesPath)) {
            try {
                Files.createDirectories(filesPath);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create bin/", e);
            }
        }
    }

    private Path getBinPath() {
        return Paths.get(this.server.getDataPath()).resolve(Paths.get("bin/"));
    }

    public Collection<CustomBlockDefinition> getBlockDefinitions() {
        return this.blockDefinitions.values();
    }

    private static int legacyToFullId(int legacyId) {
        int blockId = legacyId >> Block.DATA_BITS;
        int meta = legacyId & Block.DATA_MASK;
        return (blockId << Block.DATA_BITS) | meta;
    }

    public static CompoundTag convertNbtMap(NbtMap nbt) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (NBTOutputStream nbtOutputStream = NbtUtils.createWriter(stream)) {
                nbtOutputStream.writeTag(nbt);
            } finally {
                stream.close();
            }
            return NBTIO.read(stream.toByteArray(), ByteOrder.BIG_ENDIAN, false);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to convert NbtMap: " + nbt, e);
        }
    }
}
