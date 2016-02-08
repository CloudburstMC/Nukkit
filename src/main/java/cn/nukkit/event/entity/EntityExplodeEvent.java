package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

import java.util.HashMap;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class EntityExplodeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected Position position;
    protected HashMap<String, Block> blocks;
    protected double yield;

    public EntityExplodeEvent(Entity entity, Position position, HashMap<String, Block> blocks, double yield) {
        this.entity = entity;
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
    }

    public Position getPosition() {
        return this.position;
    }

    public HashMap<String, Block> getBlockList() {
        return this.blocks;
    }

    public void setBlockList(HashMap<String, Block> blocks) {
        this.blocks = blocks;
    }

    public double getYield() {
        return this.yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

}
