package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.Vector3;

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
    public Item toItem() {
        return new ItemBlock(new BlockDaylightDetector(), 0);
    }

    @Override
    public boolean isInverted() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockDaylightDetector block = new BlockDaylightDetector();
        this.getLevel().setBlock(getFloorX(), getFloorY(), getFloorZ(), block, false, true);
        block.updatePower();
        getLevel().updateAroundRedstone(new Vector3(x, y, z), null);
        return true;
    }

}