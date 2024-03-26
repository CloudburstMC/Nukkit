package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Event for Block fading.
 */
public class BlockFadeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block newState;

    /**
     * Currently used when:
     * - Snow melts because of block light.
     * - Ice melts because of block light.
     * - Glowing redstone ore turns to normal redstone ore.
     * @param block Block that has faded/melted.
     * @param newState New state of the block.
     */
    public BlockFadeEvent(Block block, Block newState) {
        super(block);
        this.newState = newState;
    }

    public Block getNewState() {
        return newState;
    }
}
