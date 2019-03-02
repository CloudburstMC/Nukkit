package com.nukkitx.server.crafting.recipe;

import com.nukkitx.api.container.CraftingContainer;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.RecipeItemStack;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.UUID;

public interface Recipe {

    static UUID generateRecipeUuid(ItemStack[] items) {
        ByteBuffer buffer = ByteBuffer.allocate(items.length * 4);

        for (ItemStack item : items) {
            buffer.putInt(item.getItemType().getId() | item.getAmount() << 15);
        }

        return UUID.nameUUIDFromBytes(buffer.array());
    }

    UUID getUuid();

    default boolean isMultiRecipe() {
        return false;
    }

    Collection<RecipeItemStack> getIngredients();

    boolean matchesItems(CraftingContainer container);

    ItemStack getResult();

    void writeTo(ByteBuf buf);

    enum Type {
        SHAPELESS(false),
        SHAPED(true),
        FURNACE(false),
        FURNACE_DATA(false),
        MULTI(false),
        SHULKER_BOX(false),
        SHAPELESS_CHEMISTRY(false),
        SHAPED_CHEMISTRY(true);

        private final boolean ordered;

        Type(boolean ordered) {
            this.ordered = ordered;
        }

        public boolean isOrdered() {
            return ordered;
        }
    }
}
