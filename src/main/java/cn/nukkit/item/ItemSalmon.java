package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemSalmon extends ItemFish {

    public ItemSalmon() {
        this(0, 1);
    }

    public ItemSalmon(Integer meta) {
        this(meta, 1);
    }

    public ItemSalmon(Integer meta, int count) {
        super(RAW_SALMON, meta, count, "Raw Salmon");
    }

}
