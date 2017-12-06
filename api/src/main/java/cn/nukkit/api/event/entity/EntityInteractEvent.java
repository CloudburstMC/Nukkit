package cn.nukkit.api.event.entity;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

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
