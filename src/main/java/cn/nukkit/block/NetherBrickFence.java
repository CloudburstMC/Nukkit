package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class NetherBrickFence extends Fence {

    public NetherBrickFence() {
        this(0);
    }

    public NetherBrickFence(int meta) {
        super(meta);
    }

    @Override
    public double getBreakTime(Item item) {
        if (item.getId() == 0) {
            return 10;
        } else {
            return super.getBreakTime(item);
        }
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Nether Brick Fence";
    }

    @Override
    public int getId() {
        return NETHER_BRICK_FENCE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_WOODEN) {
            return new int[][]{
                    {this.getId(), 0, 1}
            };
        } else {
            return new int[0][];
        }
    }

    @Override
    public boolean canConnect(Block block) {
        return (block instanceof NetherBrickFence || block instanceof FenceGate) || block.isSolid() && !block.isTransparent();
    }

    @Override
    public Color getColor() {
        return Color.netherrackColor;
    }

}
