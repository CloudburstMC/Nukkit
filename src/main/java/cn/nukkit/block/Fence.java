package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Fence extends Transparent {

    public static final int FENCE_OAK = 0;
    public static final int FENCE_SPRUCE = 1;
    public static final int FENCE_BIRCH = 2;
    public static final int FENCE_JUNGLE = 3;
    public static final int FENCE_ACACIA = 4;
    public static final int FENCE_DARK_OAK = 5;

    public Fence() {
        this(0);
    }

    public Fence(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Oak Fence",
                "Spruce Fence",
                "Birch Fence",
                "Jungle Fence",
                "Acacia Fence",
                "Dark Oak Fence",
                "",
                ""
        };
        return names[this.meta & 0x07];
    }

    protected AxisAlignedBB recalculateBoundingBox() {
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

    public boolean canConnect(Block block) {
        return (block instanceof Fence || block instanceof FenceGate) || block.isSolid() && !block.isTransparent();
    }
}
