package cn.nukkit.item;

/**
 * @author Kaooot
 * @version 1.0
 */
public class ItemIngotNetherite extends Item {

    public ItemIngotNetherite() {
        this(0, 1);
    }

    public ItemIngotNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotNetherite(Integer meta, int count) {
        super(NETHERITE_INGOT, meta, count, "Netherite Ingot");
    }
}