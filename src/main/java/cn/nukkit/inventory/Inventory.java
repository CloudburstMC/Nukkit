package cn.nukkit.inventory;

import cn.nukkit.item.Item;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Inventory {

    Map<Integer, Item> getContents();
}
