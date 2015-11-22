package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Arrow extends Item {

    public Arrow() {
        this(0, 1);
    }

    public Arrow(Integer meta) {
        this(meta, 1);
    }

    public Arrow(Integer meta, int count) {
        super(ARROW, meta, count, "Arrow");
    }

}
