package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Feather extends Item {

    public Feather() {
        this(0, 1);
    }

    public Feather(int meta) {
        this(meta, 1);
    }

    public Feather(int meta, int count) {
        super(FEATHER, 0, count, "Feather");
    }
}
