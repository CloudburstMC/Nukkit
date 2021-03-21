package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemChickenRaw extends ItemEdible {

    public ItemChickenRaw() {
        this(0, 1);
    }

    public ItemChickenRaw(Integer meta) {
        this(meta, 1);
    }

    public ItemChickenRaw(Integer meta, int count) {
        super(RAW_CHICKEN, meta, count, "Raw Chicken");
    }
}
