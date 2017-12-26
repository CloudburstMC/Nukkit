package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemStack;
import lombok.Getter;
import lombok.Setter;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

@Getter
public class BlockBreakEvent extends BlockEvent implements Cancellable {

    @Setter
    private boolean cancelled;

    protected final Player player;
    protected final ItemStack item;
    protected boolean instaBreak = false;
    protected ItemStack[] blockDrops = new ItemStack[][0];
    protected boolean fastBreak = false;

    public BlockBreakEvent(Player player, Block block, ItemStack item) {
        this(player, block, item, false, false);
    }

    public BlockBreakEvent(Player player, Block block, ItemStack item, boolean instaBreak) {
        this(player, block, item, instaBreak, false);
    }

    public BlockBreakEvent(Player player, Block block, ItemStack item, boolean instaBreak, boolean fastBreak) {
        super(block);
        this.item = item;
        this.player = player;
        this.instaBreak = instaBreak;
        this.blockDrops = player.isSurvival() ? block.getDrops(item) : new ItemStack[][ 0];
        this.fastBreak = fastBreak;
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

    public boolean getInstaBreak() {
        return this.instaBreak;
    }

    public void setInstaBreak(boolean instaBreak) {
        this.instaBreak = instaBreak;
    }

    public Item[] getDrops() {
        return blockDrops;
    }

    public void setDrops(Item[] drops) {
        this.blockDrops = drops;
    }

    public boolean isFastBreak() {
        return this.fastBreak;
    }
}
