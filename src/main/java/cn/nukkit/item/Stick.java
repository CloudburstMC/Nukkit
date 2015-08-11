package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Stick extends Item {

    public Stick() {
        this(0, 1);
    }

    public Stick(int meta) {
        this(meta, 1);
    }

    public Stick(int meta, int count) {
        super(STICK, 0, count, "Stick");
    }
}
