package com.nukkitx.api.event.entity;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.item.ItemStack;

public class EntityDeathEvent implements EntityEvent {
    private final Entity entity;
    private ItemStack[] drops = new ItemStack[0];

    public EntityDeathEvent(Entity entity) {
        this(entity, new ItemStack[0]);
    }

    public EntityDeathEvent(Entity entity, ItemStack[] drops) {
        this.entity = entity;
        this.drops = drops;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    public ItemStack[] getDrops() {
        return drops;
    }

    public void setDrops(ItemStack[] drops) {
        this.drops = drops;
    }
}
