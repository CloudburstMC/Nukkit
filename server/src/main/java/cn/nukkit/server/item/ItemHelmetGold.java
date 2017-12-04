package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemHelmetGold extends ItemArmor {

    public ItemHelmetGold() {
        this(0, 1);
    }

    public ItemHelmetGold(Integer meta) {
        this(meta, 1);
    }

    public ItemHelmetGold(Integer meta, int count) {
        super(GOLD_HELMET, meta, count, "Gold Helmet");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_GOLD;
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
        return 78;
    }
}
