package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerItemHeldEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item item;
    private final int slot;
    private final int inventorySlot;

    public PlayerItemHeldEvent(Player player, Item item, int inventorySlot, int slot) {
        this.player = player;
        this.item = item;
        this.inventorySlot = inventorySlot;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public Item getItem() {
        return item;
    }

}
