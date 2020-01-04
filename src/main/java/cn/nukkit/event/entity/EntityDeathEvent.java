package cn.nukkit.event.entity;

import cn.nukkit.entity.LivingEntity;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDeathEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item[] drops;

    public EntityDeathEvent(LivingEntity entity) {
        this(entity, new Item[0]);
    }

    public EntityDeathEvent(LivingEntity entity, Item[] drops) {
        this.entity = entity;
        this.drops = drops;
    }

    public Item[] getDrops() {
        return drops;
    }

    public void setDrops(Item[] drops) {
        if (drops == null) {
            drops = new Item[0];
        }

        this.drops = drops;
    }
}
