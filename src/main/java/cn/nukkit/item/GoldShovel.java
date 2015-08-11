package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldShovel extends Tool {

    public GoldShovel() {
        this(0, 1);
    }

    public GoldShovel(int meta) {
        this(meta, 1);
    }

    public GoldShovel(int meta, int count) {
        super(GOLD_SHOVEL, meta, count, "Gold Shovel");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_GOLD;
    }

    @Override
    public boolean isShovel() {
        return true;
    }
}
