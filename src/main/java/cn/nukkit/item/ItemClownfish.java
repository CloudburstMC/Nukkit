package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemClownfish extends ItemFish {

    public ItemClownfish() {
        this(0, 1);
    }

    public ItemClownfish(Integer meta) {
        this(meta, 1);
    }

    public ItemClownfish(Integer meta, int count) {
        super(CLOWNFISH, meta, count, "Clownfish");
    }
}
