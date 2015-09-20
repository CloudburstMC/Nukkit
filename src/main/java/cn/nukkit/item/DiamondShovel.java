package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondShovel extends Tool {

    public DiamondShovel() {
        this(0, 1);
    }

    public DiamondShovel(int meta) {
        this(meta, 1);
    }

    public DiamondShovel(int meta, int count) {
        super(DIAMOND_SHOVEL, meta, count, "Diamond Shovel");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_DIAMOND;
    }
}
