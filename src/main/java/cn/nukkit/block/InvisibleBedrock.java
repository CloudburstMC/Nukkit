package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.RGBColor;

/**
 * Created by Pub4Game on 03.01.2016.
 */
public class InvisibleBedrock extends Solid {

    public InvisibleBedrock() {
        this(0);
    }

    public InvisibleBedrock(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return INVISIBLE_BEDROCK;
    }

    @Override
    public String getName() {
        return "Invisible Bedrock";
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public RGBColor getMapColor() {
        if (this.getFloorY() == 0) return RGBColor.voidColor;
        Block down = getSide(Vector3.SIDE_DOWN);
        return down.getMapColor();
    }

}
