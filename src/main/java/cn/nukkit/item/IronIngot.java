package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronIngot extends Item {

    public IronIngot() {
        this(0, 1);
    }

    public IronIngot(int meta) {
        this(meta, 1);
    }

    public IronIngot(int meta, int count) {
        super(IRON_INGOT, 0, count, "Iron Ingot");
    }
}
