package cn.nukkit.inventory;

import cn.nukkit.entity.item.EntityMinecartChest;

public class MinecartChestInventory extends ContainerInventory {

    public MinecartChestInventory(EntityMinecartChest minecart) {
        super(minecart, InventoryType.MINECART_CHEST);
    }

    @Override
    public EntityMinecartChest getHolder() {
        return (EntityMinecartChest) this.holder;
    }
}
