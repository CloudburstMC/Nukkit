package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondLegging extends Armor {

    public DiamondLegging() {
        this(0, 1);
    }

    public DiamondLegging(int meta) {
        this(meta, 1);
    }

    public DiamondLegging(int meta, int count) {
        super(DIAMOND_LEGGINGS, meta, count, "Diamond Leggings");
    }
}
