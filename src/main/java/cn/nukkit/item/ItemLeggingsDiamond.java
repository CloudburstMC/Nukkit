package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemLeggingsDiamond extends ItemArmor {

    public ItemLeggingsDiamond(Identifier id) {
        super(id);
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_DIAMOND;
    }

    @Override
    public int getArmorPoints() {
        return 6;
    }

    @Override
    public int getMaxDurability() {
        return 496;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}
