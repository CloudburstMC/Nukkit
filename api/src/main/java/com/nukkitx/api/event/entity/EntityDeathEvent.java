package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.item.ItemInstance;

public class EntityDeathEvent implements EntityEvent {
    private final Entity entity;
    private ItemInstance[] drops = new ItemInstance[0];

    public EntityDeathEvent(Entity entity) {
        this(entity, new ItemInstance[0]);
    }

    public EntityDeathEvent(Entity entity, ItemInstance[] drops) {
        this.entity = entity;
        this.drops = drops;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    public ItemInstance[] getDrops() {
        return drops;
    }

    public void setDrops(ItemInstance[] drops) {
        this.drops = drops;
    }
}
