package cn.nukkit.block;

import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceNetherBrick extends BlockFence {

    public BlockFenceNetherBrick() {
        this(0);
    }

    public BlockFenceNetherBrick(int meta) {
        super(meta);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canConnect(Block block) {
        return (block instanceof BlockFenceNetherBrick || block instanceof BlockFenceGate) || block.isSolid() && !block.isTransparent();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Since("1.2.1.0-PN")
    @Override
    public int getBurnChance() {
        return 0;
    }

    @Since("1.2.1.0-PN")
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
