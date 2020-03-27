package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemShield extends Item {

    public ItemShield(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
