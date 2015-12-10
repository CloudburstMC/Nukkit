package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class IronBars extends Thin {

    public IronBars() {
        this(0);
    }

    public IronBars(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Iron Bars";
    }

    @Override
    public int getId() {
        return IRON_BARS;
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
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{
                    {Item.IRON_BARS, 0, 1}
            };
        } else {
            return new int[][]{};
        }
    }
}
