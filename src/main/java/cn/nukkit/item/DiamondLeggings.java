package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondLeggings extends Armor {

    public DiamondLeggings() {
        this(0, 1);
    }

    public DiamondLeggings(int meta) {
        this(meta, 1);
    }

    public DiamondLeggings(int meta, int count) {
        super(DIAMOND_LEGGINGS, meta, count, "Diamond Leggings");
    }
}
