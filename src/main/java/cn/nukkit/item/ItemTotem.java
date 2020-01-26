package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemTotem extends Item {

    public ItemTotem(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
