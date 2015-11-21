package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Recipe {

    Item getResult();

    void registerToCraftingManager();

    UUID getId();

    void setId(UUID id);
}
