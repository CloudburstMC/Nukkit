package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Clock extends Item {

    public Clock() {
        this(0, 1);
    }

    public Clock(Integer meta) {
        this(meta, 1);
    }

    public Clock(Integer meta, int count) {
        super(CLOCK, meta, count, "Clock");
    }
}
