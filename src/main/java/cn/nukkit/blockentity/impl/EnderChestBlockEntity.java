package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.EnderChest;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;

public class EnderChestBlockEntity extends BaseBlockEntity implements EnderChest {

    public EnderChestBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public boolean isValid() {
        return this.getBlock().getId() == BlockIds.ENDER_CHEST;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
