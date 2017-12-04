package cn.nukkit.server.block;

import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.utils.BlockColor;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockFence extends BlockTransparent {

    public static final int FENCE_OAK = 0;
    public static final int FENCE_SPRUCE = 1;
    public static final int FENCE_BIRCH = 2;
    public static final int FENCE_JUNGLE = 3;
    public static final int FENCE_ACACIA = 4;
    public static final int FENCE_DARK_OAK = 5;

    public BlockFence() {
        this(0);
    }

    public BlockFence(int meta) {
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
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
        boolean north = this.canConnect(this.north());
        boolean south = this.canConnect(this.south());
        boolean west = this.canConnect(this.west());
        boolean east = this.canConnect(this.east());
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

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    public boolean canConnect(Block block) {
        return (block instanceof BlockFence || block instanceof BlockFenceGate) || block.isSolid() && !block.isTransparent();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
