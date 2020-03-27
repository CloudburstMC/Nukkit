package cn.nukkit.blockentity.impl;

import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Dropper;
import cn.nukkit.inventory.DropperInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;

public class DropperBlockEntity extends DispenserBlockEntity implements Dropper {

    private final DropperInventory inventory = new DropperInventory(this);

    public DropperBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
