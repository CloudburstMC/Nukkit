package com.nukkitx.server.crafting.recipe;

public interface MultiRecipe extends Recipe {

    boolean isShapeless();

    @Override
    default boolean isMultiRecipe() {
        return true;
    }
}
