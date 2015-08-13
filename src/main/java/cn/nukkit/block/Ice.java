package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Ice extends Trasparent {
    protected int id = ICE;

    public Ice() {
        super(ICE, 0);
    }

    public Ice(int meta) {
        super(ICE, 0);
    }

    @Override
    public String getName() {
        return "Ice";
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_NONE;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new Water(), true);
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[0][0];
    }
}
