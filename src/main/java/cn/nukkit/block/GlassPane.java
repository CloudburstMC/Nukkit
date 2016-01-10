package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class GlassPane extends Thin {

    public GlassPane() {
        this(0);
    }

    public GlassPane(int meta) {
        super(0);
    }


    @Override
    public String getName() {
        return "Glass Pane";
    }

    @Override
    public int getId() {
        return GLASS_PANE;
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
        return new int[][]{};
    }
}
