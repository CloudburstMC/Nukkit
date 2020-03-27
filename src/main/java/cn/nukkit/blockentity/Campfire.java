package cn.nukkit.blockentity;

import cn.nukkit.item.Item;

public interface Campfire extends BlockEntity {

    boolean putItemInFire(Item item);

    default boolean putItemInFire(Item item, int index) {
        return putItemInFire(item, index, false);
    }

    boolean putItemInFire(Item item, int index, boolean overwrite);
}
