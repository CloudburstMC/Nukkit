package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Wheat extends Item {

    public Wheat() {
        this(0, 1);
    }

    public Wheat(Integer meta) {
        this(meta, 1);
    }

    public Wheat(Integer meta, int count) {
        super(WHEAT, meta, count, "Wheat");
    }
}
