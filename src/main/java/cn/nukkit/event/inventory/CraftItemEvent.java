package cn.nukkit.event.inventory;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.CraftingTransactionGroup;
import cn.nukkit.inventory.Recipe;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftItemEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private CraftingTransactionGroup ts;

    private Recipe recipe;

    public CraftItemEvent(CraftingTransactionGroup ts, Recipe recipe) {
        this.ts = ts;
        this.recipe = recipe;
    }

    public CraftingTransactionGroup getTransaction() {
        return this.ts;
    }

}