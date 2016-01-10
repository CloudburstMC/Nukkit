package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.RGBColor;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class NetherBrick extends Solid {

    public NetherBrick() {
        this(0);
    }

    public NetherBrick(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Nether Bricks";
    }

    @Override
    public int getId() {
        return NETHER_BRICKS;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{
                    {Item.NETHER_BRICKS, 0, 1}
            };
        } else {
            return new int[][]{};
        }
    }

    @Override
    public RGBColor getMapColor() {
        return RGBColor.netherrackColor;
    }
}
