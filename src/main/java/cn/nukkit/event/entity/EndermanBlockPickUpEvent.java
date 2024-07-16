package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.mob.EntityEnderman;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class EndermanBlockPickUpEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block block;

    public EndermanBlockPickUpEvent(EntityEnderman entity, Block block) {
        this.entity = entity;
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public EntityEnderman getEntity() {
        return (EntityEnderman) super.getEntity();
    }
}
