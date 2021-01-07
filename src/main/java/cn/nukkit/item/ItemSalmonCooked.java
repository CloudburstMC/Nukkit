package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemSalmonCooked extends ItemFish {

    public ItemSalmonCooked() {
        this(0, 1);
    }

    public ItemSalmonCooked(Integer meta) {
        this(meta, 1);
    }

    public ItemSalmonCooked(Integer meta, int count) {
        super(COOKED_SALMON, meta, count, "Cooked Salmon");
    }

}
