package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBootsGold extends ItemArmor {

    public ItemBootsGold() {
        this(0, 1);
    }

    public ItemBootsGold(Integer meta) {
        this(meta, 1);
    }

    public ItemBootsGold(Integer meta, int count) {
        super(GOLD_BOOTS, meta, count, "Gold Boots");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_GOLD;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 92;
    }
}
