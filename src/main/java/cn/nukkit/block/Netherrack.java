package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.RGBColor;

/**
 * Created on 2015/12/26 by Pub4Game.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Netherrack extends Solid {

    public Netherrack() {
        this(0);
    }

    public Netherrack(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return NETHERRACK;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Netherrack";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{
                    {Item.NETHERRACK, 0, 1}
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
