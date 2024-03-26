package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityDropper;

public class DropperInventory extends ContainerInventory {

    public DropperInventory(BlockEntityDropper dropper) {
        super(dropper, InventoryType.DROPPER);
    }

    @Override
    public BlockEntityDropper getHolder() {
        return (BlockEntityDropper) super.getHolder();
    }
}
