package cn.nukkit.item;

/**
 * @author lion
 * @since 21.03.17
 */
public class ItemBlazeRod extends Item {

    public ItemBlazeRod() {
        this(0, 1);
    }

    public ItemBlazeRod(Integer meta) {
        this(meta, 1);
    }

    public ItemBlazeRod(Integer meta, int count) {
        super(BLAZE_ROD, meta, count, "Blaze Rod");
    }

}
