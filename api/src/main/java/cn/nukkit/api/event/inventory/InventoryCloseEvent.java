package cn.nukkit.api.event.inventory;

import cn.nukkit.api.Player;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.inventory.Inventory;

/**
 * author: Box
 * Nukkit Project
 */
public class InventoryCloseEvent extends InventoryEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player who;

    public InventoryCloseEvent(Inventory inventory, Player who) {
        super(inventory);
        this.who = who;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return this.who;
    }
}
