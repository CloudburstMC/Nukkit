package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsDarkOak extends BlockStairsWood {

    public BlockStairsDarkOak() {
        this(0);
    }

    public BlockStairsDarkOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARK_OAK_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Dark Oak Wood Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

}
