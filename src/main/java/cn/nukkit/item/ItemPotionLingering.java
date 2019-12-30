package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemPotionLingering extends Item {

    public ItemPotionLingering(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}