package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronBoots extends Armor {

    public IronBoots() {
        this(0, 1);
    }

    public IronBoots(int meta) {
        this(meta, 1);
    }

    public IronBoots(int meta, int count) {
        super(IRON_BOOTS, meta, count, "Iron Boots");
    }
}
