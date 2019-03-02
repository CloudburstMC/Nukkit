package com.nukkitx.server.crafting.recipe;

import com.google.common.collect.ImmutableList;
import com.nukkitx.api.container.CraftingContainer;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.RecipeItemStack;
import com.nukkitx.server.block.BlockUtils;
import com.nukkitx.server.metadata.MetadataSerializers;
import io.netty.buffer.ByteBuf;

import java.util.*;

public class ShapelessRecipe implements CraftingRecipe {
    private static final Comparator<ItemStack> ITEM_COMPARATOR = (item1, item2) -> {
        int result = Integer.compare(item1.getItemType().getId(), item2.getItemType().getId());
        if (result != 0) {
            return result;
        }
        return Integer.compare(MetadataSerializers.serializeMetadata(item1), MetadataSerializers.serializeMetadata(item2));
    };

    private final UUID uuid;
    private final List<RecipeItemStack> ingredients = new ArrayList<>();
    private final ItemStack result;

    private ShapelessRecipe(UUID uuid, Collection<RecipeItemStack> ingredients, ItemStack result) {
        this.uuid = uuid;
        this.ingredients.addAll(ingredients);
        this.ingredients.sort(ITEM_COMPARATOR);
        this.result = result;
    }

    @Override
    public boolean matchesItems(ItemStack[][] items) {
        List<ItemStack> inputs = new ArrayList<>();

        for (int x = 0; x < items.length; x++) {
            for (int y = 0; y < items.length; y++) {
                if (items[x][y] != null && !items[x][y].equals(BlockUtils.AIR)) {
                    inputs.add(items[x][y]);
                }
            }
        }

        inputs.sort(ITEM_COMPARATOR);

        if (!ingredients.equals(inputs)) {
            return false;
        }

        // Recipe matches. Now remove items used so that there is no item duping.

        for (int x = 0; x < items.length; x++) {
            for (int y = 0; y < items.length; y++) {
                for (ItemStack ingredient : inputs) {
                    if (items[x][y] == ingredient) {
                        ItemStack item = items[x][y];
                        items[x][y] = item.toBuilder().amount(item.getAmount() - ingredient.getAmount()).build();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public byte getHeight() {
        return 0;
    }

    @Override
    public byte getWidth() {
        return 0;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Collection<RecipeItemStack> getIngredients() {
        return ImmutableList.copyOf(ingredients);
    }

    @Override
    public boolean matchesItems(CraftingContainer craftable) {
        return false;
    }

    @Override
    public ItemStack getResult() {
        return result;
    }

    @Override
    public void writeTo(ByteBuf buf) {

    }
}
