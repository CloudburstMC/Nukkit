package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDaylightDetectorInverted extends BlockDaylightDetector {

    public BlockDaylightDetectorInverted() {
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
    public boolean onActivate(Item item, Player player) {
        this.getLevel().setBlock(this, new BlockDaylightDetector());
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockDaylightDetector(), 0);
    }

    protected boolean invertDetect() {
        return true;
    }

}
