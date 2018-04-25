package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class BlockPlaceEvent implements BlockEvent, Cancellable {
    private final Player player;
    private final ItemInstance item;
    private final Block oldBlock;
    private final Block against;
    private final BlockState newBlockState;
    private boolean cancelled;

    public BlockPlaceEvent(Player player, Block against, Block oldBlock, BlockState newBlockState, ItemInstance item) {
        this.player = player;
        this.against = against;
        this.oldBlock = oldBlock;
        this.newBlockState = newBlockState;
        this.item = item;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemInstance getItem() {
        return item;
    }

    public BlockState getNewBlockState() {
        return newBlockState;
    }

    public Block getAgainst() {
        return against;
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
        return oldBlock;
    }
}
