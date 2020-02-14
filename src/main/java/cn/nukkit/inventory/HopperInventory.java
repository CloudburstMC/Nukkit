package cn.nukkit.inventory;

import cn.nukkit.blockentity.impl.HopperBlockEntity;

/**
 * Created by CreeperFace on 8.5.2017.
 */
public class HopperInventory extends ContainerInventory {

    public HopperInventory(HopperBlockEntity hopper) {
        super(hopper, InventoryType.HOPPER);
    }

    @Override
    public HopperBlockEntity getHolder() {
        return (HopperBlockEntity) super.getHolder();
    }
}
