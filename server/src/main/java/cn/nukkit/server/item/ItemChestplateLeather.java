package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemChestplateLeather extends ItemColorArmor {

    public ItemChestplateLeather() {
        this(0, 1);
    }

    public ItemChestplateLeather(Integer meta) {
        this(meta, 1);
    }

    public ItemChestplateLeather(Integer meta, int count) {
        super(LEATHER_TUNIC, meta, count, "Leather Tunic");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_LEATHER;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 81;
    }
}
