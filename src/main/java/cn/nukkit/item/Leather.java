package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Leather extends Item {

    public Leather() {
        this(0, 1);
    }

    public Leather(Integer meta) {
        this(meta, 1);
    }

    public Leather(Integer meta, int count) {
        super(LEATHER, meta, count, "Leather");
    }
}
