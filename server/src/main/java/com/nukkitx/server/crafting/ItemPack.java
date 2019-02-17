package com.nukkitx.server.crafting;

import com.google.common.base.Preconditions;
import com.nukkitx.api.item.RecipeItemStack;
import com.nukkitx.server.crafting.recipe.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ItemPack {
    private final RecipeItemStack[] items;

    private ItemPack(RecipeItemStack[] items) {
        this.items = items;
    }

    public RecipeItemStack[] getItems() {
        return items;
    }

    public static class Builder {
        private final List<RecipeItemStack> items = new ArrayList<>();
        private Recipe.Type type;

        private Builder(RecipeItemStack[] items) {
            Collections.addAll(this.items, items);
        }

        public Builder() {
        }

        public List<RecipeItemStack> getItems() {
            return items;
        }

        public Builder setRecipeType(Recipe.Type type) {
            Preconditions.checkNotNull(type, "type");
            this.type = type;
            return this;
        }

        public ItemPack build() {
            Preconditions.checkArgument(items.isEmpty(), "Items cannot be empty");
            return new ItemPack(items.toArray(new RecipeItemStack[0]));
        }
    }
}
