package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class DaylightDetector extends Transparent {

    public DaylightDetector() {
        this(0);
    }

    public DaylightDetector(int meta) {
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
    public Color getMapColor() {
        return Color.woodColor;
    }

    //这个函数提供一个结构的建议，可重命名也可删
    protected boolean invertDetect() {
        return false;
    }

    //todo redstone

}
