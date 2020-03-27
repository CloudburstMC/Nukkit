package cn.nukkit.inventory;

import cn.nukkit.blockentity.Dropper;

public class DropperInventory extends ContainerInventory {

    public DropperInventory(Dropper blockEntity) {
        super(blockEntity, InventoryType.DROPPER);
    }

    @Override
    public Dropper getHolder() {
        return (Dropper) super.getHolder();
    }
}
