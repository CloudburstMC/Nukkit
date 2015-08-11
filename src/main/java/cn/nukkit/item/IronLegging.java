package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronLegging extends Armor {

    public IronLegging() {
        this(0, 1);
    }

    public IronLegging(int meta) {
        this(meta, 1);
    }

    public IronLegging(int meta, int count) {
        super(IRON_LEGGINGS, meta, count, "Iron Leggings");
    }
}
