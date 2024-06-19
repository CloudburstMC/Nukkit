package cn.nukkit.item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemRail extends Item {

    public ItemRail() {
        this(0, 1);
    }

    public ItemRail(Integer meta) {
        this(meta, 1);
    }

    public ItemRail(Integer meta, int count) {
        super(RAIL, 0, count, "Rail");
    }
}
