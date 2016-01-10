package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

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
        return new int[][]{{Item.DAYLIGHT_DETECTOR, 0, 1}};
    }
}
