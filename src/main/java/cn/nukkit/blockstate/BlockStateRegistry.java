package cn.nukkit.blockstate;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
@ParametersAreNonnullByDefault
@Log4j2
public class BlockStateRegistry {
    public final int BIG_META_MASK = 0xFFFFFFFF;
    private final Map<BlockState, Integer> blockStateToRuntimeId = new ConcurrentHashMap<>();
    private final Long2IntMap bigIdToRuntimeId = new Long2IntOpenHashMap();
    private final int updateBlockRuntimeId;
    
    private final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    private final Int2ObjectMap<String> blockIdToPersistenceName = new Int2ObjectOpenHashMap<>();
    private final Map<CompoundTag, Integer> stateNbtToRuntimeId = new ConcurrentHashMap<>();
    
    private final byte[] blockPaletteBytes;

    //<editor-fold desc="static initialization" defaultstate="collapsed">
    static {
        bigIdToRuntimeId.defaultReturnValue(-1);

        Map<CompoundTag, List<CompoundTag>> metaOverrides = new LinkedHashMap<>();
        //<editor-fold desc="Loading runtime_block_states_overrides.dat" defaultstate="collapsed">
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_overrides.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            ListTag<CompoundTag> states;
            try (BufferedInputStream buffered = new BufferedInputStream(stream)) {
                states = NBTIO.read(buffered).getList("Overrides", CompoundTag.class);
            }

            for (CompoundTag override : states.getAll()) {
                if (override.contains("block") && override.contains("LegacyStates")) {
                    metaOverrides.put(override.getCompound("block").remove("version"), override.getList("LegacyStates", CompoundTag.class).getAll());
                }
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>

        ListTag<CompoundTag> tag;
        //<editor-fold desc="Loading runtime_block_states.dat" defaultstate="collapsed">
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            try (BufferedInputStream buffered = new BufferedInputStream(stream)) {
                //noinspection unchecked
                tag = (ListTag<CompoundTag>) NBTIO.readTag(buffered, ByteOrder.LITTLE_ENDIAN, false);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        //</editor-fold>

        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();
            CompoundTag stateKey = state.getCompound("block").copy().remove("version");
            String name = state.getCompound("block").getString("name").toLowerCase();
            List<CompoundTag> legacyStates = metaOverrides.get(stateKey);
            if (legacyStates == null) {
                if (!state.contains("LegacyStates")) {
                    registerStateNbt(state, runtimeId);
                    continue;
                } else {
                    legacyStates = state.getList("LegacyStates", CompoundTag.class).getAll();
                }
            }

            // Resolve to first legacy id
            CompoundTag firstState = legacyStates.get(0);
            int firstId = firstState.getInt("id");
            int firstMeta = firstState.getInt("val");

            registerBigId(firstId, firstMeta, runtimeId);
            registerPersistenceName(firstId, name);

            for (CompoundTag legacyState : legacyStates) {
                int newBlockId = legacyState.getInt("id");
                int meta = legacyState.getInt("val");
                registerBigId(newBlockId, meta, runtimeId);
            }
            // No point in sending this since the client doesn't use it.
            state.remove("meta");
            state.remove("LegacyStates");
        }

        updateBlockRuntimeId = getRuntimeId(new BlockState(248, 0));
        
        try {
            blockPaletteBytes = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        
    }
    //</editor-fold>

    public int getRuntimeId(BlockState state) {
        return blockStateToRuntimeId.computeIfAbsent(state, BlockStateRegistry::discoverRuntimeId);
    }

    @Deprecated
    @DeprecationDetails(reason = "Does not support hyper ids", replaceWith = "getOrCreateRuntimeId(int id, int meta)", since = "1.3.0.0-PN")
    public int getRuntimeId(int blockId, int meta) {
        int runtimeId = bigIdToRuntimeId.get((long)blockId << 32 | meta);
        return runtimeId == -1? updateBlockRuntimeId : runtimeId;
    }

    private int discoverRuntimeId(BlockState state) {
        if (state.getPropertyNames().equals(CommonBlockProperties.LEGACY_PROPERTIES.getNames())) {
            return discoverRuntimeIdFromLegacy(state);
        }
        return discoverRuntimeIdByStateNbt(state);
    }

    private int discoverRuntimeIdFromLegacy(BlockState state) {
        long bigId = state.getBigId();
        int runtimeId = bigIdToRuntimeId.get(bigId);
        if (runtimeId == -1) {
            logDiscoveryError(state);
            return updateBlockRuntimeId;
        }
        return runtimeId;
    }

    private int discoverRuntimeIdByStateNbt(BlockState state) {
        CompoundTag nbt = new CompoundTag(getPersistenceName(state.getBlockId()));
        for (String propertyName : state.getPropertyNames()) {
            nbt.putString(propertyName, state.getPersistenceValue(propertyName));
        }
        
        Integer runtimeId = stateNbtToRuntimeId.remove(nbt);
        if (runtimeId == null) {
            runtimeId = blockStateToRuntimeId.get(state);
        }
        
        if (runtimeId == null) {
            runtimeId = blockStateToRuntimeId.putIfAbsent(state, -1);
        }
        
        if (runtimeId == null) {
            logDiscoveryError(state);
            return -1;
        }
        
        return runtimeId;
    }

    private void logDiscoveryError(BlockState state) {
        log.error("Found an unknown BlockId:Meta combination: "+state.getBlockId()+":"+state.getDataStorage()+", replacing with an \"UPDATE!\" block.");
    }

    @Nullable
    public String getPersistenceName(int blockId) {
        return blockIdToPersistenceName.get(blockId);
    }

    public void registerPersistenceName(int blockId, String persistenceName) {
        String oldName = blockIdToPersistenceName.putIfAbsent(blockId, persistenceName.toLowerCase());
        if (oldName != null && !persistenceName.equalsIgnoreCase(oldName)) {
            throw new UnsupportedOperationException("The persistence name registration tried to replaced a name. Name:"+persistenceName+", Old:"+oldName+", Id:"+blockId);
        }
    }

    private void registerStateNbt(CompoundTag state, int runtimeId) {
        CompoundTag block = state.getCompound("block");
        CompoundTag nbt = new CompoundTag(block.getString("name").toLowerCase());
        for (Tag property : block.getCompound("states").getAllTags()) {
            nbt.putString(property.getName(), property.parseValue().toString());
        }
        Integer old = stateNbtToRuntimeId.putIfAbsent(nbt, runtimeId);
        if (old != null) {
            throw new UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:"+old+", New:"+runtimeId+", State:"+nbt);
        }
    }

    private void registerBigId(long blockId, long meta, int runtimeId) {
        long bigId = blockId << 32 | meta;
        bigIdToRuntimeId.put(bigId, runtimeId);
    }

    public int getBlockPaletteDataVersion() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        Object obj = blockPaletteBytes;
        return obj.hashCode();
    }

    @Nonnull
    public byte[] getBlockPaletteBytes() {
        return blockPaletteBytes.clone();
    }
    
    public int getBlockPaletteLength() {
        return blockPaletteBytes.length;
    }
    
    public void copyBlockPaletteBytes(byte[] target, int targetIndex) {
        System.arraycopy(blockPaletteBytes, 0, target, targetIndex, blockPaletteBytes.length);
    }

    @SuppressWarnings({"deprecation", "squid:CallToDepreca"})
    @Nonnull
    public BlockProperties getProperties(int blockId) {
        int fullId = blockId << Block.DATA_BITS;
        Block block;
        if (fullId >= Block.fullList.length || (block = Block.fullList[fullId]) == null) {
            return BlockUnknown.PROPERTIES;
        }
        return block.getProperties();
    }

    @Nonnull
    public MutableBlockState createMutableState(int blockId) {
        return getProperties(blockId).createMutableState(blockId);
    }
    
    @Nonnull
    public MutableBlockState createMutableState(int blockId, int bigMeta) {
        MutableBlockState blockState = createMutableState(blockId);
        blockState.setDataStorageFromInt(bigMeta);
        return blockState;
    }

    @Nonnull
    public MutableBlockState createMutableState(int blockId, Number storage) {
        MutableBlockState blockState = createMutableState(blockId);
        blockState.setDataStorage(storage);
        return blockState;
    }
}
