package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Beetroot extends Crop {
    public Beetroot() {
        this(0);
    }

    public Beetroot(int meta) {
        super(BEETROOT_BLOCK, meta);
    }

    @Override
    public String getName() {
        return "Beetroot Block";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (this.meta >= 0x07) {
            return new int[][]{
                    {Item.BEETROOT, 0, 1},
                    {Item.BEETROOT_SEEDS, 0, (int) (4d * Math.random())}
            };
        } else {
            return new int[][]{
                    {Item.BEETROOT_SEEDS, 0, 1}
            };
        }
    }
}
