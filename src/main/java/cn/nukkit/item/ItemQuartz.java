package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemQuartz extends Item {

    public ItemQuartz() {
        this(0, 1);
    }

    public ItemQuartz(Integer meta) {
        this(meta, 1);
    }

    public ItemQuartz(Integer meta, int count) {
        super(NETHER_QUARTZ, 0, count, "Nether Quartz");
    }
}
