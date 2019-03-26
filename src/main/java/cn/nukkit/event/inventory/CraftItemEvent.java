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

    private Item[] input = new Item[0];

    private final Recipe recipe;

    private final Player player;

    private CraftingTransaction transaction;

    public CraftItemEvent(CraftingTransaction transaction) {
        this.transaction = transaction;

        List<Item> merged = new ArrayList<>();
        Item[][] input = transaction.getInputMap();

        for (Item[] items : input) {
            merged.addAll(Arrays.asList(items));
        }
        this.player = transaction.getSource();
        this.input = merged.toArray(new Item[0]);
        this.recipe = transaction.getRecipe();
    }

    public CraftItemEvent(Player player, Item[] input, Recipe recipe) {
        this.player = player;
        this.input = input;
        this.recipe = recipe;
    }

    public CraftingTransaction getTransaction() {
        return transaction;
    }

    public Item[] getInput() {
        return input;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Player getPlayer() {
        return this.player;
    }
}