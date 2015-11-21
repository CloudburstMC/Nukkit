package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Clay extends Item {

    public Clay() {
        this(0, 1);
    }

    public Clay(Integer meta) {
        this(meta, 1);
    }

    public Clay(Integer meta, int count) {
        super(CLAY, meta, count, "Clay");
    }
}
