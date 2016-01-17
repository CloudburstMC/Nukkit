package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Dyes;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Wool extends Solid {

    public Wool() {
        this(0);
    }

    public Wool(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return Dyes.getUnlocalizedColorName(meta) + " Wool";
    }

    @Override
    public int getId() {
        return WOOL;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHEARS;
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 4;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.getDyeColor(meta);
    }
}
