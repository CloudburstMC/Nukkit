package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Farmland extends Solid {

    public Farmland() {
        this(0);
    }

    public Farmland(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Farmland";
    }

    @Override
    public int getId() {
        return FARMLAND;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 0.6;
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
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.DIRT, 0, 1}
        };
    }

    @Override
    public Color getColor() {
        return Color.dirtColor;
    }
}
