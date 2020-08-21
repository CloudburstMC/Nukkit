package cn.nukkit.item;

/**
 * @author Leonidius20
 * @since 18.08.18
 */
public class ItemMagmaCream extends Item {

    public ItemMagmaCream() {
        this(0);
    }

    public ItemMagmaCream(Integer meta) {
        this(0, 1);
    }

    public ItemMagmaCream(Integer meta, int count) {
        super(MAGMA_CREAM, meta, count, "Magma Cream");
    }
}
