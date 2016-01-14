package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RawChicken extends EdibleItem {

    public RawChicken() {
        this(0, 1);
    }

    public RawChicken(Integer meta) {
        this(meta, 1);
    }

    public RawChicken(Integer meta, int count) {
        super(RAW_CHICKEN, meta, count, "Raw Chicken");
    }
}
