package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDaylightDetector extends BlockTransparent {

    public BlockDaylightDetector() {
        this(0);
    }

    public BlockDaylightDetector(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DAYLIGHT_DETECTOR;
    }

    @Override
    public String getName() {
        return "Daylight Detector";
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.DAYLIGHT_DETECTOR, 0, 1}
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    //这个函数提供一个结构的建议，可重命名也可删
    protected boolean invertDetect() {
        return false;
    }

    //todo redstone

}
