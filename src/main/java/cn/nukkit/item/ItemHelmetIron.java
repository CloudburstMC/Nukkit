package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemHelmetIron extends ItemArmor {

    public ItemHelmetIron() {
        this(0, 1);
    }

    public ItemHelmetIron(Integer meta) {
        this(meta, 1);
    }

    public ItemHelmetIron(Integer meta, int count) {
        super(IRON_HELMET, meta, count, "Iron Helmet");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_IRON;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return 166;
    }
}
