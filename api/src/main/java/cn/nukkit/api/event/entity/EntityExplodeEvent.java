package cn.nukkit.api.event.entity;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;
import com.flowpowered.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EntityExplodeEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final Vector3f position;
    private final List<Block> blocks = new ArrayList<>();
    private float yield;
    private boolean cancelled;

    public EntityExplodeEvent(Entity entity, Vector3f position, List<Block> blocks, float yield) {
        this.entity = entity;
        this.position = position;
        this.blocks.addAll(blocks);
        this.yield = yield;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public float getYield() {
        return this.yield;
    }

    public void setYield(float yield) {
        this.yield = yield;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    public List<Block> getBlocks() {
        return blocks;
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
