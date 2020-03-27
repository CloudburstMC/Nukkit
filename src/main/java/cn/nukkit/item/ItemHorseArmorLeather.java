package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemHorseArmorLeather extends Item {

    public ItemHorseArmorLeather(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
