package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldNugget extends Item {

    public GoldNugget() {
        this(0, 1);
    }

    public GoldNugget(Integer meta) {
        this(meta, 1);
    }

    public GoldNugget(Integer meta, int count) {
        super(GOLD_NUGGET, meta, count, "Gold Nugget");
    }
}
