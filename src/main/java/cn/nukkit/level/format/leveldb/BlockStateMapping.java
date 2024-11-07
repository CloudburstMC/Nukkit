package cn.nukkit.level.format.leveldb;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.leveldb.structure.BlockStateSnapshot;
import cn.nukkit.level.format.leveldb.updater.BlockStateUpdaterChunker;
import cn.nukkit.level.format.leveldb.updater.BlockStateUpdaterVanilla;
import cn.nukkit.utils.MainLogger;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cloudburstmc.blockstateupdater.*;
import org.cloudburstmc.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import org.cloudburstmc.nbt.NbtMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.nukkit.level.format.leveldb.LevelDBConstants.PALETTE_VERSION;

public class BlockStateMapping {
    private static final Logger log = LogManager.getLogger("LevelDB-Logger");
    private static final Logger serverLog = LogManager.getLogger(MainLogger.class);

    private static final CompoundTagUpdaterContext CONTEXT;
    private static final int LATEST_UPDATER_VERSION;
    private static final BlockStateMapping INSTANCE = new BlockStateMapping(PALETTE_VERSION);

    private static final Cache<NbtMap, NbtMap> BLOCK_UPDATE_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();

    static {
        INSTANCE.setLegacyMapper(new NukkitLegacyMapper());
        NukkitLegacyMapper.registerStates(INSTANCE);

        List<BlockStateUpdater> updaters = new ArrayList<>();
        updaters.add(BlockStateUpdaterBase.INSTANCE);
        updaters.add(BlockStateUpdater_1_10_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_12_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_13_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_14_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_15_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_16_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_16_210.INSTANCE);
        updaters.add(BlockStateUpdater_1_17_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_17_40.INSTANCE);
        updaters.add(BlockStateUpdater_1_18_10.INSTANCE);
        updaters.add(BlockStateUpdater_1_18_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_20.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_70.INSTANCE);
        updaters.add(BlockStateUpdater_1_19_80.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_10.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_30.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_40.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_50.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_60.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_70.INSTANCE);
        updaters.add(BlockStateUpdater_1_20_80.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_0.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_10.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_20.INSTANCE);
        updaters.add(BlockStateUpdater_1_21_30.INSTANCE);
        updaters.add(BlockStateUpdaterVanilla.INSTANCE);

        boolean chunkerSupport = Boolean.parseBoolean(System.getProperty("leveldb-chunker"));
        if (chunkerSupport) {
            updaters.add(BlockStateUpdaterChunker.INSTANCE);
            serverLog.warn("Enabled chunker.app LevelDB updater. This may impact chunk loading performance!");
        }

        CompoundTagUpdaterContext context = new CompoundTagUpdaterContext();
        updaters.forEach(updater -> updater.registerUpdaters(context));

        CONTEXT = context;
        LATEST_UPDATER_VERSION = context.getLatestVersion();
        log.info("Latest block state updater version {}", context.getLatestVersion());
    }

    public static BlockStateMapping get() {
        return INSTANCE;
    }

    private final Int2ObjectMap<BlockStateSnapshot> runtime2State = new Int2ObjectOpenHashMap<>();
    private final Object2ObjectMap<NbtMap, BlockStateSnapshot> paletteMap = new Object2ObjectOpenCustomHashMap<>(new Hash.Strategy<NbtMap>() {
        @Override
        public int hashCode(NbtMap nbtMap) {
            return nbtMap.hashCode();
        }

        @Override
        public boolean equals(NbtMap nbtMap1, NbtMap nbtMap2) {
            return Objects.equals(nbtMap1, nbtMap2);
        }
    });
    private final int version;

    private LegacyStateMapper legacyMapper;

    private int defaultRuntimeId = -1;
    private BlockStateSnapshot defaultState;

    public BlockStateMapping(int version) {
        this(version, null);
    }

    public BlockStateMapping(int version, LegacyStateMapper legacyMapper) {
        this.version = version;
        this.legacyMapper = legacyMapper;
    }

    public void registerState(int runtimeId, NbtMap state) {
        Preconditions.checkArgument(!this.runtime2State.containsKey(runtimeId),
                "Mapping for runtimeId " + runtimeId + " is already created!");
        Preconditions.checkArgument(!this.paletteMap.containsKey(state),
                "Mapping for state is already created: " + state);

        BlockStateSnapshot blockState = BlockStateSnapshot.builder()
                .version(this.version)
                .vanillaState(state)
                .runtimeId(runtimeId)
                .build();
        this.runtime2State.put(runtimeId, blockState);
        this.paletteMap.put(state, blockState);
    }

    public void clearMapping() {
        this.runtime2State.clear();
        this.paletteMap.clear();
    }

    public BlockStateSnapshot getState(int legacyId, int data) {
        int runtimeId = this.legacyMapper.legacyToRuntime(legacyId, data);
        if (runtimeId == -1) {
            log.warn("Can not find state! No legacy2runtime mapping for " + legacyId + ":" + data);
            return this.getDefaultState();
        }
        return this.getState(runtimeId);
    }

    public BlockStateSnapshot getState(int runtimeId) {
        BlockStateSnapshot state = this.runtime2State.get(runtimeId);
        if (state == null) {
            log.warn("Can not find state! No runtime2State mapping for " + runtimeId);
            return this.getDefaultState();
        }
        return state;
    }

    public BlockStateSnapshot getState(NbtMap vanillaState) {
        BlockStateSnapshot state = this.paletteMap.get(vanillaState);
        if (state == null) {
            log.warn("Can not find block state! " + vanillaState);
            return this.getDefaultState();
        }
        return state;
    }

    public BlockStateSnapshot getStateUnsafe(NbtMap vanillaState) {
        return this.paletteMap.get(vanillaState);
    }

    public int getRuntimeId(int legacyId, int data) {
        int runtimeId = this.legacyMapper.legacyToRuntime(legacyId, data);
        if (runtimeId == -1) {
            log.warn("Can not find runtimeId! No legacy2runtime mapping for " + legacyId + ":" + data);
            return this.getDefaultRuntimeId();
        }
        return runtimeId;
    }

    public int getFullId(int runtimeId) {
        int fullId = this.legacyMapper.runtimeToFullId(runtimeId);
        if (fullId == -1) {
            log.warn("Can not find legacyId! No runtime2FullId mapping for " + runtimeId);
            fullId = this.legacyMapper.runtimeToFullId(this.getDefaultRuntimeId());
            Preconditions.checkArgument(fullId != -1, "Can not find fullId for default runtimeId: " + this.getDefaultRuntimeId());
        }
        return fullId;
    }

    public int getLegacyId(int runtimeId) {
        int legacyId = this.legacyMapper.runtimeToLegacyId(runtimeId);
        if (legacyId == -1) {
            log.warn("Can not find legacyId! No runtime2legacy mapping for " + runtimeId);
            legacyId = this.legacyMapper.runtimeToLegacyId(this.getDefaultRuntimeId());
            Preconditions.checkArgument(legacyId != -1, "Can not find legacyId for default runtimeId: " + this.getDefaultRuntimeId());
        }
        return legacyId;
    }

    public int getLegacyData(int runtimeId) {
        int data = this.legacyMapper.runtimeToLegacyData(runtimeId);
        if (data == -1) {
            log.warn("Can not find legacyId! No runtime2legacy mapping for " + runtimeId);
            data = this.legacyMapper.runtimeToLegacyData(this.getDefaultRuntimeId());
            Preconditions.checkArgument(data != -1, "Can not find legacyData for default runtimeId: " + this.getDefaultRuntimeId());
        }
        return data;
    }

    public void setDefaultBlock(int legacyId, int legacyData) {
        int runtimeId = this.legacyMapper.legacyToRuntime(legacyId, legacyData);
        Preconditions.checkArgument(runtimeId != -1, "Can not find runtimeId mapping for default block: " + legacyId + ":" + legacyData);
        this.defaultRuntimeId = runtimeId;

        BlockStateSnapshot state = this.runtime2State.get(runtimeId);
        Preconditions.checkNotNull(state, "Can not find state for default block: " + legacyId + ":" + legacyData);
        this.defaultState = state;
    }

    public int getDefaultRuntimeId() {
        if (this.defaultRuntimeId == -1) {
            this.setDefaultBlock(BlockID.INFO_UPDATE, 0);
        }
        return this.defaultRuntimeId;
    }

    public BlockStateSnapshot getDefaultState() {
        if (this.defaultState == null) {
            this.setDefaultBlock(BlockID.INFO_UPDATE, 0);
        }
        return this.defaultState;
    }

    public BlockStateSnapshot updateState(NbtMap state) {
        BlockStateSnapshot blockState = this.paletteMap.get(state);
        if (blockState == null) {
            blockState = this.updateStateUnsafe(state);
        }
        return blockState;
    }

    public BlockStateSnapshot updateStateUnsafe(NbtMap state) {
        return this.getState(this.updateVanillaState(state));
    }

    public BlockStateSnapshot getUpdatedState(NbtMap state) {
        if (this.paletteMap.get(state) == null) {
            return this.getState(this.updateVanillaState(state));
        }
        return null;
    }

    public NbtMap updateVanillaState(NbtMap state) {
        NbtMap cached = BLOCK_UPDATE_CACHE.getIfPresent(state);
        if (cached == null) {
            int version = state.getInt("version"); // TODO: validate this when updating next time
            cached = CONTEXT.update(state, LATEST_UPDATER_VERSION == version ? version - 1 : version);
            BLOCK_UPDATE_CACHE.put(state, cached);
        }
        return cached;
    }

    public BlockStateSnapshot getUpdatedOrCustom(NbtMap state) {
        return this.getUpdatedOrCustom(state, this.updateVanillaState(state));
    }

    public BlockStateSnapshot getUpdatedOrCustom(NbtMap state, NbtMap updated) {
        BlockStateSnapshot blockState = this.getStateUnsafe(updated);
        if (blockState != null) {
            return blockState;
        }

        return BlockStateSnapshot.builder()
                .vanillaState(state)
                .runtimeId(this.getDefaultState().getRuntimeId())
                .version(this.version)
                .custom(true)
                .build();
    }

    public void setLegacyMapper(LegacyStateMapper legacyMapper) {
        this.legacyMapper = legacyMapper;
    }

    public LegacyStateMapper getLegacyMapper() {
        return this.legacyMapper;
    }

    public int getVersion() {
        return this.version;
    }
}
