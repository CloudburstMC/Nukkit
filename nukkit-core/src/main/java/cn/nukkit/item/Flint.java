package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Flint extends Item {

    public Flint() {
        this(0, 1);
    }

    public Flint(Integer meta) {
        this(meta, 1);
    }

    public Flint(Integer meta, int count) {
        super(FLINT, meta, count, "Flint");
    }
}
