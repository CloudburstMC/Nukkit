package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemSaddle extends Item {

    public ItemSaddle(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
