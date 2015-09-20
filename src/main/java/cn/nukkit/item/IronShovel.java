package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronShovel extends Tool {

    public IronShovel() {
        this(0, 1);
    }

    public IronShovel(int meta) {
        this(meta, 1);
    }

    public IronShovel(int meta, int count) {
        super(IRON_SHOVEL, meta, count, "Gold Shovel");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_GOLD;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_IRON;
    }
}
