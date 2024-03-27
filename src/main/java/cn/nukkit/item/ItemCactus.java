package cn.nukkit.item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemCactus extends Item {

    public ItemCactus() {
        this(0, 1);
    }

    public ItemCactus(Integer meta) {
        this(meta, 1);
    }

    public ItemCactus(Integer meta, int count) {
        super(CACTUS, 0, count, "Cactus");
    }
}
