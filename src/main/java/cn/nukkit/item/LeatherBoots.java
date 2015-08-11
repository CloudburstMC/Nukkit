package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeatherBoots extends Armor {

    public LeatherBoots() {
        this(0, 1);
    }

    public LeatherBoots(int meta) {
        this(meta, 1);
    }

    public LeatherBoots(int meta, int count) {
        super(LEATHER_BOOTS, meta, count, "Leather Boots");
    }
}
