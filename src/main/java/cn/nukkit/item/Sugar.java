package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Sugar extends Item {

    public Sugar() {
        this(0, 1);
    }

    public Sugar(Integer meta) {
        this(meta, 1);
    }

    public Sugar(Integer meta, int count) {
        super(SUGAR, meta, count, "Sugar");
    }
}
