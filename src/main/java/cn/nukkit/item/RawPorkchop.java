package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RawPorkchop extends EdibleItem {

    public RawPorkchop() {
        this(0, 1);
    }

    public RawPorkchop(Integer meta) {
        this(meta, 1);
    }

    public RawPorkchop(Integer meta, int count) {
        super(RAW_PORKCHOP, meta, count, "Raw Porkchop");
    }
}
