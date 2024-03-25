package cn.nukkit.event.level;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

import java.util.List;

/**
 * author: SergeyDertan
 * Nukkit Project
 */
public class ExplodeEvent extends LevelEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final Position position;
    protected List<Block> blocks;
    protected double yield;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ExplodeEvent(Level level, Position position, List<Block> blocks, double yield) {
        super(level);
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
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
}
