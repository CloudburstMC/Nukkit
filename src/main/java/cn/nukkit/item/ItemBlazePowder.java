package cn.nukkit.item;

/**
 * Created by Leonidius20 on 18.08.18.
 */
public class ItemBlazePowder extends Item {

    public ItemBlazePowder() {
        this(0);
    }

    public ItemBlazePowder(Integer meta) {
        this(meta, 1);
    }

    public ItemBlazePowder(Integer meta, int count) {
        super(BLAZE_POWDER, meta, count, "Blaze Powder");
    }
}
