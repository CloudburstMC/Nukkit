package cn.nukkit.blockentity;

import cn.nukkit.inventory.ContainerInventory;

public interface Chest extends BlockEntity, ContainerBlockEntity {

    boolean isFindable();

    void setFindable(boolean findable);

    @Override
    ContainerInventory getInventory();

    boolean isPaired();

    Chest getPair();

    boolean pairWith(Chest chest);

    boolean unpair();
}
