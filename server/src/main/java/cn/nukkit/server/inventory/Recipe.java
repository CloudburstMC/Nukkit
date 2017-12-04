package cn.nukkit.server.inventory;

import cn.nukkit.server.item.Item;

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

    default boolean requiresCraftingTable() {
        return false;
    }
}
