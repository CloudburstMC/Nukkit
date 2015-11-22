package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronIngot extends Item {

    public IronIngot() {
        this(0, 1);
    }

    public IronIngot(Integer meta) {
        this(meta, 1);
    }

    public IronIngot(Integer meta, int count) {
        super(IRON_INGOT, 0, count, "Iron Ingot");
    }
}
