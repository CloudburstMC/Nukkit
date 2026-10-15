package cn.nukkit.level.format.leveldb;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.leveldb.structure.BlockStateSnapshot;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NukkitLegacyMapper implements LegacyStateMapper {

    public static void registerStates(BlockStateMapping mapping) {
        List<NbtMap> states = loadBlockPalette();
        for (int i = 0; i < states.size(); i++) {
            NbtMap state = states.get(i);
            if (state.containsKey("name_hash") || state.containsKey("network_id") || state.containsKey("block_id")) {
                NbtMapBuilder builder = NbtMapBuilder.from(state);
                builder.remove("name_hash");
                builder.remove("network_id");
                builder.remove("block_id");
                state = builder.build();
            }
            //noinspection ResultOfMethodCallIgnored
            state.hashCode(); // cache hashCode
            mapping.registerState(i, state);
        }
    }

    public static List<NbtMap> loadBlockPalette() {
        try (InputStream stream = NukkitLegacyMapper.class.getClassLoader().getResourceAsStream("block_palette_" + LevelDBConstants.PALETTE_VERSION + ".nbt")) {
            return ((NbtMap) NbtUtils.createGZIPReader(stream).readTag()).getList("blocks", NbtType.COMPOUND);
        } catch (Exception e) {
            throw new AssertionError("Error while loading leveldb block palette", e);
        }
    }

    @Override
    public int legacyToRuntime(int legacyId, int meta) {
        return GlobalBlockPalette.getLeveldbBlockPalette().getRuntimeId(legacyId, meta);
    }

    @Override
    public int runtimeToFullId(int runtimeId) {
        int fullId = GlobalBlockPalette.getLeveldbBlockPalette().getLegacyFullId(runtimeId);
        if (fullId == -1) {
            fullId = doorFullId(runtimeId);
        }
        return fullId != -1 ? fullId : siblingFullId(runtimeId);
    }

    private static final Map<String, Integer> DOOR_IDS = new ConcurrentHashMap<>();
    private static volatile Map<String, Integer> idsByName;

    /**
     * Other states of the shipped palette have no legacy block either, for example a sideways wood block. They
     * are given the block of a state of the same name, with the data of the default state, so that a wood block
     * of the right kind is loaded instead of the default block of the palette.
     */
    private static int siblingFullId(int runtimeId) {
        BlockStateSnapshot snapshot = BlockStateMapping.get().peekState(runtimeId);
        if (snapshot == null) {
            return -1;
        }

        Integer id = getIdsByName().get(snapshot.getVanillaState().getString("name"));
        return id == null ? -1 : id << Block.DATA_BITS;
    }

    private static Map<String, Integer> getIdsByName() {
        Map<String, Integer> ids = idsByName;
        if (ids == null) {
            ids = new ConcurrentHashMap<>();
            for (int runtimeId : BlockStateMapping.get().getRuntimeIds()) {
                int fullId = GlobalBlockPalette.getLeveldbBlockPalette().getLegacyFullId(runtimeId);
                BlockStateSnapshot snapshot = BlockStateMapping.get().peekState(runtimeId);
                if (fullId != -1 && snapshot != null) {
                    ids.putIfAbsent(snapshot.getVanillaState().getString("name"), fullId >> Block.DATA_BITS);
                }
            }
            idsByName = ids;
        }
        return ids;
    }

    /**
     * The shipped palette has no legacy block for part of the door states, for example the top half of a door that
     * faces south, so the door was loaded as the default block and showed up as a missing block. The door halves
     * are given the data the game gives them itself: the facing and the open bit for the bottom half, the
     * hinge for the top half.
     */
    private static int doorFullId(int runtimeId) {
        BlockStateSnapshot snapshot = BlockStateMapping.get().peekState(runtimeId);
        if (snapshot == null) {
            return -1;
        }

        NbtMap vanilla = snapshot.getVanillaState();
        String name = vanilla.getString("name");
        NbtMap states = vanilla.getCompound("states");
        if (!name.endsWith("_door") || !states.containsKey("direction") || !states.containsKey("upper_block_bit")) {
            return -1;
        }

        Integer id = DOOR_IDS.computeIfAbsent(name, key -> {
            NbtMapBuilder base = states.toBuilder();
            base.putInt("direction", 0);
            base.putByte("door_hinge_bit", (byte) 0);
            base.putByte("open_bit", (byte) 0);
            base.putByte("upper_block_bit", (byte) 0);
            BlockStateSnapshot lower = BlockStateMapping.get().getStateUnsafe(vanilla.toBuilder().putCompound("states", base.build()).build());
            int baseFullId = lower == null ? -1 : GlobalBlockPalette.getLeveldbBlockPalette().getLegacyFullId(lower.getRuntimeId());
            return baseFullId == -1 ? -1 : baseFullId >> Block.DATA_BITS;
        });
        if (id == -1) {
            return -1;
        }

        boolean top = states.getByte("upper_block_bit") != 0;
        int data = top
                ? 0x08 | (states.getByte("door_hinge_bit") != 0 ? 0x01 : 0)
                : (states.getInt("direction") & 0x03) | (states.getByte("open_bit") != 0 ? 0x04 : 0);
        return id << Block.DATA_BITS | data;
    }

    @Override
    public int runtimeToLegacyId(int runtimeId) {
        int fullId = this.runtimeToFullId(runtimeId);
        return fullId == -1 ? -1 : fullId >> Block.DATA_BITS;
    }

    @Override
    public int runtimeToLegacyData(int runtimeId) {
        int fullId = this.runtimeToFullId(runtimeId);
        return fullId == -1 ? -1 : (fullId & Block.DATA_MASK);
    }
}
