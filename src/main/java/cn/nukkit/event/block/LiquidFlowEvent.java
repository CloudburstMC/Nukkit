package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Event for liquid flow.
 */
public class LiquidFlowEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block to;
    private final BlockLiquid source;
    private final int newFlowDecay;
    /**
     * Event for liquid flowing (water/lava).
     * @param to Flowing from one place to another.
     * @param source Source of liquid flow.
     * @param newFlowDecay Number for when water stops flowing.
     */
    public LiquidFlowEvent(Block to, BlockLiquid source, int newFlowDecay) {
        super(to);
        this.to = to;
        this.source = source;
        this.newFlowDecay = newFlowDecay;
    }

    public int getNewFlowDecay() {
        return this.newFlowDecay;
    }

    public BlockLiquid getSource() {
        return this.source;
    }

    public Block getTo() {
        return this.to;
    }
}
