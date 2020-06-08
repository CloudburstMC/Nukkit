package cn.nukkit.event.block;

import cn.nukkit.block.BlockBell;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BellRingEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final RingCause cause;
    private final Entity entity;

    public BellRingEvent(BlockBell bell, RingCause cause, Entity entity) {
        super(bell);
        this.cause = cause;
        this.entity = entity;
    }

    @Override
    public BlockBell getBlock() {
        return (BlockBell) super.getBlock();
    }

    public Entity getEntity() {
        return entity;
    }

    public RingCause getCause() {
        return cause;
    }

    public enum RingCause {
        HUMAN_INTERACTION,
        REDSTONE,
        PROJECTILE,
        DROPPED_ITEM,
        UNKNOWN
    }

}
