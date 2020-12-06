package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.3.2.0-PN")
public class ItemBootsNetherite extends ItemArmor {

    @Since("1.3.2.0-PN")
    public ItemBootsNetherite() {
        this(0, 1);
    }

    @Since("1.3.2.0-PN")
    public ItemBootsNetherite(Integer meta) {
        this(meta, 1);
    }

    @Since("1.3.2.0-PN")
    public ItemBootsNetherite(Integer meta, int count) {
        super(NETHERITE_BOOTS, meta, count, "Netherite Boots");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 481;
    }

    @Override
    public int getToughness() {
        return 3;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
