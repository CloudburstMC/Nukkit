package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoodenHoe extends Tool {

    public WoodenHoe() {
        this(0, 1);
    }

    public WoodenHoe(Integer meta) {
        this(meta, 1);
    }

    public WoodenHoe(Integer meta, int count) {
        super(WOODEN_HOE, meta, count, "Wooden Hoe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_WOODEN;
    }
}
