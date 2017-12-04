package cn.nukkit.server.event.entity;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.entity.Entity;

/**
 * author: Box
 * Nukkit Project
 */
public class EntityCombustByBlockEvent extends EntityCombustEvent {

    protected final Block combuster;

    public EntityCombustByBlockEvent(Block combuster, Entity combustee, int duration) {
        super(combustee, duration);
        this.combuster = combuster;
    }

    public Block getCombuster() {
        return combuster;
    }
}
