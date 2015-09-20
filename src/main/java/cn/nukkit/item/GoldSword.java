package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldSword extends Tool {

    public GoldSword() {
        this(0, 1);
    }

    public GoldSword(int meta) {
        this(meta, 1);
    }

    public GoldSword(int meta, int count) {
        super(GOLD_SWORD, meta, count, "Gold Sword");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_GOLD;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_GOLD;
    }
}
