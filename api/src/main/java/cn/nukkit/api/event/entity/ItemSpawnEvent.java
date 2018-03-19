package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.misc.DroppedItem;

public class ItemSpawnEvent implements EntityEvent {
    private final DroppedItem item;

    public ItemSpawnEvent(DroppedItem item) {
        this.item = item;
    }

    @Override
    public DroppedItem getEntity() {
        return item;
    }
}
