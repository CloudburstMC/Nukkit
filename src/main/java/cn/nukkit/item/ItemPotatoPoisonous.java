package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
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
