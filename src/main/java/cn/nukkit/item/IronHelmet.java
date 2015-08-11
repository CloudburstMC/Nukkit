package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronHelmet extends Armor {

    public IronHelmet() {
        this(0, 1);
    }

    public IronHelmet(int meta) {
        this(meta, 1);
    }

    public IronHelmet(int meta, int count) {
        super(IRON_HELMET, meta, count, "Iron Helmet");
    }
}
