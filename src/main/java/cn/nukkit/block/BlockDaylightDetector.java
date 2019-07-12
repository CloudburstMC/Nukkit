package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDaylightDetector extends BlockTransparent {

    public BlockDaylightDetector() {
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
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if(super.place(item, block, target, face, fx, fy, fz, player)) {
            getLevel().updateAroundRedstone(new Vector3(x, y, z), null);
            return true;
        } else return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        this.getLevel().setBlock(getFloorX(), getFloorY(), getFloorZ(), new BlockDaylightDetectorInverted(), false, true);
        getLevel().updateAroundRedstone(new Vector3(x, y, z), null);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        if (super.onBreak(item)) {
            getLevel().updateAroundRedstone(new Vector3(x, y, z), null);
            return true;
        }
        return false;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        //return calculatePowerLevel(getLevel().getTime(), getLevel().isRaining(), getLevel().isThundering());
        return 15;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return getStrongPower(face);
    }

    /**
     * Returns the power output of a Daylight Detector based on weather and time.
     * https://minecraft.gamepedia.com/Daylight_Detector#Daylight_Detector
     */
    /*private int calculatePowerLevel(int time, boolean isRaining, boolean isThundering) {
        if (!isRaining && !isThundering) {*/
            /*if (time >= 180 && time < 540) return 8;
            if (time >= 540 && time < 940) return 9;
            if (time >= 940 && time < 1380) return 10;
            if (time >= 1380 && time < 1880) return 11;
            if (time >= 1880 && time < 2460) return 12;
            if (time >= 2460 && time < 3180) return 13;
            if (time >= 3180 && time < 4300) return 14;
            if (time >= 4300 && time < 7720) return 15;
            if (time >= 7720 && time < 8840) return 14;
            if (time >= 8840 && time < 9560) return 13;
            if (time >= 9560 && time < 10140) return 12;
            if (time >= 10140 && time < 10640) return 11;
            if (time >= 10640 && time < 11080) return 10;
            if (time >= 11080 && time < 11480) return 9;
            if (time >= 11480 && time < 11840) return 8;
            if (time >= 11840 && time < 12040) return 7;
            if (time >= 12040 && time < 12240) return 6;
            if (time >= 12240 && time < 12480) return 5;
            if (time >= 12480 && time < 12720) return 4;
            if (time >= 12720 && time < 12940) return 3;
            if (time >= 12940 && time < 13220) return 2;
            if (time >= 13220 && time < 13680) return 1;
            if (time >= 13680 && time < 22340) return 0;
            if (time >= 22340 && time < 22800) return 1;
            if (time >= 22800 && time < 23080) return 2;
            if (time >= 23080 && time < 23300) return 3;
            if (time >= 23300 && time < 23540) return 4;
            if (time >= 23540 && time < 23780) return 5;
            if (time >= 23780 && time < 23960) return 6;
            if (time >= 23960 || time < 180) return 7;*/
            /*
            if (timeIsBetween(time, 13680, 22340) && getLightLevel() <= 5) return 0;
            if (timeIsBetween(time, 22340, 13680) && (getLightLevel() >= 4 && getLightLevel() <= 7)) return 1;
            if (timeIsBetween(time, 22800, 13220) && (getLightLevel() >= 7 && getLightLevel() <= 9)) return 2;
            if (timeIsBetween(time, 23080, 12940) && (getLightLevel() >= 9) && getLightLevel() <= 11) return 3;
            if (timeIsBetween(time, 23300, 12720) && (getLightLevel() >= 11 && getLightLevel() <= 12)) return 4;
            if (timeIsBetween(time, 23540, 12480) && (getLightLevel() >= 12 && getLightLevel() <= 13)) return 5;
            if (timeIsBetween(time, 23780, 12240) && (getLightLevel() >= 13 && getLightLevel() <= 14)) return 6;
            if (timeIsBetween(time, 23960, 12040) && getLightLevel() == 15) return 7;
            if (timeIsBetween(time, , ) && getLightLevel() == 15) return 8;
            */

    /*    } else if (isRaining) {
            return 0; // todo
        } else if (isThundering) {
            return 0; // todo
        }
        return 0;
    }*/

    /**
     * Calculates if the specified time belongs to a time segment defined by two numbers
     * (or equals to the start time of the segment)
     * @param time Time
     * @param a Start of the time segment
     * @param b End the time segment
     * @return true if the time belongs to the segment, false otherwise
     */
    /*private boolean timeIsBetween(int time, int a, int b) {
        if (a > 24000 || b > 24000 || a < 0 || b < 0) return false;
        if (b > a) return time >= a && time < b;
        else if (a > b) return (time >= a && time < 24000) || (time >= 0 && time < b);
        else return time == a;
    }*/

}