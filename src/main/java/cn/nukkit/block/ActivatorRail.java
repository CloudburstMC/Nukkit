package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * @author Nukkit Project Team
 */
public class ActivatorRail extends Transparent {

    public ActivatorRail(int meta) {
        super(meta);
    }

    public ActivatorRail() {
        this(0);
    }

    @Override
    public String getName() {
        return "Activator Rail";
    }

    @Override
    public int getId() {
        return ACTIVATOR_RAIL;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.ACTIVATOR_RAIL, 0, 1}};
    }

}
