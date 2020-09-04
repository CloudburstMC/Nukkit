package cn.nukkit.blockstate;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.HumanStringComparator;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@ParametersAreNonnullByDefault
@Log4j2
public class BlockStateRegistry {
    public final int BIG_META_MASK = 0xFFFFFFFF;
    private final Pattern BLOCK_ID_NAME_PATTERN = Pattern.compile("^blockid:(\\d+)$"); 
    private final Set<String> LEGACY_NAME_SET = Collections.singleton(CommonBlockProperties.LEGACY_PROPERTY_NAME);
    
    private final Registration updateBlockRegistration;

    private final Map<BlockState, Registration> blockStateRegistration = new ConcurrentHashMap<>();
    private final Map<String, Registration> stateIdRegistration = new ConcurrentHashMap<>();

    private final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    private final Int2ObjectMap<String> blockIdToPersistenceName = new Int2ObjectOpenHashMap<>();
    private final Map<String, Integer> persistenceNameToBlockId = new LinkedHashMap<>();
    
    private final byte[] blockPaletteBytes;

    //<editor-fold desc="static initialization" defaultstate="collapsed">
    static {
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

        //<editor-fold desc="Loading block_ids.csv" defaultstate="collapsed">
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("block_ids.csv")) { 
            if (stream == null) {
                throw new AssertionError("Unable to locate block_ids.csv");
            }

            int count = 0;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    count++;
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    Preconditions.checkArgument(parts.length == 2 || parts[0].matches("^[0-9]+$"));
                    if (parts.length > 1 && parts[1].startsWith("minecraft:")) {
                        int id = Integer.parseInt(parts[0]);
                        blockIdToPersistenceName.put(id, parts[1]);
                        persistenceNameToBlockId.put(parts[1], id);
                    }
                }
            } catch (Exception e) {
                throw new IOException("Error reading the line "+count+" of the block_ids.csv", e);
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

        Registration infoUpdateRuntimeId = null;
        
        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();
            String name = state.getCompound("block").getString("name").toLowerCase();
            
            if (name.equals("minecraft:info_update")) {
                infoUpdateRuntimeId = new Registration(BlockState.of(248), runtimeId);
            }
            
            List<CompoundTag> legacyStates = metaOverrides.get(state.getCompound("block").copy().remove("version"));
            if (legacyStates == null) {
                if (!state.contains("LegacyStates")) {
                    registerStateId(state, runtimeId);
                    continue;
                } else {
                    legacyStates = state.getList("LegacyStates", CompoundTag.class).getAll();
                }
            }
            
            // Override is forcing to clear the LegacyStates
            if (legacyStates.isEmpty()) {
                registerStateId(state, runtimeId);
                continue;
            }

            // Resolve to first legacy id
            CompoundTag firstState = legacyStates.get(0);
            int firstId = firstState.getInt("id");
            int firstMeta = firstState.getInt("val");

            // Special condition: minecraft:wood maps 3 blocks, minecraft:wood, minecraft:log and minecraft:log2
            // All other cases, register the name normally
            if (isNameOwnerOfId(name, firstId)) {
                registerPersistenceName(firstId, name);
                registerStateId(state, runtimeId);
                registerState(firstId, firstMeta, state, runtimeId);
            }
            
            registerState(firstId, firstMeta, state, runtimeId);

            for (CompoundTag legacyState : legacyStates) {
                int newBlockId = legacyState.getInt("id");
                int meta = legacyState.getInt("val");
                registerState(newBlockId, meta, state, runtimeId);
                
                if (isNameOwnerOfId(name, newBlockId)) {
                    registerState(newBlockId, meta, state, runtimeId);
                }
            }
            // No point in sending this since the client doesn't use it.
            state.remove("meta");
            state.remove("LegacyStates");
        }

        if (infoUpdateRuntimeId == null) {
            throw new IllegalStateException("Could not find the minecraft:info_update runtime id!");
        }
        
        updateBlockRegistration = infoUpdateRuntimeId;
        
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
        return getRegistration(state).runtimeId;
    }
    
    private Registration getRegistration(BlockState state) {
        return blockStateRegistration.computeIfAbsent(state, BlockStateRegistry::findRegistration);
    }

    public int getRuntimeId(int blockId) {
        return getRuntimeId(BlockState.of(blockId));
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "getRuntimeId(BlockState state)", since = "1.3.0.0-PN")
    public int getRuntimeId(int blockId, int meta) {
        return getRuntimeId(BlockState.of(blockId, meta));
    }

    private Registration findRegistration(final BlockState state) {
        // Special case for PN-96 PowerNukkit#210 where the world contains blocks like 0:13, 0:7, etc
        if (state.getBlockId() == BlockID.AIR) {
            Registration airRegistration = blockStateRegistration.get(BlockState.AIR);
            if (airRegistration != null) {
                return new Registration(state, airRegistration.runtimeId);
            }
        }
        
        Registration registration;
        if (state.getPropertyNames().equals(LEGACY_NAME_SET)) {
            registration = logDiscoveryError(state);
        } else {
            registration = findRegistrationByStateId(state);
        }
        removeStateIdsAsync(registration);
        return registration;
    }

    private Registration findRegistrationByStateId(BlockState state) {
        Registration registration;
        try {
            registration = stateIdRegistration.remove(state.getStateId());
            if (registration != null) {
                registration.state = state;
                return registration;
            }
        } catch (Exception e) {
            try {
                log.fatal("An error has occurred while trying to get the stateId of state: " +
                        state.getBlockId() + ":" + state.getDataStorage()
                        + " - " + state.getProperties()
                        + " - " + blockIdToPersistenceName.get(state.getBlockId()),
                        e);
            } catch (Exception e2) {
                e.addSuppressed(e2);
                log.fatal("An error has occurred while trying to get the stateId of state: " +
                        state.getBlockId() + ":" + state.getDataStorage(), 
                        e);
            }
        }
        
        try {
            registration = stateIdRegistration.remove(state.getLegacyStateId());
            if (registration != null) {
                registration.state = state;
                return registration;
            }
        } catch (Exception e) {
            log.fatal("An error has occurred while trying to parse the legacyStateId of "+state.getBlockId()+":"+state.getDataStorage(), e);
        }

        return logDiscoveryError(state);
    }
    
    private void removeStateIdsAsync(@Nullable Registration registration) {
        if (registration != null && registration != updateBlockRegistration) {
            new Thread(() -> stateIdRegistration.values().removeIf(r -> r.runtimeId == registration.runtimeId)).start();
        }
    }

    private Registration logDiscoveryError(BlockState state) {
        log.error("Found an unknown BlockId:Meta combination: "+state.getBlockId()+":"+state.getDataStorage()
                + " - " + state.getStateId()
                + " - " + state.getProperties()
                + " - " + blockIdToPersistenceName.get(state.getBlockId())
                + ", trying to repair or replacing with an \"UPDATE!\" block.");
        return updateBlockRegistration;
    }

    @Nonnull
    public String getPersistenceName(int blockId) {
        String persistenceName = blockIdToPersistenceName.get(blockId);
        if (persistenceName == null) {
            String fallback = "blockid:"+ blockId;
            log.warn("The persistence name of the block id "+ blockId +" is unknown! Using "+fallback+" as an alternative!");
            registerPersistenceName(blockId, fallback);
            return fallback;
        }
        return persistenceName;
    }

    public void registerPersistenceName(int blockId, String persistenceName) {
        synchronized (blockIdToPersistenceName) {
            String newName = persistenceName.toLowerCase();
            String oldName = blockIdToPersistenceName.putIfAbsent(blockId, newName);
            if (oldName != null && !persistenceName.equalsIgnoreCase(oldName)) {
                throw new UnsupportedOperationException("The persistence name registration tried to replaced a name. Name:" + persistenceName + ", Old:" + oldName + ", Id:" + blockId);
            }
            Integer oldId = persistenceNameToBlockId.putIfAbsent(newName, blockId);
            if (oldId != null && blockId != oldId) {
                blockIdToPersistenceName.remove(blockId);
                throw new UnsupportedOperationException("The persistence name registration tried to replaced an id. Name:" + persistenceName + ", OldId:" + oldId + ", Id:" + blockId);
            }
        }
    }

    private void registerStateId(CompoundTag state, int runtimeId) {
        String stateId = getStateId(state.getCompound("block"));
        Registration registration = new Registration(null, runtimeId);
        
        Registration old = stateIdRegistration.putIfAbsent(stateId, registration);
        if (old != null && !old.equals(registration)) {
            throw new UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:"+old+", New:"+runtimeId+", State:"+stateId);
        }
    }
    
    private void registerState(int blockId, int meta, CompoundTag originalState, int runtimeId) {
        BlockState state = BlockState.of(blockId, meta);
        Registration registration = new Registration(state, runtimeId);

        Registration old = blockStateRegistration.putIfAbsent(state, registration);
        if (old != null && !registration.equals(old)) {
            throw new UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:"+old+", New:"+runtimeId+", State:"+state);
        }

        CompoundTag block = originalState.getCompound("block");
        stateIdRegistration.remove(getStateId(block));
        stateIdRegistration.remove(state.getLegacyStateId());
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
    
    public void putBlockPaletteBytes(BinaryStream stream) {
        stream.put(blockPaletteBytes);
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

    /**
     * @throws InvalidBlockStateException
     */
    @Nonnull
    public MutableBlockState createMutableState(int blockId, Number storage) {
        MutableBlockState blockState = createMutableState(blockId);
        blockState.setDataStorage(storage);
        return blockState;
    }

    public int getUpdateBlockRegistration() {
        return updateBlockRegistration.runtimeId;
    }
    
    @Nullable
    public Integer getBlockId(String persistenceName) {
        Integer blockId = persistenceNameToBlockId.get(persistenceName);
        if (blockId != null) {
            return blockId;
        }
        
        Matcher matcher = BLOCK_ID_NAME_PATTERN.matcher(persistenceName);
        if (matcher.matches()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getFallbackRuntimeId() {
        return updateBlockRegistration.runtimeId;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState getFallbackBlockState() {
        return updateBlockRegistration.state;
    }
    
    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    private class Registration {
        @Nullable
        private BlockState state;
        
        private final int runtimeId;
    }
}
