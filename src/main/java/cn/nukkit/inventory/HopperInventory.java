package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityHopper;

/**
 * Created by CreeperFace on 8.5.2017.
 */
public class HopperInventory extends ContainerInventory {

    public HopperInventory(BlockEntityHopper hopper) {
        super(hopper, InventoryType.HOPPER);
    }

    @Override
    public BlockEntityHopper getHolder() {
        return (BlockEntityHopper) super.getHolder();
    }
}
