package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Leaves2 extends Leaves {

    public Leaves2() {
        this(0);
    }

    public Leaves2(int meta) {
        super(meta);
    }

    public String getName(){
        String[] names = new String[]{
                "Oak Leaves",
                "Spruce Leaves",
                "Birch Leaves",
                "Jungle Leaves"
        };
        return names[this.meta & 0x01];
    }

    @Override
    public int getId() {
        return LEAVES2;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isShears()) {
            return new int[][]{new int[]{Item.LEAVES2, this.meta & 0x03, 1}};
        } else {
            if ((int) ((Math.random()) * 20) == 0) {
                return new int[][]{new int[]{Item.SAPLING, this.meta & 0x03, 1}};
            }
        }
        return new int[0][];
    }
}
