package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoodenSword extends Tool {

    public WoodenSword() {
        this(0, 1);
    }

    public WoodenSword(Integer meta) {
        this(meta, 1);
    }

    public WoodenSword(Integer meta, int count) {
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

    @Override
    public int getTier() {
        return Tool.TIER_WOODEN;
    }
}
