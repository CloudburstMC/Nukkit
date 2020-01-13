package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityDispenser;

public class DispenserInventory extends ContainerInventory {

    public DispenserInventory(BlockEntityDispenser blockEntity) {
        super(blockEntity, InventoryType.DISPENSER);
    }

    @Override
    public BlockEntityDispenser getHolder() {
        return (BlockEntityDispenser) super.getHolder();
    }
}
