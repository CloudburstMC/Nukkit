package cn.nukkit.api.event.entity;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;

public class EntityInteractEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final Block block;
    private boolean cancelled;

    public EntityInteractEvent(Entity entity, Block block) {
        this.entity = entity;
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
