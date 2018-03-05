package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class PlayerBlockPickEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Block blockClicked;
    private ItemInstance item;
    private boolean cancelled;

    public PlayerBlockPickEvent(Player player, Block blockClicked, ItemInstance item) {
        this.player = player;
        this.blockClicked = blockClicked;
        this.item = item;
    }

    public ItemInstance getItem() {
        return item;
    }

    public void setItem(ItemInstance item) {
        this.item = item;
    }

    public Block getBlockClicked() {
        return blockClicked;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
