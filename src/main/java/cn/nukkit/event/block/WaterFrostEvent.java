package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class WaterFrostEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    
    protected final Entity entity;

    public WaterFrostEvent(Block block, Entity entity) {
        super(block);
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return entity;
    }
}
