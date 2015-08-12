package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronLeggings extends Armor {

    public IronLeggings() {
        this(0, 1);
    }

    public IronLeggings(int meta) {
        this(meta, 1);
    }

    public IronLeggings(int meta, int count) {
        super(IRON_LEGGINGS, meta, count, "Iron Leggings");
    }
}
