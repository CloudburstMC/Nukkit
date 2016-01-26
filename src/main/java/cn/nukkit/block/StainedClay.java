package cn.nukkit.block;

import cn.nukkit.item.Dye;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class StainedClay extends Solid {

    public StainedClay() {
        this(0);
    }

    public StainedClay(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return Dye.getColorName(meta) + " Stained Clay";
    }

    @Override
    public int getId() {
        return STAINED_CLAY;
    }

    @Override
    public double getHardness() {
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.getDyeColor(meta);
    }

}
