package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Glass extends Transparent {

    public Glass() {
        this(0);
    }

    public Glass(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GLASS;
    }

    @Override
    public String getName() {
        return "Glass";
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[0][];
    }
}
