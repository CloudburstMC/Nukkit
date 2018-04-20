package cn.nukkit.server.level.provider;

import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.chunk.ChunkSection;
import cn.nukkit.server.level.util.NibbleArray;
import gnu.trove.list.array.TIntArrayList;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LegacyChunkConverter {

    public static ChunkSection convertFromLegacy(byte[] blockIds, byte[] blockData, byte[] skyLight, byte[] blockLight) {
        short[][] indexes = new short[2][4096];
        NibbleArray data = new NibbleArray(blockData);
        TIntArrayList runtimeIds = new TIntArrayList();
        for (int i = 0; i < 4096; i++) {
            int runtimeId = NukkitLevel.getPaletteManager().fromLegacy(blockIds[i] << 4,  data.get(i));

            if (!runtimeIds.contains(runtimeId)) {
                runtimeIds.add(runtimeId);
            }

            indexes[0][i] = (short) runtimeIds.indexOf(runtimeId);
        }
        return new ChunkSection(indexes, runtimeIds.toArray(), skyLight, blockLight);
    }
}
