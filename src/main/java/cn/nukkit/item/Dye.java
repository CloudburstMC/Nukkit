package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Dye extends Item {

    public Dye() {
        this(0, 1);
    }

    public Dye(Integer meta) {
        this(meta, 1);
    }

    public Dye(Integer meta, int count) {
        super(DYE, meta, count, "Dye");
    }
}
