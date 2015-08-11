package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Shears extends Item {

    public Shears() {
        this(0, 1);
    }

    public Shears(int meta) {
        this(meta, 1);
    }

    public Shears(int meta, int count) {
        super(SHEARS, meta, count, "Shears");
    }
}
