package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;

/**
 * Created on 15-10-26.
 */
public class EntityBlockChangeEvent extends EntityEvent implements Cancellable {

    private final Block from;
    private final Block to;

    public EntityBlockChangeEvent(Entity entity, Block from, Block to) {
        this.entity = entity;
        this.from   = from;
        this.to     = to;
    }

    public Block getFrom() {
        return from;
    }

    public Block getTo() {
        return to;
    }

}
