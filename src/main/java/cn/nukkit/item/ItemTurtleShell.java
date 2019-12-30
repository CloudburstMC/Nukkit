package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

/**
 * Created by PetteriM1
 */
public class ItemTurtleShell extends ItemArmor {

    public ItemTurtleShell(Identifier id) {
        super(id);
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_OTHER;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 276;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}
