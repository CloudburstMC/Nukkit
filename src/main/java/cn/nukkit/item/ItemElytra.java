package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemElytra extends ItemArmor {

    public ItemElytra(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxDurability() {
        return 431;
    }

    @Override
    public boolean isArmor() {
        return true;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

}
