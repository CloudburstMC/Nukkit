package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.blockentity.BlockEntityDispenser;

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends EjectableInventory only in PowerNukkit")
public class DispenserInventory extends EjectableInventory {

    public DispenserInventory(BlockEntityDispenser blockEntity) {
        super(blockEntity, InventoryType.DISPENSER);
    }

    @Override
    public BlockEntityDispenser getHolder() {
        return (BlockEntityDispenser) super.getHolder();
    }
}
