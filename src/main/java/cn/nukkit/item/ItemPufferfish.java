package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
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
