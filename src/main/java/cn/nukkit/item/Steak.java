package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Steak extends Item {

    public Steak() {
        this(0, 1);
    }

    public Steak(Integer meta) {
        this(meta, 1);
    }

    public Steak(Integer meta, int count) {
        super(STEAK, meta, count, "Steak");
    }
}
