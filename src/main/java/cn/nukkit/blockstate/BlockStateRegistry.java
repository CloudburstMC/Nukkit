package cn.nukkit.blockstate;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.HumanStringComparator;
import com.google.common.base.Preconditions;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
@ParametersAreNonnullByDefault
@Log4j2
public class BlockStateRegistry {
    public final int BIG_META_MASK = 0xFFFFFFFF;
    private final Set<String> LEGACY_NAME_SET = Collections.singleton(CommonBlockProperties.LEGACY_PROPERTY_NAME);
    private final Map<BlockState, Integer> blockStateToRuntimeId = new ConcurrentHashMap<>();
    private final Long2IntMap bigIdToRuntimeId = new Long2IntOpenHashMap();
    private final int updateBlockRuntimeId;
    
    private final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    private final Int2ObjectMap<String> blockIdToPersistenceName = new Int2ObjectOpenHashMap<>();
    private final Map<String, Integer> stateIdToRuntimeId = new ConcurrentHashMap<>();
    
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

        int infoUpdateRuntimeId = -1;
        
        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();
            CompoundTag stateKey = state.getCompound("block").copy().remove("version");
            String name = state.getCompound("block").getString("name").toLowerCase();

            registerStateNbt(state, runtimeId);
            
            if (name.equals("minecraft:info_update")) {
                infoUpdateRuntimeId = runtimeId;
            }
            
            List<CompoundTag> legacyStates = metaOverrides.get(stateKey);
            if (legacyStates == null) {
                if (!state.contains("LegacyStates")) {
                    continue;
                } else {
                    legacyStates = state.getList("LegacyStates", CompoundTag.class).getAll();
                }
            }

            // Resolve to first legacy id
            CompoundTag firstState = legacyStates.get(0);
            int firstId = firstState.getInt("id");
            int firstMeta = firstState.getInt("val");

            // Special condition: minecraft:wood maps 3 blocks, minecraft:wood, minecraft:log and minecraft:log2
            // All other cases, register the name normally
            if (isNameOwnerOfId(name, firstId)) {
                registerPersistenceName(firstId, name);
                registerLegacyState(name, firstMeta, runtimeId);
            }

            registerBigId(firstId, firstMeta, runtimeId);

            for (CompoundTag legacyState : legacyStates) {
                int newBlockId = legacyState.getInt("id");
                int meta = legacyState.getInt("val");
                registerBigId(newBlockId, meta, runtimeId);
                
                if (isNameOwnerOfId(name, newBlockId)) {
                    registerLegacyState(name, meta, runtimeId);
                }
            }
            // No point in sending this since the client doesn't use it.
            state.remove("meta");
            state.remove("LegacyStates");
        }

        if (infoUpdateRuntimeId == -1) {
            log.error("Could not find the minecraft:info_update runtime id!");
            infoUpdateRuntimeId = 0;
        }
        
        updateBlockRuntimeId = infoUpdateRuntimeId;
        
        try {
            blockPaletteBytes = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        
    }
    //</editor-fold>
    
    private boolean isNameOwnerOfId(String name, int blockId) {
        return !name.equals("minecraft:wood") || blockId == BlockID.WOOD_BARK;
    }
    
    @Nonnull
    private String getStateId(CompoundTag block) {
        Map<String, String> propertyMap = new TreeMap<>(HumanStringComparator.getInstance());
        for (Tag tag : block.getCompound("states").getAllTags()) {
            propertyMap.put(tag.getName(), tag.parseValue().toString());
        }

        String blockName = block.getString("name");
        Preconditions.checkArgument(!blockName.isEmpty(), "Couldn't find the block name!");
        StringBuilder stateId = new StringBuilder(blockName);
        propertyMap.forEach((name, value) -> stateId.append(';').append(name).append('=').append(value));
        return stateId.toString();
    }

    public int getRuntimeId(BlockState state) {
        return blockStateToRuntimeId.computeIfAbsent(state, BlockStateRegistry::discoverRuntimeId);
    }

    public int getRuntimeId(int blockId) {
        return getRuntimeId(blockId, 0);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "getRuntimeId(BlockState state)", since = "1.3.0.0-PN")
    public int getRuntimeId(int blockId, int meta) {
        long bigId = (long)blockId << 32 | meta;
        int runtimeId = bigIdToRuntimeId.get(bigId);
        if (runtimeId == -1) {
            synchronized (bigIdToRuntimeId) {
                return bigIdToRuntimeId.computeIfAbsent(bigId, k-> discoverRuntimeId(BlockState.of(blockId, meta)));
            }
        }
        return runtimeId;
    }

    private int discoverRuntimeId(BlockState state) {
        int runtimeId;
        Set<String> names = state.getPropertyNames();
        if (names.isEmpty() || names.equals(LEGACY_NAME_SET)) {
            runtimeId = discoverRuntimeIdFromLegacy(state);
        } else {
            runtimeId = discoverRuntimeIdByStateNbt(state);
        }
        removeStateIdsAsync(runtimeId);
        return runtimeId;
    }

    private int discoverRuntimeIdFromLegacy(BlockState state) {
        long bigId = state.getBigId();
        int runtimeId = bigIdToRuntimeId.get(bigId);
        if (runtimeId == -1) {
            return logDiscoveryError(state);
        }
        return runtimeId;
    }

    private int discoverRuntimeIdByStateNbt(BlockState state) {
        Integer runtimeId = stateIdToRuntimeId.remove(state.getStateId());
        if (runtimeId != null) {
            return runtimeId;
        }

        runtimeId = stateIdToRuntimeId.remove(state.getLegacyStateId());
        if (runtimeId != null) {
            return runtimeId;
        }

        runtimeId = logDiscoveryError(state);
        return runtimeId;
    }
    
    private void removeStateIdsAsync(@Nullable Integer runtimeId) {
        if (runtimeId != null && runtimeId != updateBlockRuntimeId) {
            new Thread(() -> stateIdToRuntimeId.values().removeIf(runtimeId::equals)).start();
        }
    }

    private int logDiscoveryError(BlockState state) {
        log.error("Found an unknown BlockId:Meta combination: "+state.getBlockId()+":"+state.getDataStorage()+", replacing with an \"UPDATE!\" block.");
        return updateBlockRuntimeId;
    }

    @Nullable
    public String getPersistenceName(int blockId) {
        return blockIdToPersistenceName.get(blockId);
    }

    public void registerPersistenceName(int blockId, String persistenceName) {
        synchronized (blockIdToPersistenceName) {
            String oldName = blockIdToPersistenceName.putIfAbsent(blockId, persistenceName.toLowerCase());
            if (oldName != null && !persistenceName.equalsIgnoreCase(oldName)) {
                throw new UnsupportedOperationException("The persistence name registration tried to replaced a name. Name:" + persistenceName + ", Old:" + oldName + ", Id:" + blockId);
            }
        }
    }

    private void registerStateNbt(CompoundTag state, int runtimeId) {
        registerStateId(getStateId(state.getCompound("block")), runtimeId);
    }
    
    private void registerStateId(String stateId, int runtimeId) {
        Integer old = stateIdToRuntimeId.putIfAbsent(stateId, runtimeId);
        if (old != null && !old.equals(runtimeId)) {
            throw new UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:"+old+", New:"+runtimeId+", State:"+stateId);
        }
    }

    private void registerBigId(long blockId, long meta, int runtimeId) {
        long bigId = blockId << 32 | meta;
        bigIdToRuntimeId.put(bigId, runtimeId);
    }
    
    private void registerLegacyState(String blockName, int meta, int runtimeId) {
        registerStateId(blockName+";nukkit-legacy="+meta, runtimeId);
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
