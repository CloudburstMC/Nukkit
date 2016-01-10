package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class DaylightDetectorInverted extends Transparent {
    public DaylightDetectorInverted() {
        this(0);
    }

    public DaylightDetectorInverted(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DAYLIGHT_DETECTOR_INVERTED;
    }

    @Override
    public String getName() {
        return "Daylight Detector Inverted";
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.DAYLIGHT_DETECTOR_INVERTED}};
    }
}
