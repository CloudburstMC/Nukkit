package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemFish extends Item {

    public ItemFish() {
        this(0, 1);
    }

    public ItemFish(Integer meta) {
        this(meta, 1);
    }

    public ItemFish(Integer meta, int count) {
        super(RAW_FISH, meta, count, "Raw Fish");
    }

    protected ItemFish(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

}
