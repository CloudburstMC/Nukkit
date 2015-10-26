package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Compass extends Item {

    public Compass() {
        this(0, 1);
    }

    public Compass(Integer meta) {
        this(meta, 1);
    }

    public Compass(Integer meta, int count) {
        super(COMPASS, meta, count, "Compass");
    }
}
