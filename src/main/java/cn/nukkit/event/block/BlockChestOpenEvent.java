package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.inventory.ChestInventory;

public class BlockChestOpenEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private ChestInventory inventory;

    public BlockChestOpenEvent(Player player, ChestInventory inventory) {
        this.player = player;
        this.inventory = inventory;
    }

    public ChestInventory getInventory() {
        return inventory;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}