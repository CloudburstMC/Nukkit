package cn.nukkit.blockstate;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@UtilityClass
@ParametersAreNonnullByDefault
@Log4j2
public class BlockStateRegistry {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final int BIG_META_MASK = 0xFFFFFFFF;
    private final ExecutorService asyncStateRemover = Executors.newSingleThreadExecutor();
    private final Pattern BLOCK_ID_NAME_PATTERN = Pattern.compile("^blockid:(\\d+)$"); 

    private final Registration updateBlockRegistration;

    private final Map<BlockState, Registration> blockStateRegistration = new ConcurrentHashMap<>();
    private final Map<String, Registration> stateIdRegistration = new ConcurrentHashMap<>();
    private final Int2ObjectMap<Registration> runtimeIdRegistration = new Int2ObjectOpenHashMap<>();

    private final Int2ObjectMap<String> blockIdToPersistenceName = new Int2ObjectOpenHashMap<>();
    private final Map<String, Integer> persistenceNameToBlockId = new LinkedHashMap<>();
    
    private final byte[] blockPaletteBytes;

    //<editor-fold desc="static initialization" defaultstate="collapsed">
    static {

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

        //<editor-fold desc="Loading canonical_block_states.nbt" defaultstate="collapsed">
        List<CompoundTag> tags = new ArrayList<>();
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("canonical_block_states.nbt")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            try (BufferedInputStream bis = new BufferedInputStream(stream)) {
                int runtimeId = 0;
                while (bis.available() > 0) {
                    CompoundTag tag = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true);
                    tag.putInt("runtimeId", runtimeId++);
                    tag.putInt("blockId", persistenceNameToBlockId.getOrDefault(tag.getString("name").toLowerCase(), -1));
                    tags.add(tag);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>
        Integer infoUpdateRuntimeId = null;
        
        for (CompoundTag state : tags) {
            int blockId = state.getInt("blockId");
            int runtimeId = state.getInt("runtimeId");
            String name = state.getString("name").toLowerCase();
            if (name.equals("minecraft:unknown")) {
                infoUpdateRuntimeId = runtimeId;
            }
            
            // Special condition: minecraft:wood maps 3 blocks, minecraft:wood, minecraft:log and minecraft:log2
            // All other cases, register the name normally
            if (isNameOwnerOfId(name, blockId)) {
                registerPersistenceName(blockId, name);
                registerStateId(state, runtimeId);
            }
        }

        if (infoUpdateRuntimeId == null) {
            throw new IllegalStateException("Could not find the minecraft:info_update runtime id!");
        }
        
        updateBlockRegistration = findRegistrationByRuntimeId(infoUpdateRuntimeId);
        
        try {
            blockPaletteBytes = NBTIO.write(tags, ByteOrder.LITTLE_ENDIAN, true);
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

        String blockName = block.getString("name").toLowerCase(Locale.ENGLISH);
        Preconditions.checkArgument(!blockName.isEmpty(), "Couldn't find the block name!");
        StringBuilder stateId = new StringBuilder(blockName);
        propertyMap.forEach((name, value) -> stateId.append(';').append(name).append('=').append(value));
        return stateId.toString();
    }

    @Nullable
    private static Registration findRegistrationByRuntimeId(int runtimeId) {
        return runtimeIdRegistration.get(runtimeId);
    }

    /**
     * @return {@code null} if the runtime id does not matches any known block state.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public BlockState getBlockStateByRuntimeId(int runtimeId) {
        Registration registration = findRegistrationByRuntimeId(runtimeId);
        if (registration == null) {
            return null;
        }
        BlockState state = registration.state;
        if (state != null) {
            return state;
        }
        CompoundTag originalBlock = registration.originalBlock;
        if (originalBlock != null) {
            state = buildStateFromCompound(originalBlock);
            if (state != null) {
                registration.state = state;
                registration.originalBlock = null;
            }
        }
        return state;
    }
    
    @Nullable
    private BlockState buildStateFromCompound(CompoundTag block) {
        String name = block.getString("name").toLowerCase(Locale.ENGLISH);
        Integer id = getBlockId(name);
        if (id == null) {
            return null;
        }

        BlockState state = BlockState.of(id);

        CompoundTag properties = block.getCompound("states");
        for (Tag tag : properties.getAllTags()) {
            state = state.withProperty(tag.getName(), tag.parseValue().toString());
        }
        
        return state;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getRuntimeId(BlockState state) {
        return getRegistration(convertToNewState(state)).runtimeId;
    }

    private BlockState convertToNewState(BlockState oldState) {
        int exactInt;
        switch (oldState.getBlockId()) {
            // Check OldWoodBarkUpdater.java and https://minecraft.fandom.com/wiki/Log#Metadata
            // The Only bark variant is replaced in the client side to minecraft:wood with the same wood type
            case BlockID.LOG:
            case BlockID.LOG2:
                if (oldState.getBitSize() == 4 && ((exactInt = oldState.getExactIntStorage()) & 0b1100) == 0b1100) {
                    int increment = oldState.getBlockId() == BlockID.LOG? 0b000 : 0b100;
                    return BlockState.of(BlockID.WOOD_BARK, (exactInt & 0b11) + increment);
                }
                break;
        }
        return oldState;
    }

    private Registration getRegistration(BlockState state) {
        return blockStateRegistration.computeIfAbsent(state, BlockStateRegistry::findRegistration);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getRuntimeId(int blockId) {
        return getRuntimeId(BlockState.of(blockId));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
                return new Registration(state, airRegistration.runtimeId, null);
            }
        }

        Registration registration = findRegistrationByStateId(state);
        removeStateIdsAsync(registration);
        return registration;
    }

    private Registration findRegistrationByStateId(BlockState state) {
        Registration registration;
        try {
            registration = stateIdRegistration.remove(state.getStateId());
            if (registration != null) {
                registration.state = state;
                registration.originalBlock = null;
                return registration;
            }
        } catch (Exception e) {
            try {
                log.fatal("An error has occurred while trying to get the stateId of state: "
                        + "{}:{}"
                        + " - {}"
                        + " - {}",
                        state.getBlockId(),
                        state.getDataStorage(),
                        state.getProperties(),
                        blockIdToPersistenceName.get(state.getBlockId()),
                        e);
            } catch (Exception e2) {
                e.addSuppressed(e2);
                log.fatal("An error has occurred while trying to get the stateId of state: {}:{}",
                        state.getBlockId(), state.getDataStorage(), e);
            }
        }
        
        try {
            registration = stateIdRegistration.remove(state.getLegacyStateId());
            if (registration != null) {
                registration.state = state;
                registration.originalBlock = null;
                return registration;
            }
        } catch (Exception e) {
            log.fatal("An error has occurred while trying to parse the legacyStateId of {}:{}", state.getBlockId(), state.getDataStorage(), e);
        }

        return logDiscoveryError(state);
    }
    
    private void removeStateIdsAsync(@Nullable Registration registration) {
        if (registration != null && registration != updateBlockRegistration) {
            asyncStateRemover.submit(() -> stateIdRegistration.values().removeIf(r -> r.runtimeId == registration.runtimeId));
        }
    }

    private Registration logDiscoveryError(BlockState state) {
        log.error("Found an unknown BlockId:Meta combination: {}:{}"
                + " - {}"
                + " - {}"
                + " - {}"
                + ", trying to repair or replacing with an \"UPDATE!\" block.",
                state.getBlockId(), state.getDataStorage(), state.getStateId(), state.getProperties(),
                blockIdToPersistenceName.get(state.getBlockId())
                );
        return updateBlockRegistration;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public List<String> getPersistenceNames() {
        return new ArrayList<>(persistenceNameToBlockId.keySet());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getPersistenceName(int blockId) {
        String persistenceName = blockIdToPersistenceName.get(blockId);
        if (persistenceName == null) {
            String fallback = "blockid:"+ blockId;
            log.warn("The persistence name of the block id {} is unknown! Using {} as an alternative!", blockId, fallback);
            registerPersistenceName(blockId, fallback);
            return fallback;
        }
        return persistenceName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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

    private void registerStateId(CompoundTag block, int runtimeId) {
        String stateId = getStateId(block);
        Registration registration = new Registration(null, runtimeId, block);
        
        Registration old = stateIdRegistration.putIfAbsent(stateId, registration);
        if (old != null && !old.equals(registration)) {
            throw new UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:"+old+", New:"+runtimeId+", State:"+stateId);
        }
        
        runtimeIdRegistration.put(runtimeId, registration);
    }
    
    private void registerState(int blockId, int meta, CompoundTag originalState, int runtimeId) {
        BlockState state = BlockState.of(blockId, meta);
        Registration registration = new Registration(state, runtimeId, null);

        Registration old = blockStateRegistration.putIfAbsent(state, registration);
        if (old != null && !registration.equals(old)) {
            throw new UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:"+old+", New:"+runtimeId+", State:"+state);
        }
        runtimeIdRegistration.put(runtimeId, registration);
        
        stateIdRegistration.remove(getStateId(originalState));
        stateIdRegistration.remove(state.getLegacyStateId());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBlockPaletteDataVersion() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        Object obj = blockPaletteBytes;
        return obj.hashCode();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public byte[] getBlockPaletteBytes() {
        return blockPaletteBytes.clone();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void putBlockPaletteBytes(BinaryStream stream) {
        stream.put(blockPaletteBytes);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBlockPaletteLength() {
        return blockPaletteBytes.length;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void copyBlockPaletteBytes(byte[] target, int targetIndex) {
        System.arraycopy(blockPaletteBytes, 0, target, targetIndex, blockPaletteBytes.length);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public MutableBlockState createMutableState(int blockId) {
        return getProperties(blockId).createMutableState(blockId);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public MutableBlockState createMutableState(int blockId, int bigMeta) {
        MutableBlockState blockState = createMutableState(blockId);
        blockState.setDataStorageFromInt(bigMeta);
        return blockState;
    }

    /**
     * @throws InvalidBlockStateException
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public MutableBlockState createMutableState(int blockId, Number storage) {
        MutableBlockState blockState = createMutableState(blockId);
        blockState.setDataStorage(storage);
        return blockState;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getUpdateBlockRegistration() {
        return updateBlockRegistration.runtimeId;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
    private static class Registration {
        @Nullable
        private BlockState state;
        
        private final int runtimeId;
        
        @Nullable
        private CompoundTag originalBlock;
    }
}
