package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Gunpowder extends Item {

    public Gunpowder() {
        this(0, 1);
    }

    public Gunpowder(Integer meta) {
        this(meta, 1);
    }

    public Gunpowder(Integer meta, int count) {
        super(GUNPOWDER, meta, count, "Gunpowder");
    }
}
