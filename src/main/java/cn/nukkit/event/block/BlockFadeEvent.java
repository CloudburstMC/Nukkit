package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BlockFadeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block newBlock;

    public BlockFadeEvent(Block block, Block newBlock) {
        super(block);
        this.newBlock = newBlock;
    }

    public Block getNewBlock() {
        return newBlock;
    }
}
