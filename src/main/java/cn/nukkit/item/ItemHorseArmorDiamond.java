package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemHorseArmorDiamond extends Item {

    public ItemHorseArmorDiamond(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
