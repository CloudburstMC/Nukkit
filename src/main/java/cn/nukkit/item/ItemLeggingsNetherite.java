package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.3.2.0-PN")
public class ItemLeggingsNetherite extends ItemArmor {

    @Since("1.3.2.0-PN")
    public ItemLeggingsNetherite() {
        this(0, 1);
    }

    @Since("1.3.2.0-PN")
    public ItemLeggingsNetherite(Integer meta) {
        this(meta, 1);
    }

    @Since("1.3.2.0-PN")
    public ItemLeggingsNetherite(Integer meta, int count) {
        super(NETHERITE_LEGGINGS, meta, count, "Netherite Leggings");
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
    }

    @Override
    public int getMaxDurability() {
        return 555;
    }

    @Override
    public int getArmorPoints() {
        return 6;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}
