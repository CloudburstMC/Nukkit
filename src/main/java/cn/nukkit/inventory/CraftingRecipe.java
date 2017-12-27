package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.List;
import java.util.UUID;

/**
 * @author CreeperFace
 */
public interface CraftingRecipe extends Recipe {

    UUID getId();

    void setId(UUID id);

    boolean requiresCraftingTable();

    List<Item> getExtraResults();

    List<Item> getAllResults();

    /**
     * Returns whether the specified list of crafting grid inputs and outputs matches this recipe. Outputs DO NOT
     * include the primary result item.
     *
     * @param input  2D array of items taken from the crafting grid
     * @param output 2D array of items put back into the crafting grid (secondary results)
     * @return bool
     */
    boolean matchItems(Item[][] input, Item[][] output);
}
