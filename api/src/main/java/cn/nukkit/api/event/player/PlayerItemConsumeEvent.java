package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

/**
 * Called when a player eats something
 */
public class PlayerItemConsumeEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemInstance item;
    private boolean cancelled;

    public PlayerItemConsumeEvent(Player player, ItemInstance item) {
        this.player = player;
        this.item = item;
    }

    public ItemInstance getItem() {
        return item;
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
