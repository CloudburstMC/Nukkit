package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class EntityExplodeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Position position;
    protected List<Block> blocks;
    protected Set<Block> ignitions;
    protected double yield;

    public EntityExplodeEvent(Entity entity, Position position, List<Block> blocks, double yield) {
        this.entity = entity;
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
        this.ignitions = new HashSet<>(0);
    }

    public Position getPosition() {
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
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Set<Block> getIgnitions() {
        return ignitions;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setIgnitions(Set<Block> ignitions) {
        this.ignitions = ignitions;
    }

}
