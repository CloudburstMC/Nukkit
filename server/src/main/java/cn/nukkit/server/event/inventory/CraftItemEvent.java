package cn.nukkit.server.event.inventory;

import cn.nukkit.server.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.Event;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.inventory.Recipe;
import cn.nukkit.server.item.Item;

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

    public CraftItemEvent(Player player, Item[] input, Recipe recipe) {
        this.player = player;
        this.input = input;
        this.recipe = recipe;
    }

    public Item[] getInput() {
        Item[] items = new Item[this.input.length];
        for (int i = 0; i < this.input.length; i++) {
            items[i] = this.input[i].clone();
        }

        return items;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Player getPlayer() {
        return player;
    }
}