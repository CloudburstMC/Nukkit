package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class GrassPath extends Transparent {

    public GrassPath() {
        this(0);
    }

    public GrassPath(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return GRASS_PATH;
    }

    @Override
    public String getName() {
        return "Grass Path";
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 0.9375,
                this.z + 1
        );
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3.25;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.DIRT, 0, 1}
        };
    }
}
