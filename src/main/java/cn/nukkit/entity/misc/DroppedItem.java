package cn.nukkit.entity.misc;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface DroppedItem extends Entity {

    Item getItem();

    void setItem(@Nonnull Item item);

    @Nonnegative
    int getPickupDelay();

    void setPickupDelay(@Nonnegative int pickupDelay);
}
