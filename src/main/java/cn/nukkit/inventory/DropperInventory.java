package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.blockentity.BlockEntityDropper;

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends EjectableInventory only in PowerNukkit")
public class DropperInventory extends EjectableInventory {

    public DropperInventory(BlockEntityDropper blockEntity) {
        super(blockEntity, InventoryType.DROPPER);
    }

    @Override
    public BlockEntityDropper getHolder() {
        return (BlockEntityDropper) super.getHolder();
    }
}
