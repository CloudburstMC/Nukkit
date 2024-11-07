package cn.nukkit.level;

import cn.nukkit.level.format.leveldb.LevelDBConstants;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
public class GlobalBlockPalette {

    private static boolean initialized;

    @Getter
    private static final BlockPalette currentBlockPalette = new BlockPalette(ProtocolInfo.CURRENT_PROTOCOL);
    @Getter
    private static final BlockPalette leveldbBlockPalette = new BlockPalette(LevelDBConstants.PALETTE_VERSION);
    
    public static void init() {
        if (initialized) {
            throw new IllegalStateException("GlobalBlockPalette was already generated!");
        }
        initialized = true;
        log.debug("Loading block palette...");

        //noinspection unchecked
        loadBlockStates((ListTag<CompoundTag>) Utils.loadTagResource("runtime_block_states.dat"), getCurrentBlockPalette());
        //noinspection unchecked
        loadBlockStates((ListTag<CompoundTag>) Utils.loadTagResource("runtime_block_states_" + LevelDBConstants.PALETTE_VERSION + ".dat"), getLeveldbBlockPalette());
    }

    private static void loadBlockStates(ListTag<CompoundTag> blockStates, BlockPalette blockPalette) {
        List<CompoundTag> stateOverloads = new ObjectArrayList<>();
        for (CompoundTag state : blockStates.getAll()) {
            if (!registerBlockState(blockPalette, state, false)) {
                stateOverloads.add(state);
            }
        }

        for (CompoundTag state : stateOverloads) {
            log.debug("[{}] Registering block palette overload: {}", blockPalette.getProtocol(), state.getString("name"));
            registerBlockState(blockPalette, state, true);
        }

        blockPalette.lock(); // prevent adding new states
    }

    private static boolean registerBlockState(BlockPalette blockPalette, CompoundTag state, boolean force) {
        int id = state.getInt("id");
        int data = state.getShort("data");
        int runtimeId = state.getInt("runtimeId");
        boolean stateOverload = state.getBoolean("stateOverload");

        if (stateOverload && !force) {
            return false;
        }

        CompoundTag vanillaState = state
                .remove("id")
                .remove("data")
                .remove("runtimeId")
                .remove("stateOverload");
        blockPalette.registerState(id, data, runtimeId, vanillaState);
        return true;
    }

    public static int getOrCreateRuntimeId(int id, int meta) {
        return getCurrentBlockPalette().getRuntimeId(id, meta);
    }

    public static int getOrCreateRuntimeId(int legacyId) throws NoSuchElementException {
        return getCurrentBlockPalette().getRuntimeId(legacyId);
    }

    public static int getLegacyFullId(int runtimeId) {
       return getCurrentBlockPalette().getLegacyFullId(runtimeId);
    }
}
