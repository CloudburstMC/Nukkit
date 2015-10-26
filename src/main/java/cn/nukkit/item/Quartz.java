package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Quartz extends Item {

    public Quartz() {
        this(0, 1);
    }

    public Quartz(Integer meta) {
        this(meta, 1);
    }

    public Quartz(Integer meta, int count) {
        super(QUARTZ, meta, count, "Quartz");
    }
}
