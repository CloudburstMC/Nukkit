package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemNuggetGold extends Item {

    public ItemNuggetGold() {
        this(0, 1);
    }

    public ItemNuggetGold(Integer meta) {
        this(meta, 1);
    }

    public ItemNuggetGold(Integer meta, int count) {
        super(GOLD_NUGGET, meta, count, "Gold Nugget");
    }
}
