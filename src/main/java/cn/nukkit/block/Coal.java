package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/24 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Coal extends Solid {
    public Coal() {
        this(0);
    }

    public Coal(int meta) {
        super(COAL_BLOCK, meta);
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Coal Block";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new int[][]{
                    {Item.COAL_BLOCK, 0, 1}
            };
        } else {
            return new int[0][];
        }
    }
}
