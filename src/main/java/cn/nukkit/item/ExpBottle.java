package cn.nukkit.item;

/**
 * Created on 2015/12/25 by xtypr.
 * Package cn.nukkit.item in project Nukkit .
 */
public class ExpBottle extends Item {

    public ExpBottle() {
        this(0, 1);
    }

    public ExpBottle(int meta) {
        this(meta, 1);
    }

    public ExpBottle(Integer meta, int count) {
        super(EXPERIENCE_BOTTLE, meta, count, "Exp Bottle");
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
