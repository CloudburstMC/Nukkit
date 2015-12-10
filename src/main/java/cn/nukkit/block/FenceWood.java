package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class FenceWood extends Fence {
    public static final int FENCE_OAK = 0;
    public static final int FENCE_SPRUCE = 1;
    public static final int FENCE_BIRCH = 2;
    public static final int FENCE_JUNGLE = 3;
    public static final int FENCE_ACACIA = 4;
    public static final int FENCE_DARK_OAK = 5;

    public FenceWood() {
        this(0);
    }

    public FenceWood(int meta) {
        super(meta);
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

    @Override
    public int getId() {
        return FENCE;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), this.meta, 1}
        };
    }

    public boolean canConnect(Block block) {
        //block instanceof FenceGateWood?
        return (block instanceof FenceWood || block instanceof FenceGate) || block.isSolid() && !block.isTransparent();
    }
}
