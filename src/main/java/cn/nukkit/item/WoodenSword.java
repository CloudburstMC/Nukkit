package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoodenSword extends Tool {

    public WoodenSword() {
        this(0, 1);
    }

    public WoodenSword(int meta) {
        this(meta, 1);
    }

    public WoodenSword(int meta, int count) {
        super(WOODEN_SWORD, meta, count, "Wooden Sword");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isSword() {
        return true;
    }
}
