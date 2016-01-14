package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RawBeef extends EdibleItem {

    public RawBeef() {
        this(0, 1);
    }

    public RawBeef(Integer meta) {
        this(meta, 1);
    }

    public RawBeef(Integer meta, int count) {
        super(RAW_BEEF, meta, count, "Raw Beef");
    }
}
