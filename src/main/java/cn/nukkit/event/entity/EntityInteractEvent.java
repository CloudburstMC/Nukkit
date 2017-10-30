package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * @author CreeperFace
 */
public class EntityInteractEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Block block;

    public EntityInteractEvent(Entity entity, Block block) {
        this.entity = entity;
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
