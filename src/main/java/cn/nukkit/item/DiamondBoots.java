package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondBoots extends Armor {

    public DiamondBoots() {
        this(0, 1);
    }

    public DiamondBoots(int meta) {
        this(meta, 1);
    }

    public DiamondBoots(int meta, int count) {
        super(DIAMOND_BOOTS, meta, count, "Diamond Boots");
    }
}
