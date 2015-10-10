package cn.nukkit.entity;

import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Human extends Creature implements InventoryHolder {
    public Human(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return -1;
    }
    //todo alot alot alot !!! :\
}
