package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * @author Nukkit Project Team
 */
public class Clay extends Solid {

    public Clay(int meta) {
        super(meta);
    }

    public Clay() {
        this(0);
    }

    @Override
    public int getId() {
        return Block.CLAY_BLOCK;
    }

    @Override
    public String getName() {
        return "Clay Block";
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.CLAY, 0, 4}};
    }

}
