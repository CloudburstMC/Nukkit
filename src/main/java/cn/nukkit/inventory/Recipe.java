package cn.nukkit.inventory;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Recipe {

    Item getResult();

    boolean isCompatibleWith(int protocolVersion);
    void setRecipeProtocol(int protocolVersion);

    void registerToCraftingManager(CraftingManager manager);
}
