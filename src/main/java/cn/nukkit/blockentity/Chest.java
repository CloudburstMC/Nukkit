package cn.nukkit.blockentity;

import cn.nukkit.blockentity.impl.ChestBlockEntity;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.InventoryHolder;

public interface Chest extends BlockEntity, InventoryHolder {

    boolean isFindable();

    void setFindable(boolean findable);

    @Override
    ContainerInventory getInventory();

    boolean isPaired();

    ChestBlockEntity getPair();

    boolean pairWith(Chest chest);

    boolean unpair();
}
