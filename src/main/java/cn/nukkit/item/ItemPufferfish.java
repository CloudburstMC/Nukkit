package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemPufferfish extends ItemFish {

    public ItemPufferfish() {
        this(0, 1);
    }

    public ItemPufferfish(Integer meta) {
        this(meta, 1);
    }

    public ItemPufferfish(Integer meta, int count) {
        super(PUFFERFISH, meta, count, "Pufferfish");
    }

}
