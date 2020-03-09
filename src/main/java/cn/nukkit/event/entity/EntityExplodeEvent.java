package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import com.nukkitx.math.vector.Vector3f;

import java.util.List;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class EntityExplodeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Vector3f position;
    protected List<Block> blocks;
    protected double yield;

    public EntityExplodeEvent(Entity entity, Vector3f position, List<Block> blocks, double yield) {
        this.entity = entity;
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public List<Block> getBlockList() {
        return this.blocks;
    }

    public void setBlockList(List<Block> blocks) {
        this.blocks = blocks;
    }

    public double getYield() {
        return this.yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

}
