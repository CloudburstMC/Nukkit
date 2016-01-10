package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.RGBColor;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Glowstone extends Transparent {
    public Glowstone() {
        this(0);
    }

    public Glowstone(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Glowstone";
    }

    @Override
    public int getId() {
        return GLOWSTONE_BLOCK;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.GLOWSTONE_DUST, 0, ((int) (2d * Math.random()) + 2)}
        };
    }

    @Override
    public RGBColor getMapColor() {
        return RGBColor.airColor;
    }
}
