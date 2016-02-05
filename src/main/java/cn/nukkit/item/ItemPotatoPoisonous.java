package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class ItemPotatoPoisonous extends ItemPotato {

    public ItemPotatoPoisonous() {
        this(0, 1);
    }

    public ItemPotatoPoisonous(Integer meta) {
        this(meta, 1);
    }

    public ItemPotatoPoisonous(Integer meta, int count) {
        super(POISONOUS_POTATO, meta, count, "Poisonous Potato");
    }
}
