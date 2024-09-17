package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityDropper;
import cn.nukkit.item.Item;

public class DropperInventory extends ContainerInventory {

    public DropperInventory(BlockEntityDropper dropper) {
        super(dropper, InventoryType.DROPPER);
    }

    @Override
    public BlockEntityDropper getHolder() {
        return (BlockEntityDropper) super.getHolder();
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().chunk.setChanged();
    }
}
