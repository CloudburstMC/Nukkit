package cn.nukkit.inventory;

import cn.nukkit.entity.item.EntityMinecartHopper;

public class MinecartHopperInventory extends ContainerInventory {

    public MinecartHopperInventory(EntityMinecartHopper minecart) {
        super(minecart, InventoryType.MINECART_HOPPER);
    }

    @Override
    public EntityMinecartHopper getHolder() {
        return (EntityMinecartHopper) super.getHolder();
    }
}
