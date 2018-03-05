package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

import java.util.ArrayList;
import java.util.Collection;

public class BlockBreakEvent implements BlockEvent, Cancellable {

    protected final ItemInstance item;
    protected final Player player;
    private final Block block;
    protected boolean instaBreak;
    protected Collection<ItemInstance> drops;
    private boolean fastBreak;
    private boolean cancelled;

    public BlockBreakEvent(Player player, Block block, Collection<ItemInstance> drops, ItemInstance item, boolean instaBreak, boolean fastBreak) {
        this.block = block;
        this.item = item;
        this.player = player;
        this.instaBreak = instaBreak;
        this.drops = new ArrayList<>(drops);
        this.fastBreak = fastBreak;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemInstance getItem() {
        return item;
    }

    public boolean getInstaBreak() {
        return this.instaBreak;
    }

    public void setInstaBreak(boolean instaBreak) {
        this.instaBreak = instaBreak;
    }

    public Collection<ItemInstance> getDrops() {
        return drops;
    }

    public boolean isFastBreak() {
        return this.fastBreak;
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
        return block;
    }
}
