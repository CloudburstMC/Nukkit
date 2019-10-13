package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityDropper;

public class DropperInventory extends ContainerInventory {

    public DropperInventory(BlockEntityDropper blockEntity) {
        super(blockEntity, InventoryType.DROPPER);
    }

    @Override
    public BlockEntityDropper getHolder() {
        return (BlockEntityDropper) super.getHolder();
    }
}
