package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.EnchantingTable;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantingTableBlockEntity extends BaseBlockEntity implements EnchantingTable {

    public EnchantingTableBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public boolean isValid() {
        return getBlock().getId() == BlockIds.ENCHANTING_TABLE;
    }

}
