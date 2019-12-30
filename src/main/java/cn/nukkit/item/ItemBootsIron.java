package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBootsIron extends ItemArmor {

    public ItemBootsIron(Identifier id) {
        super(id);
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_IRON;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 196;
    }
}
