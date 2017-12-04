package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPotatoBaked extends Item {

    public ItemPotatoBaked() {
        this(0, 1);
    }

    public ItemPotatoBaked(Integer meta) {
        this(meta, 1);
    }

    public ItemPotatoBaked(Integer meta, int count) {
        super(BAKED_POTATO, meta, count, "Baked Potato");
    }

}
