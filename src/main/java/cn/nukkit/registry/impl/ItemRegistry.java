package cn.nukkit.registry.impl;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.registry.AbstractRegistry;
import cn.nukkit.registry.RegistryType;

public class ItemRegistry extends AbstractRegistry<Item> implements ItemID {
    public ItemRegistry() {
        super(RegistryType.ITEM);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void parseArgs(Item obj, Object... args) {

    }
}
