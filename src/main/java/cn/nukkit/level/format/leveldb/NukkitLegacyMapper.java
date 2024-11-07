package cn.nukkit.level.format.leveldb;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;

import java.io.InputStream;
import java.util.List;

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
        return GlobalBlockPalette.getLeveldbBlockPalette().getLegacyFullId(runtimeId);
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
