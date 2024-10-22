package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityDispenser;
import cn.nukkit.item.Item;

public class DispenserInventory extends ContainerInventory {

    public DispenserInventory(BlockEntityDispenser dispenser) {
        super(dispenser, InventoryType.DISPENSER);
    }

    @Override
    public BlockEntityDispenser getHolder() {
        return (BlockEntityDispenser) super.getHolder();
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().chunk.setChanged();
    }
}
