package com.nukkitx.server.crafting;

import com.google.common.base.Preconditions;
import com.nukkitx.api.container.CraftingContainer;
import com.nukkitx.server.crafting.recipe.Recipe;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecipeManager<T extends Recipe> {
    private final TIntObjectMap<List<T>> recipes = new TIntObjectHashMap<>();

    private void registerRecipe(T recipe) {
        Preconditions.checkNotNull(recipe, "recipe");
        int key = recipe.hashCode();

        List<T> recipeList = recipes.get(key);
        if (recipeList == null) {
            recipes.put(key, recipeList = new CopyOnWriteArrayList<>());
        } else if (recipeList.contains(recipe)) {
            throw new IllegalArgumentException("Recipe already registered");
        }
        recipeList.add(recipe);
    }

    public List<T> getRecipes() {
        List<T> recipes = new ArrayList<>();
        this.recipes.forEachValue(list -> {
            recipes.addAll(list);
            return true;
        });
        return recipes;
    }

    public Optional<T> matchRecipe(CraftingContainer container) {
        int key = container.hashCode();
        List<T> recipeList;
        if ((recipeList = recipes.get(key)) == null) {
            return Optional.empty();
        }

        for (T recipe : recipeList) {
            if (recipe.matchesItems(container)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }
}
