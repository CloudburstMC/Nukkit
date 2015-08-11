package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Diamond extends Item {

    public Diamond() {
        this(0, 1);
    }

    public Diamond(int meta) {
        this(meta, 1);
    }

    public Diamond(int meta, int count) {
        super(DIAMOND, 0, count, "Diamond");
    }
}
