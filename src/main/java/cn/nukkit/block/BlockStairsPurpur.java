package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockStairsPurpur extends BlockStairs {

    public BlockStairsPurpur(int id, int meta) {
        super(id, meta);
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Purpur Stairs";
    }
}
