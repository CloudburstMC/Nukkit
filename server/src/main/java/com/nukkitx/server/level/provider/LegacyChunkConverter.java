package com.nukkitx.server.level.provider;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.metadata.block.Liquid;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.level.chunk.ChunkSection;
import com.nukkitx.server.level.util.BlockStorage;
import com.nukkitx.server.level.util.NibbleArray;
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
