package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemHelmetLeather extends ItemArmor {

    public ItemHelmetLeather() {
        this(0, 1);
    }

    public ItemHelmetLeather(Integer meta) {
        this(meta, 1);
    }

    public ItemHelmetLeather(Integer meta, int count) {
        super(LEATHER_CAP, meta, count, "Leather Cap");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_LEATHER;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }
}
