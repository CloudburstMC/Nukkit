package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class BlockPlaceEvent implements BlockEvent, Cancellable {
    private final Player player;
    private final ItemInstance item;
    private final Block blockPlace;
    private final Block blockReplace;
    private final Block blockAgainst;
    private boolean cancelled;

    public BlockPlaceEvent(Player player, Block blockPlace, Block blockReplace, Block blockAgainst, ItemInstance item) {
        this.blockPlace = blockPlace;
        this.blockReplace = blockReplace;
        this.blockAgainst = blockAgainst;
        this.item = item;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemInstance getItem() {
        return item;
    }

    public Block getBlockReplace() {
        return blockReplace;
    }

    public Block getBlockAgainst() {
        return blockAgainst;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Block getBlock() {
        return blockPlace;
    }
}
