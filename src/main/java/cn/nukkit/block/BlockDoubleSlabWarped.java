package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabWarped extends BlockDoubleSlabBase {

    public BlockDoubleSlabWarped() {
        super(0);
    }

    public BlockDoubleSlabWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_DOUBLE_SLAB;
    }

    @Override
    public String getSlabName() {
        return "Warped";
    }

    @Override
    public int getSingleSlabId() {
        return WARPED_SLAB;
    }

    @Override
    public int getItemDamage() {
        return 0;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}
