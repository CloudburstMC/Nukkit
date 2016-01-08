package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class SoulSand extends Solid {

    public SoulSand() {
        this(0);
    }

    public SoulSand(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Soul Sand";
    }

    @Override
    public int getId() {
        return SOUL_SAND;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 1 - 0.125,
                this.z + 1
        );
    }

}
