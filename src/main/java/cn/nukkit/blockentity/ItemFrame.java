package cn.nukkit.blockentity;

import cn.nukkit.item.Item;

public interface ItemFrame extends BlockEntity {

    int getItemRotation();

    void setItemRotation(int itemRotation);

    Item getItem();

    void setItem(Item item);

    float getItemDropChance();

    void setItemDropChance(float itemDropChance);

    int getAnalogOutput();
}
