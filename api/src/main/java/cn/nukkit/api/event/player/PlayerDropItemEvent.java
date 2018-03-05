package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class PlayerDropItemEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemInstance item;
    private boolean cancelled;

    public PlayerDropItemEvent(Player player, ItemInstance drop) {
        this.player = player;
        this.item = drop;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public ItemInstance getItem() {
        return item;
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
