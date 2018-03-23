package cn.nukkit.registry.impl;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.registry.AbstractRegistry;
import cn.nukkit.registry.RegistryType;
import cn.nukkit.registry.function.IntObjectFunction;

public final class ItemRegistry extends AbstractRegistry<Item, IntObjectFunction<Item>> implements ItemID {
    public ItemRegistry() {
        super(RegistryType.ITEM);
    }

    @Override
    protected void init() {

    }

    @Override
    protected Item accept(IntObjectFunction<Item> func, int i, Object... args) {
        if (args.length == 0)   {
            return func.accept(i);
        }
        //porktodo: check if there's anything else that needs to be done here
        return null;
    }
}
