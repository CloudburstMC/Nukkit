package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

import java.util.List;

public class BlockExplodeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Position position;
    protected List<Block> blocks;
    protected double yield;

    /**
     * Block explode event is called when a block explodes (For example a bed in nether)
     * @param block Block that exploded
     * @param position Position
     * @param blocks Blocks affected by the explosion
     * @param yield Explosion yield
     */
    public BlockExplodeEvent(Block block, Position position, List<Block> blocks, double yield) {
        super(block);
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
