package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.List;
import java.util.UUID;

/**
 * @author CreeperFace
 */
public interface CraftingRecipe extends Recipe {

    String getRecipeId();

    UUID getId();

    void setId(UUID id);

    boolean requiresCraftingTable();

    List<Item> getExtraResults();

    List<Item> getAllResults();

    int getPriority();

    /**
     * Returns whether the specified list of crafting grid inputs and outputs matches this recipe. Outputs DO NOT
     * include the primary result item.
     *
     * @param input  list of items taken from the crafting grid
     * @param output list of items put back into the crafting grid (secondary results)
     * @return bool
     */
    boolean matchItems(List<Item> input, List<Item> output);
}
