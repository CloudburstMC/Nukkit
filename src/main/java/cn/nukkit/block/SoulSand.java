package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.Color;

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
    public double getResistance() {
        return 2.5;
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

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.motionX *= 0.4d;
        entity.motionZ *= 0.4d;
    }

    @Override
    public Color getMapColor() {
        return Color.sandColor;
    }

}
