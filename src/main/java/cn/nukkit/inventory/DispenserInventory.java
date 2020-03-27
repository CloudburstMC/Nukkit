package cn.nukkit.inventory;

import cn.nukkit.blockentity.Dispenser;

public class DispenserInventory extends ContainerInventory {

    public DispenserInventory(Dispenser blockEntity) {
        super(blockEntity, InventoryType.DISPENSER);
    }

    @Override
    public Dispenser getHolder() {
        return (Dispenser) super.getHolder();
    }
}
