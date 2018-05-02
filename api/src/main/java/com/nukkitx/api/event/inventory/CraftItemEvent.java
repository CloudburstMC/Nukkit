/*package com.nukkitx.api.event.inventory;

import com.nukkitx.api.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.inventory.Recipe;
import cn.nukkit.server.item.Item;

public class CraftItemEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Recipe recipe;
    private final Player player;
    private Item[] input = new Item[0];

    public CraftItemEvent(Player player, Item[] input, Recipe recipe) {
        this.player = player;
        this.input = input;
        this.recipe = recipe;
    }

    public static HandlerList getHandlers() {
        return handlers;
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
}*/