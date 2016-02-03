package cn.nukkit.item;

/**
 * Created on 2015/12/25 by xtypr.
 * Package cn.nukkit.item in project Nukkit .
 */
public class ItemExpBottle extends Item {

    public ItemExpBottle() {
        this(0, 1);
    }

    public ItemExpBottle(int meta) {
        this(meta, 1);
    }

    public ItemExpBottle(Integer meta, int count) {
        super(EXPERIENCE_BOTTLE, meta, count, "Exp Bottle");
    }

}
