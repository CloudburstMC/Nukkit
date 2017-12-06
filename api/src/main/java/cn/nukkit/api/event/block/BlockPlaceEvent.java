package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockPlaceEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected final Player player;
    protected final Item item;
    protected final Block blockReplace;
    protected final Block blockAgainst;
    public BlockPlaceEvent(Player player, Block blockPlace, Block blockReplace, Block blockAgainst, Item item) {
        super(blockPlace);
        this.blockReplace = blockReplace;
        this.blockAgainst = blockAgainst;
        this.item = item;
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
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
