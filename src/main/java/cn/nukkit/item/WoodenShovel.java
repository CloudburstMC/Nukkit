package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoodenShovel extends Tool {

    public WoodenShovel() {
        this(0, 1);
    }

    public WoodenShovel(Integer meta) {
        this(meta, 1);
    }

    public WoodenShovel(Integer meta, int count) {
        super(WOODEN_SHOVEL, meta, count, "Wooden Shovel");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isShovel() {
        return true;
    }
}
