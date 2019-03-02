package com.nukkitx.server.crafting;

import com.nukkitx.api.item.RecipeItemStackBuilder;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;
import com.nukkitx.server.crafting.recipe.ShapedRecipe;
import com.nukkitx.server.crafting.recipe.ShapelessRecipe;
import com.nukkitx.server.item.NukkitRecipeItemStackBuilder;

public class CraftingManager {
    private final RecipeManager<ShapedRecipe> shapedRecipeManager = new RecipeManager<>();
    private final RecipeManager<ShapelessRecipe> shapelessRecipeManager = new RecipeManager<>();

    private CraftingDataPacket cached = null;

    public RecipeItemStackBuilder createRecipeItemInstanceBuilder() {
        return new NukkitRecipeItemStackBuilder();
    }

    public CraftingDataPacket createCraftingDataPacket() {
        if (cached != null) {
            return cached;
        }

        CraftingDataPacket packet = new CraftingDataPacket();

        cached = packet;
        return packet;
    }
}
