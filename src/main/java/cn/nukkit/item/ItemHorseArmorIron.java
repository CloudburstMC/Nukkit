package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

public class ItemHorseArmorIron extends Item {

    public ItemHorseArmorIron(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
