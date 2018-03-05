package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class PlayerGlassBottleFillEvent implements PlayerEvent, Cancellable {
    protected final ItemInstance item;
    private final Player player;
    protected final Block target;
    private boolean cancelled;

    public PlayerGlassBottleFillEvent(Player player, Block target, ItemInstance item) {
        this.player = player;
        this.target = target;
        this.item = item;
    }

    public ItemInstance getItem() {
        return item;
    }

    public Block getBlock() {
        return target;
    }

    public Block getTarget() {
        return target;
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
