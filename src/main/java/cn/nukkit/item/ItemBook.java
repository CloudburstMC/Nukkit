package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBook extends Item {

    public ItemBook(Identifier id) {
        super(id);
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }
}
