package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * Event for a block being placed.
 * @author MagicDroidX
 */
public class BlockPlaceEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Player player;

    protected final Item item;

    protected final Block blockReplace;
    protected final Block blockAgainst;

    /**
     * This event is called when a block is placed.
     * @param player Player who placed block.
     * @param blockPlace Placed Block.
     * @param blockReplace Block replace.
     * @param blockAgainst Block against.
     * @param item Item that was placed.
     */
    public BlockPlaceEvent(Player player, Block blockPlace, Block blockReplace, Block blockAgainst, Item item) {
        super(blockPlace);
        this.blockReplace = blockReplace;
        this.blockAgainst = blockAgainst;
        this.item = item;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public Block getBlockReplace() {
        return blockReplace;
    }

    public Block getBlockAgainst() {
        return blockAgainst;
    }
}
