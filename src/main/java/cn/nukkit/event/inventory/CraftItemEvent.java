package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftItemEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private CraftingTransaction transaction;

    public CraftItemEvent(CraftingTransaction transaction) {
        this.transaction = transaction;
    }

    public CraftingTransaction getTransaction() {
        return transaction;
    }

    public Item[] getInput() {
        List<Item> merged = new ArrayList<>();
        Item[][] input = transaction.getInputMap();

        for (Item[] items : input) {
            merged.addAll(Arrays.asList(items));
        }

        return merged.stream().toArray(Item[]::new);
    }

    public Recipe getRecipe() {
        return transaction.getRecipe();
    }

    public Player getPlayer() {
        return transaction.getSource();
    }
}