package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityHopper;

/**
 * @author CreeperFace
 * @since 8.5.2017
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
