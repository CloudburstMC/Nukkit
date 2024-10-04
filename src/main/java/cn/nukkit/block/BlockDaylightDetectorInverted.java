package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDaylightDetectorInverted extends BlockDaylightDetector {

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
        this.getLevel().setBlock(this, Block.get(DAYLIGHT_DETECTOR));
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(DAYLIGHT_DETECTOR), 0);
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        int time = level.getTime() % Level.TIME_FULL;
        return time < 13184 || time > 22800 ? 0 : 15;
    }
}
