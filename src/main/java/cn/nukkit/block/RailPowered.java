package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.block in project nukkit
 */
public class RailPowered extends Rail {
    public RailPowered() {
        this(0);
    }

    public RailPowered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POWERED_RAIL;
    }

    @Override
    public String getName() {
        return "Powered Rail";
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.POWERED_RAIL, 0, 1}};
    }
}
