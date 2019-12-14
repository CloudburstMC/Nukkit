package cn.nukkit.level.provider.leveldb;

import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.chunk.BlockStorage;
import cn.nukkit.utils.NibbleArray;

public class BlockStorageConverter {

    public static BlockStorage fromXZY(byte[] blockIds, byte[] blockData) {
        NibbleArray data = new NibbleArray(blockData);

        BlockStorage storage = new BlockStorage();

        for (int i = 0; i < 4096; i++) {
            storage.setBlock(i, GlobalBlockPalette.getBlock(blockIds[i], data.get(i)));
        }

        return storage;
    }
}
