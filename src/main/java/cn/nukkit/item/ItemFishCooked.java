package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemFishCooked extends ItemFish {

    public ItemFishCooked() {
        this(0, 1);
    }

    public ItemFishCooked(Integer meta) {
        this(meta, 1);
    }

    public ItemFishCooked(Integer meta, int count) {
        super(COOKED_FISH, meta, count, "Cooked Fish");
    }

}
