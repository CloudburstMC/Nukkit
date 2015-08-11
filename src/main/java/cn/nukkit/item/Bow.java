package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bow extends Item {

    public Bow() {
        this(0, 1);
    }

    public Bow(int meta) {
        this(meta, 1);
    }

    public Bow(int meta, int count) {
        super(BOW, meta, count, "Bow");
    }
}
