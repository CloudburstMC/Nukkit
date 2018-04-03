package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockGrassPath extends BlockGrass {

    public BlockGrassPath() {
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
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9375;
    }

    @Override
    public double getResistance() {
        return 3.25;
    }

    @Override
    public BlockColor getColor() {
        //todo edit this after minecraft pc 1.9 come out
        return BlockColor.GRASS_BLOCK_COLOR;
    }

}
