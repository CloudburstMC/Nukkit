package cn.nukkit.inventory;

import cn.nukkit.blockentity.Hopper;

/**
 * Created by CreeperFace on 8.5.2017.
 */
public class HopperInventory extends ContainerInventory {

    public HopperInventory(Hopper hopper) {
        super(hopper, InventoryType.HOPPER);
    }

    @Override
    public Hopper getHolder() {
        return (Hopper) super.getHolder();
    }
}
