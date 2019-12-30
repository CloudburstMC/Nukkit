package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemHorseArmorGold extends Item {

    public ItemHorseArmorGold(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
