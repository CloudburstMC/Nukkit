package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWall;
import cn.nukkit.block.custom.properties.BlockProperties;
import cn.nukkit.block.properties.VanillaProperties;
import cn.nukkit.block.properties.WallConnectionType;
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

    /**
     * The shipped palettes only contain the default state of each wall, the connection states are derived from it.
     * Vanilla sorts them by west, south, east, north and finally the post bit, the last one changing first.
     */
    private static void registerWallStates(BlockPalette blockPalette) {
        WallConnectionType[] connections = WallConnectionType.values();

        for (int id = 0; id < Block.MAX_BLOCK_ID; id++) {
            Block block = Block.getPrototype(id, 0);
            if (!(block instanceof BlockWall)) {
                continue;
            }

            BlockProperties properties = ((BlockWall) block).getBlockProperties();
            boolean typed = properties.contains(BlockWall.WALL_TYPE);
            int typeCount = typed ? BlockWall.WallType.values().length : 1;

            for (int type = 0; type < typeCount; type++) {
                int typeMeta = typed
                        ? properties.setValue(0, BlockWall.WALL_TYPE.getName(), BlockWall.WallType.values()[type])
                        : 0;
                int runtimeId = blockPalette.getRuntimeId(id, typeMeta);

                for (WallConnectionType west : connections) {
                    int westMeta = properties.setValue(typeMeta, VanillaProperties.WALL_CONNECTION_TYPE_WEST.getName(), west);
                    for (WallConnectionType south : connections) {
                        int southMeta = properties.setValue(westMeta, VanillaProperties.WALL_CONNECTION_TYPE_SOUTH.getName(), south);
                        for (WallConnectionType east : connections) {
                            int eastMeta = properties.setValue(southMeta, VanillaProperties.WALL_CONNECTION_TYPE_EAST.getName(), east);
                            for (WallConnectionType north : connections) {
                                int northMeta = properties.setValue(eastMeta, VanillaProperties.WALL_CONNECTION_TYPE_NORTH.getName(), north);
                                blockPalette.registerState(id, northMeta, runtimeId++);
                                blockPalette.registerState(id,
                                        properties.setBooleanValue(northMeta, VanillaProperties.WALL_POST.getName(), true), runtimeId++);
                            }
                        }
                    }
                }
            }
        }
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

        registerWallStates(blockPalette);

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
