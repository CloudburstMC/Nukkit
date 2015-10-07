package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondHoe extends Tool {

    public DiamondHoe() {
        this(0, 1);
    }

    public DiamondHoe(Integer meta) {
        this(meta, 1);
    }

    public DiamondHoe(Integer meta, int count) {
        super(DIAMOND_HOE, meta, count, "Diamond Hoe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_DIAMOND;
    }
}
