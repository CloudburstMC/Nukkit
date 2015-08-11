package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondSword extends Tool {

    public DiamondSword() {
        this(0, 1);
    }

    public DiamondSword(int meta) {
        this(meta, 1);
    }

    public DiamondSword(int meta, int count) {
        super(DIAMOND_SWORD, meta, count, "Diamond Sword");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isSword() {
        return true;
    }
}
