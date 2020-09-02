package cn.nukkit.inventory;

import cn.nukkit.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface Recipe {

    Item getResult();

    void registerToCraftingManager(CraftingManager manager);

    RecipeType getType();
}
