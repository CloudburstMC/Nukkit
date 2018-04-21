package cn.nukkit.server.level.provider;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.metadata.block.Liquid;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.chunk.ChunkSection;
import cn.nukkit.server.level.util.BlockStorage;
import cn.nukkit.server.level.util.NibbleArray;
import gnu.trove.list.array.TIntArrayList;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class LegacyChunkConverter {

    public static ChunkSection convertFromLegacy(byte[] blockIds, byte[] blockData, byte[] skyLight, byte[] blockLight) {
        BlockStorage[] storage = new BlockStorage[]{new BlockStorage(), new BlockStorage()};
        NibbleArray data = new NibbleArray(blockData);
        TIntArrayList runtimeIds = new TIntArrayList();
        for (int i = 0; i < 4096; i++) {
            int runtimeId = NukkitLevel.getPaletteManager().fromLegacy(blockIds[i],  data.get(i));

            Optional<BlockState> state = NukkitLevel.getPaletteManager().getBlockState(runtimeId);
            if (state.isPresent() && Liquid.class.isAssignableFrom(state.get().getBlockType().getMetadataClass())) {
                storage[1].setBlockId(i, runtimeId);
            } else {
                storage[0].setBlockId(i, runtimeId);
            }
        }
        return new ChunkSection(storage, skyLight, blockLight);
    }
}
