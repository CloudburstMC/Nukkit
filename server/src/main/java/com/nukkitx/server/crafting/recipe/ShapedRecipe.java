package com.nukkitx.server.crafting.recipe;


import com.google.common.base.Preconditions;
import com.nukkitx.api.container.CraftingContainer;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.RecipeItemStack;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharObjectHashMap;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShapedRecipe implements CraftingRecipe {
    private final UUID uuid;
    private final char[][] shape;
    private final TCharObjectMap<RecipeItemStack> ingredients;
    private final ItemStack result;
    private final byte height;
    private final byte width;

    public static ShapedRecipe of(@Nonnull UUID uuid, @Nonnull char[][] shape, @Nonnull TCharObjectMap<RecipeItemStack> ingredients, @Nonnull ItemStack result) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(shape, "shape");
        Preconditions.checkNotNull(ingredients, "ingredients");
        Preconditions.checkNotNull(result, "result");

        checkShapeSize(shape.length);
        for (int x = 0; x < shape.length; x++) {
            checkShapeSize(shape[x].length);

            for (int y = 0; y < shape[x].length; y++) {
                if (!ingredients.containsKey(shape[x][y])) {
                    throw new IllegalArgumentException("Character on shape not found in ingredients");
                }
            }
        }

        byte height = (byte) shape.length;
        byte width = (byte) shape[0].length;

        TCharObjectMap<RecipeItemStack> ingredientsMap = new TCharObjectHashMap<>();
        ingredientsMap.putAll(ingredients);

        return new ShapedRecipe(uuid, Arrays.copyOf(shape, shape.length), ingredientsMap, result, height, width);
    }

    private static void checkShapeSize(int size) {
        Preconditions.checkArgument(size > 0 && size <= 3, "Invalid recipe size");
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public Collection<RecipeItemStack> getIngredients() {
        return ingredients.valueCollection();
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

    @Override
    public boolean matchesItems(ItemStack[][] items) {
        if (items.length != height && items[0].length != width) {
            return false;
        }

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                Optional<RecipeItemStack> ingredient = getIngredient(x, y);
                if (!ingredient.isPresent()) {
                    continue;
                }

                if (ingredient.get().equals(items[x][y])) {

                }
            }
        }
        //TODO
        return false;
    }

    @Override
    public byte getHeight() {
        return height;
    }

    @Override
    public byte getWidth() {
        return width;
    }

    private Optional<RecipeItemStack> getIngredient(int x, int y) {
        return Optional.ofNullable(ingredients.get(shape[x][y]));
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (RecipeItemStack ingredient : getIngredients()) {
            hashCode = 31 * hashCode + ingredient.getItemType().getId();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShapedRecipe that = (ShapedRecipe) o;
        return this.uuid.equals(that.uuid) &&
                Arrays.deepEquals(this.shape, that.shape) &&
                this.result.equals(that.result) &&
                this.ingredients.equals(that.ingredients);
    }
}
