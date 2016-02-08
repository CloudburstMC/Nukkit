package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBeefRaw extends ItemEdible {

    public ItemBeefRaw() {
        this(0, 1);
    }

    public ItemBeefRaw(Integer meta) {
        this(meta, 1);
    }

    public ItemBeefRaw(Integer meta, int count) {
        super(RAW_BEEF, meta, count, "Raw Beef");
    }
}
