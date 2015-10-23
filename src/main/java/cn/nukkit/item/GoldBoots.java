package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldBoots extends Armor {

    public GoldBoots() {
        this(0, 1);
    }

    public GoldBoots(Integer meta) {
        this(meta, 1);
    }

    public GoldBoots(Integer meta, int count) {
        super(GOLD_BOOTS, meta, count, "Gold Boots");
    }
}
