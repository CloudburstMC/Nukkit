package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Emerald extends Item {

    public Emerald() {
        this(0, 1);
    }

    public Emerald(Integer meta) {
        this(meta, 1);
    }

    public Emerald(Integer meta, int count) {
        super(EMERALD, meta, count, "Emerald");
    }
}
