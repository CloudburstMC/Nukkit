package cn.nukkit.level.provider.leveldb;

import cn.nukkit.level.chunk.BlockStorage;
import cn.nukkit.utils.NibbleArray;

public class BlockStorageConverter {

    public static BlockStorage fromXZY(byte[] blockIds, byte[] blockData) {
        NibbleArray data = new NibbleArray(blockData);

        BlockStorage storage = new BlockStorage();

        for (int i = 0; i < 4096; i++) {
            int legacyId = (blockIds[i] << 4) | data.get(i);
            storage.setFullBlock(i, legacyId);
        }

        return storage;
    }
}
