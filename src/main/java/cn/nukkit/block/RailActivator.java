package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * @author Nukkit Project Team
 */
public class RailActivator extends Rail {

    public RailActivator(int meta) {
        super(meta);
    }

    public RailActivator() {
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
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.ACTIVATOR_RAIL, 0, 1}};
    }

}
