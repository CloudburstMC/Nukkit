package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronHoe extends Tool {

    public IronHoe() {
        this(0, 1);
    }

    public IronHoe(int meta) {
        this(meta, 1);
    }

    public IronHoe(int meta, int count) {
        super(IRON_HOE, meta, count, "Iron Hoe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_IRON;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_IRON;
    }
}
