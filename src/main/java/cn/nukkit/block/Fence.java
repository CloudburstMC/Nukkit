package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class Fence extends Transparent {

    protected Fence(int meta) {
        super(meta);
    }

    protected AxisAlignedBB recalculateBoundingBox(){
        boolean north = this.canConnect(this.getSide(Vector3.SIDE_NORTH));
        boolean south = this.canConnect(this.getSide(Vector3.SIDE_SOUTH));
        boolean west = this.canConnect(this.getSide(Vector3.SIDE_WEST));
        boolean east = this.canConnect(this.getSide(Vector3.SIDE_EAST));
        double n = north ? 0 : 0.375;
        double s = south ? 1 : 0.625;
        double w = west ? 0 : 0.375;
        double e = east ? 1 : 0.625;
        return new AxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    protected boolean canConnect(Block block){
        return (block instanceof Fence || block instanceof FenceGate) || block.isSolid() && !block.isTransparent();
    }
}
