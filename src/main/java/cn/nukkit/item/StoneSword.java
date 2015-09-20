package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StoneSword extends Tool {

    public StoneSword() {
        this(0, 1);
    }

    public StoneSword(int meta) {
        this(meta, 1);
    }

    public StoneSword(int meta, int count) {
        super(STONE_SWORD, meta, count, "Stone Sword");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_STONE;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_STONE;
    }
}
