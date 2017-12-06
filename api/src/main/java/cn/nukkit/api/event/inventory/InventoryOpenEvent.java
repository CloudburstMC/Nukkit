package cn.nukkit.api.event.inventory;

import cn.nukkit.api.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.inventory.Inventory;

/**
 * author: Box
 * Nukkit Project
 */
public class InventoryOpenEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player who;

    public InventoryOpenEvent(Inventory inventory, Player who) {
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