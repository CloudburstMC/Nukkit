package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Cookie extends Item {

    public Cookie() {
        this(0, 1);
    }

    public Cookie(Integer meta) {
        this(meta, 1);
    }

    public Cookie(Integer meta, int count) {
        super(COOKIE, meta, count, "Cookie");
    }
}
