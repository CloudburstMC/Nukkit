package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockDriedKelpBlock extends Block {

    @Override
    public String getName() {
        return "Dried Kelp Block";
    }

    @Override
    public int getId() {
        return DRIED_KELP_BLOCK;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    public double getHardness() {
        return 0.5;
    }

    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getBurnAbility() {
        return 30;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
}
